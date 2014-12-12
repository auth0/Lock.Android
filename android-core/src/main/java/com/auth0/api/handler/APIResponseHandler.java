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

import org.apache.http.Header;

import java.io.IOException;
import java.util.Map;

/**
 * Created by hernan on 12/12/14.
 */
public abstract class APIResponseHandler<T extends Callback> extends AsyncHttpResponseHandler {

    protected T callback;

    protected APIResponseHandler(T callback) {
        this.callback = callback;
    }

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
