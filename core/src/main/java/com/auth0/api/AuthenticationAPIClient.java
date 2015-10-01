/*
 * AuthenticationClient.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.api;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.auth0.api.okhttp.RequestFactory;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;

import java.util.Map;

import static com.auth0.api.ParameterBuilder.GRANT_TYPE_PASSWORD;

/**
 * API client for Auth0 Authentication API.
 * @see <a href="https://auth0.com/docs/auth-api">Auth API docs</a>
 */
public class AuthenticationAPIClient {

    private static final String TAG = AuthenticationAPIClient.class.getName();

    private final Auth0 auth0;
    private final OkHttpClient client;
    private final Handler handler;
    private final ObjectMapper mapper;

    /**
     * Creates a new API client instance providing Auth0 account info.
     * @param auth0 account information
     */
    public AuthenticationAPIClient(Auth0 auth0) {
        this(auth0, new Handler(Looper.getMainLooper()));
    }

    /**
     * Creates a new API client instance providing Auth0 account info and a handler where all callbacks will be called
     * @param auth0 account information
     * @param handler where callback will be called with either the response or error from the server
     */
    public AuthenticationAPIClient(Auth0 auth0, Handler handler) {
        this(auth0, new OkHttpClient(), handler, new ObjectMapper());
    }

    /**
     * Creates a new API client instance providing Auth API and Configuration Urls different than the default. (Useful for on premise deploys).
     * @param clientID Your application clientID.
     * @param baseURL Auth0's auth API endpoint
     * @param configurationURL Auth0's enpoint where App info can be retrieved.
     */
    @SuppressWarnings("unused")
    public AuthenticationAPIClient(String clientID, String baseURL, String configurationURL) {
        this(new Auth0(clientID, baseURL, configurationURL));
    }

    AuthenticationAPIClient(Auth0 auth0, OkHttpClient client, Handler handler, ObjectMapper mapper) {
        this.auth0 = auth0;
        this.client = client;
        this.handler = handler;
        this.mapper = mapper;
    }

    public String getClientId() {
        return auth0.getClientId();
    }

    public String getBaseURL() {
        return auth0.getDomainUrl();
    }

    /**
     * Fetch application information from Auth0
     * @return a Auth0 request to start
     */
    public Request<Application> fetchApplicationInfo() {
        HttpUrl url = HttpUrl.parse(auth0.getConfigurationUrl()).newBuilder()
                .addPathSegment("client")
                .addPathSegment(auth0.getClientId() + ".js")
                .build();
        return RequestFactory.newApplicationInfoRequest(url, client, handler, mapper);
    }

    public ParameterizableRequest<Token> loginWithResourceOwner() {
        HttpUrl url = HttpUrl.parse(auth0.getDomainUrl()).newBuilder()
                .addPathSegment("oauth")
                .addPathSegment("ro")
                .build();
        Map<String, Object> requestParameters = new ParameterBuilder()
                .setClientId(getClientId())
                .asDictionary();
        ParameterizableRequest<Token> request = RequestFactory.POST(url, client, handler, mapper, Token.class)
                .setParameters(requestParameters);
        Log.d(TAG, "Trying to login using " + url.toString() + " with parameters " + requestParameters);
        return request;
    }

    public AuthenticationRequest login(String usernameOrEmail, String password) {
        Map<String, Object> requestParameters = new ParameterBuilder()
                .set("username", usernameOrEmail)
                .set("password", password)
                .setGrantType(GRANT_TYPE_PASSWORD)
                .asDictionary();
        final ParameterizableRequest<Token> credentialsRequest = loginWithResourceOwner()
                .setParameters(requestParameters);
        final ParameterizableRequest<UserProfile> profileRequest = profileRequest();

        return new AuthenticationRequest(credentialsRequest, profileRequest);
    }

    public Request<UserProfile> tokenInfo(String idToken) {
        Map<String, Object> requestParameters = new ParameterBuilder()
                .clearAll()
                .set("id_token", idToken)
                .asDictionary();
        Log.d(TAG, "Trying to fetch token with parameters " + requestParameters);
        return profileRequest()
                .setParameters(requestParameters);
    }

    private ParameterizableRequest<UserProfile> profileRequest() {
        HttpUrl url = HttpUrl.parse(auth0.getDomainUrl()).newBuilder()
                .addPathSegment("tokeninfo")
                .build();
        return RequestFactory.POST(url, client, handler, mapper, UserProfile.class);

    }
}
