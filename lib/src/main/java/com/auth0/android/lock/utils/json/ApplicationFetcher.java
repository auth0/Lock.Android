/*
 * ApplicationFetcher.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
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

package com.auth0.android.lock.utils.json;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.AuthenticationCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

public class ApplicationFetcher {

    private static final String JSONP_PREFIX = "Auth0.setClient(";
    private static final String TAG = ApplicationFetcher.class.getSimpleName();

    private final Auth0 account;
    private final OkHttpClient client;

    /**
     * Helper class to fetch the Application from Auth0 Dashboard.
     *
     * @param account credentials to use against the Auth0 API.
     */
    public ApplicationFetcher(@NonNull Auth0 account, @NonNull OkHttpClient client) {
        this.account = account;
        this.client = client;
    }

    /**
     * Fetch application information from Auth0
     *
     * @param callback to notify on success/error
     */
    public void fetch(@NonNull AuthenticationCallback<List<Connection>> callback) {
        makeApplicationRequest(callback);
    }

    private void makeApplicationRequest(final AuthenticationCallback<List<Connection>> callback) {
        Uri uri = Uri.parse(account.getConfigurationUrl()).buildUpon().appendPath("client")
                .appendPath(account.getClientId() + ".js").build();

        Request req = new Request.Builder()
                .url(uri.toString())
                .build();

        client.newCall(req).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, final IOException e) {
                Log.e(TAG, "Failed to fetch the Application: " + e.getMessage(), e);
                Auth0Exception exception = new Auth0Exception("Failed to fetch the Application: " + e.getMessage());
                callback.onFailure(new AuthenticationException("Failed to fetch the Application", exception));
            }

            @Override
            public void onResponse(Response response) {
                List<Connection> connections;
                try {
                    connections= parseJSONP(response);
                } catch (Auth0Exception e) {
                    Log.e(TAG, "Could not parse Application JSONP: " + e.getMessage());
                    callback.onFailure(new AuthenticationException("Could not parse Application JSONP", e));
                    return;
                }

                Log.i(TAG, "Application received!");
                callback.onSuccess(connections);
            }
        });
    }

    private List<Connection> parseJSONP(Response response) throws Auth0Exception {
        try {
            String json = response.body().string();
            final int length = JSONP_PREFIX.length();
            if (json.length() < length) {
                throw new JSONException("Invalid App Info JSONP");
            }
            json = json.substring(length);
            JSONTokener tokenizer = new JSONTokener(json);
            if (!tokenizer.more()) {
                throw tokenizer.syntaxError("Invalid App Info JSONP");
            }
            Object nextValue = tokenizer.nextValue();
            if (!(nextValue instanceof JSONObject)) {
                tokenizer.back();
                throw tokenizer.syntaxError("Invalid JSON value of App Info");
            }
            JSONObject jsonObject = (JSONObject) nextValue;
            Type applicationType = new TypeToken<List<Connection>>() {}.getType();
            return createGson().fromJson(jsonObject.toString(), applicationType);
        } catch (IOException | JSONException e) {
            throw new Auth0Exception("Failed to parse response to request", e);
        }
    }

    static Gson createGson() {
        Type applicationType = new TypeToken<List<Connection>>() {}.getType();
        Type strategyType = new TypeToken<Strategy>(){}.getType();
        return new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(applicationType, new ApplicationDeserializer())
                .registerTypeAdapter(strategyType, new StrategyDeserializer())
                .create();
    }
}
