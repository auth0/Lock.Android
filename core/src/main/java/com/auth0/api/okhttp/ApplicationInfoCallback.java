/*
 * ApplicationInfoCallback.java
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

import android.util.Log;

import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

/**
 * Callback and response handler used when requesting Auth0's app info.
 */
public abstract class ApplicationInfoCallback implements Callback, BaseCallback<Application> {

    private static final String TAG = ApplicationInfoCallback.class.getName();

    /**
     * JSON-POJO mapper (Jackson)
     */
    private final ObjectMapper mapper;

    protected ApplicationInfoCallback(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void onFailure(Request request, IOException e) {
        Log.e(TAG, "Failed to fetch Auth0 info from CDN " + request.urlString(), e);
        this.onFailure(e);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if (response.code() < 200 || response.code() >= 300) {
            String message = "Received app info failed response with code " + response.code() + " and body " + response.body().string();
            Log.d(TAG, message);
            this.onFailure(new IOException(message));
            return;
        }
        try {
            String jsonp = response.body().string();
            JSONTokener tokenizer = new JSONTokener(jsonp);
            tokenizer.skipPast("Auth0.setClient(");
            if (!tokenizer.more()) {
                this.onFailure(tokenizer.syntaxError("Invalid App Info JSONP"));
                return;
            }
            Object nextValue = tokenizer.nextValue();
            if (!(nextValue instanceof JSONObject)) {
                tokenizer.back();
                this.onFailure(tokenizer.syntaxError("Invalid JSON value of App Info"));
            }
            JSONObject jsonObject = (JSONObject) nextValue;
            Log.d(TAG, "Obtained JSON object from JSONP: " + jsonObject);
            Application app = this.mapper.readValue(jsonObject.toString(), Application.class);
            this.onSuccess(app);
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Failed to parse JSONP", e);
            this.onFailure(e);
        }
    }
}
