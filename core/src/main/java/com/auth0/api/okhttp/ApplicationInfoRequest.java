/*
 * ApplicationInfoRequest.java
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

import com.auth0.api.Request;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

public class ApplicationInfoRequest extends CallbackHandler<Application> implements Request<Application>, Callback {

    private static final String TAG = ApplicationInfoRequest.class.getName();

    private final HttpUrl url;
    private final OkHttpClient client;
    private final ObjectReader reader;

    public ApplicationInfoRequest(Handler handler, OkHttpClient client, HttpUrl url, ObjectMapper mapper) {
        super(handler);
        this.client = client;
        this.url = url;
        this.reader = mapper.reader(Application.class);
    }

    @Override
    public void start(BaseCallback<Application> callback) {
        setCallback(callback);
        Log.v(TAG, "Fetching application info from " + url);
        com.squareup.okhttp.Request request = new com.squareup.okhttp.Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(this);
    }

    @Override
    public void onFailure(com.squareup.okhttp.Request request, IOException e) {
        Log.e(TAG, "Failed to fetch Auth0 info from CDN " + request.urlString(), e);
        postOnFailure(e);
    }

    @Override
    public void onResponse(Response response) throws IOException {
        if (!response.isSuccessful()) {
            String message = "Received app info failed response with code " + response.code() + " and body " + response.body().string();
            Log.d(TAG, message);
            postOnFailure(new IOException(message));
            return;
        }
        try {
            String json = response.body().string();
            JSONTokener tokenizer = new JSONTokener(json);
            tokenizer.skipPast("Auth0.setClient(");
            if (!tokenizer.more()) {
                postOnFailure(tokenizer.syntaxError("Invalid App Info JSONP"));
                return;
            }
            Object nextValue = tokenizer.nextValue();
            if (!(nextValue instanceof JSONObject)) {
                tokenizer.back();
                postOnFailure(tokenizer.syntaxError("Invalid JSON value of App Info"));
            }
            JSONObject jsonObject = (JSONObject) nextValue;
            Log.d(TAG, "Obtained JSON object from JSONP: " + jsonObject);
            Application app = reader.readValue(jsonObject.toString());
            postOnSuccess(app);
        } catch (JSONException | IOException e) {
            Log.e(TAG, "Failed to parse JSONP", e);
            postOnFailure(e);
        }
    }
}
