/*
 * AuthenticationResponseCallback.java
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

package com.auth0.api.okhttp;

import android.os.Handler;
import android.util.Log;

import com.auth0.api.APIClientException;
import com.auth0.api.JsonEntityBuildException;
import com.auth0.api.callback.BaseCallback;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class AuthenticationResponseCallback<T> extends CallbackHandler<T> implements Callback {

    private static final String TAG = AuthenticationResponseCallback.class.getName();
    private final ObjectReader reader;
    private final ObjectReader errorReader;

    public AuthenticationResponseCallback(Handler handler, BaseCallback<T> callback, ObjectReader reader) {
        super(handler, callback);
        this.reader = reader;
        this.errorReader = new ObjectMapper().reader(Map.class);
    }

    @Override
    public void onFailure(Request request, IOException e) {
        Log.e(TAG, "Failed to make request to " + request.urlString(), e);
        postOnFailure(e);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        Log.d(TAG, String.format("Received response from request to %s with status code %d", response.request().urlString(), response.code()));
        final InputStream byteStream = response.body().byteStream();
        if (!response.isSuccessful()) {
            Throwable throwable;
            try {
                Map<String, Object> payload = errorReader.readValue(byteStream);
                throwable = new APIClientException("Failed request to " + response.request().urlString(), response.code(), payload);
            } catch (IOException e) {
                throwable = new APIClientException("Request failed", response.code(), null);
            }
            postOnFailure(throwable);
            return;
        }

        try {
            Log.d(TAG, "Received successful response from " + response.request().urlString());
            T payload = reader.readValue(byteStream);
            postOnSuccess(payload);
        } catch (IOException e) {
            postOnFailure(new APIClientException("Request failed", response.code(), null));
        }
    }
}
