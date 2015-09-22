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

import android.util.Log;

import com.auth0.api.callback.BaseCallback;
import com.auth0.api.okhttp.ApplicationInfoCallback;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;

/**
 * API client for Auth0 Authentication API.
 * @see <a href="https://auth0.com/docs/auth-api">Auth API docs</a>
 */
public class AuthenticationAPIClient {

    private static final String TAG = AuthenticationAPIClient.class.getName();

    private final Auth0 auth0;
    private final OkHttpClient client;

    private Application application;

    /**
     * Creates a new API client instance providing Auth0 account info.
     * @param auth0 account information
     */
    public AuthenticationAPIClient(Auth0 auth0) {
        this.auth0 = auth0;
        this.client = new OkHttpClient();
    }

    /**
     * Creates a new API client instance providing Auth API and Configuration Urls different than the default. (Useful for on premise deploys).
     * @param clientID Your application clientID.
     * @param baseURL Auth0's auth API endpoint
     * @param configurationURL Auth0's enpoint where App info can be retrieved.
     */
    public AuthenticationAPIClient(String clientID, String baseURL, String configurationURL) {
        this(new Auth0(clientID, baseURL, configurationURL));
    }

    public String getClientId() {
        return auth0.getClientId();
    }

    public String getBaseURL() {
        return auth0.getDomainUrl();
    }

    /**
     * Returns the Auth0 app info retrieved from Auth0
     * @return an instance of {@link com.auth0.core.Application} or null.
     */
    public Application getApplication() {
        return application;
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
        client.newCall(request).enqueue(new ApplicationInfoCallback(new ObjectMapper()) {
            @Override
            public void onSuccess(Application app) {
                Log.d(TAG, "Obtained application with id " + app.getId() + " tenant " + app.getTenant());
                callback.onSuccess(app);
                application = app;
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "Failed to fetch application from Auth0 CDN", error);
                callback.onFailure(error);
            }
        });
    }

}
