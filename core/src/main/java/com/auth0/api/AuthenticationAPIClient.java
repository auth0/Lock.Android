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

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.api.okhttp.ApplicationInfoCallback;
import com.auth0.api.okhttp.AuthenticationResponseCallback;
import com.auth0.api.okhttp.JsonRequestBodyBuilder;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

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
     * @param callback called with the application info on success or with the failure reason.
     */
    public void fetchApplicationInfo(final BaseCallback<Application> callback) {
        HttpUrl url = HttpUrl.parse(auth0.getConfigurationUrl()).newBuilder()
                .addPathSegment("client")
                .addPathSegment(auth0.getClientId() + ".js")
                .build();
        Log.v(TAG, "Fetching application info from " + url);
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new ApplicationInfoCallback(handler, callback, mapper.reader(Application.class)));
    }

    public void loginWithResourceOwner(Map<String, Object> parameters, BaseCallback<Token> callback) {
        HttpUrl url = HttpUrl.parse(auth0.getDomainUrl()).newBuilder()
                .addPathSegment("oauth")
                .addPathSegment("ro")
                .build();
        Map<String, Object> requestParameters = new ParameterBuilder()
                .setClientId(getClientId())
                .addAll(parameters)
                .asDictionary();
        Log.d(TAG, "Trying to login using " + url.toString() + " with parameters " + requestParameters);
        postRequestTo(url, requestParameters, Token.class, callback);
    }

    public void login(String usernameOrEmail, String password, Map<String, Object> parameters, final AuthenticationCallback callback) {
        Map<String, Object> requestParameters = new ParameterBuilder()
                .set("username", usernameOrEmail)
                .set("password", password)
                .setGrantType(GRANT_TYPE_PASSWORD)
                .addAll(parameters)
                .asDictionary();
        loginWithResourceOwner(requestParameters, new BaseCallback<Token>() {
            @Override
            public void onSuccess(final Token token) {
                tokenInfo(token.getIdToken(), new BaseCallback<UserProfile>() {
                    @Override
                    public void onSuccess(UserProfile profile) {
                        callback.onSuccess(profile, token);
                    }

                    @Override
                    public void onFailure(Throwable error) {
                        callback.onFailure(error);
                    }
                });
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

    public void tokenInfo(String idToken, BaseCallback<UserProfile> callback) {
        HttpUrl url = HttpUrl.parse(auth0.getDomainUrl()).newBuilder()
                .addPathSegment("tokeninfo")
                .build();
        Map<String, Object> requestParameters = new ParameterBuilder()
                .clearAll()
                .set("id_token", idToken)
                .asDictionary();
        Log.d(TAG, "Trying to fetch token from" + url.toString() + " with parameters " + requestParameters);
        postRequestTo(url, requestParameters, UserProfile.class, callback);
    }

    private <T> void postRequestTo(HttpUrl url, Map<String, Object> payload, Class<T> responseType, BaseCallback<T> callback) {
        try {
            RequestBody body = JsonRequestBodyBuilder.createBody(payload, mapper.writer());
            Request request = new Request.Builder()
                    .url(url)
                    .post(body)
                    .build();
            client.newCall(request).enqueue(new AuthenticationResponseCallback<>(handler, callback, mapper.reader(responseType)));
        } catch (JsonEntityBuildException e) {
            Log.e(TAG, "Failed to build JSON body with parameters " + payload, e);
            callback.onFailure(new APIClientException("Failed to send request to " + url.toString(), e));
        }
    }
}
