/*
 * BaseAPIResponseHandler.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.api.handler;

import android.util.Log;

import com.auth0.api.APIClientException;
import com.auth0.api.callback.Callback;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

/**
 * Default Response handler for all request performed with android-async-http client.
 * It calls a {@link com.auth0.api.callback.Callback} on success/failure.
 */
public abstract class APIResponseHandler<T extends Callback> extends AsyncHttpResponseHandler {

    /**
     * an instance of {@link com.auth0.api.callback.Callback}
     */
    protected T callback;

    /**
     * Creates an instance with the callback to be called on success/failure
     * @param callback
     */
    protected APIResponseHandler(T callback) {
        this.callback = callback;
    }

    /**
     * @see com.loopj.android.http.AsyncHttpResponseHandler#onFailure(int, cz.msebera.android.httpclient.Header[], byte[], Throwable)
     */
    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        Log.e(this.getClass().getName(), "Operation failed", error);
        Map errorResponse = null;
        if (statusCode == 400 || statusCode == 401) {
            try {
                errorResponse = new ObjectMapper().readValue(responseBody, Map.class);
                Log.e(this.getClass().getName(), "Response error " + errorResponse);
            } catch (IOException e) {
                Log.w(this.getClass().getName(), "Failed to parse json error response", error);
            }
        }
        callback.onFailure(new APIClientException("Failed to perform operation", error, statusCode, errorResponse));
    }

}
