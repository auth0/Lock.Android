/*
 * ResponseCallbackTask.java
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

package com.auth0.api.internal;

import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.auth0.api.APIClientException;
import com.auth0.api.AuthorizableRequest;
import com.auth0.api.RequestBodyBuildException;
import com.auth0.api.ParameterizableRequest;
import com.auth0.api.callback.BaseCallback;
import com.auth0.util.moshi.MoshiObjectReader;
import com.auth0.util.moshi.MoshiObjectWriter;
import com.auth0.util.moshi.MapOfObjects;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

abstract class BaseRequest<T> implements ParameterizableRequest<T>, AuthorizableRequest<T>, Callback {

    private static final String TAG = BaseCallback.class.getName();

    private final Handler handler;
    private final Map<String, String> headers;
    private final Map<String, Object> parameters;
    private final HttpUrl url;
    private final OkHttpClient client;
    private final MoshiObjectReader<T> reader;
    private final MoshiObjectWriter writer;
    private BaseCallback<T> callback;

    protected BaseRequest(Handler handler, HttpUrl url, OkHttpClient client, MoshiObjectReader reader, MoshiObjectWriter writer) {
        this(handler, url, client, reader, writer, null);
    }

    public BaseRequest(Handler handler, HttpUrl url, OkHttpClient client, MoshiObjectReader reader, MoshiObjectWriter writer, BaseCallback<T> callback) {
        this.handler = handler;
        this.url = url;
        this.client = client;
        this.reader = reader;
        this.writer = writer;
        this.callback = callback;
        this.headers = new HashMap<>();
        this.parameters = new HashMap<>();
        this.headers.put("User-Agent", String.format("Android %s (%s %s;)", Build.VERSION.RELEASE, Build.MODEL, Build.MANUFACTURER));
    }

    protected void setCallback(BaseCallback<T> callback) {
        this.callback = callback;
    }

    protected void postOnSuccess(final T payload) {
        handler.post(new CallbackTask<T>(callback) {
            @Override
            public void run() {
                this.callback.onSuccess(payload);
            }
        });
    }

    protected final void postOnFailure(final Throwable error) {
        Log.e(TAG, "Failed to make request to " + url, error);
        handler.post(new CallbackTask<T>(callback) {
            @Override
            public void run() {
                this.callback.onFailure(error);
            }
        });
    }

    protected Request.Builder newBuilder() {
        final com.squareup.okhttp.Request.Builder builder = new com.squareup.okhttp.Request.Builder()
                .url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            builder.addHeader(entry.getKey(), entry.getValue());
        }
        return builder;
    }

    protected Map<String, Object> getParameters() {
        return parameters;
    }

    protected MoshiObjectReader<T> getReader() {
        return reader;
    }

    protected RequestBody buildBody() throws RequestBodyBuildException {
        return JsonRequestBodyBuilder.createBody(new MapOfObjects(parameters),MapOfObjects.class, writer);
    }

    private static abstract class CallbackTask<T> implements Runnable {
        protected final BaseCallback<T> callback;

        protected CallbackTask(BaseCallback<T> callback) {
            this.callback = callback;
        }
    }

    @Override
    public void onFailure(com.squareup.okhttp.Request request, IOException e) {
        postOnFailure(e);
    }

    @Override
    public ParameterizableRequest<T> addHeader(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public AuthorizableRequest<T> setBearer(String jwt) {
        addHeader("Authorization", "Bearer " + jwt);
        return this;
    }

    @Override
    public ParameterizableRequest<T> addParameters(Map<String, Object> parameters) {
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    @Override
    public void start(BaseCallback<T> callback) {
        setCallback(callback);
        try {
            Request request = doBuildRequest(newBuilder());
            client.newCall(request).enqueue(this);
        } catch (RequestBodyBuildException e) {
            Log.e(TAG, "Failed to build JSON body with parameters " + parameters, e);
            callback.onFailure(new APIClientException("Failed to send request to " + url.toString(), e));
        }
    }

    protected abstract Request doBuildRequest(Request.Builder builder) throws RequestBodyBuildException;
}
