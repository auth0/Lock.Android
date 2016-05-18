/*
 * AuthenticationRequest.java
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

import android.os.Handler;
import android.util.Log;

import com.auth0.api.APIClientException;
import com.auth0.api.RequestBodyBuildException;
import com.auth0.api.ParameterizableRequest;
import com.auth0.api.Request;
import com.auth0.util.moshi.MoshiObjectMapper;
import com.auth0.util.moshi.MoshiObjectReader;
import com.auth0.util.moshi.MoshiObjectWriter;
import com.auth0.util.moshi.MapOfObjects;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.io.InputStream;

class SimpleRequest<T> extends BaseRequest<T> implements Request<T>, ParameterizableRequest<T>, Callback {

    private static final String TAG = SimpleRequest.class.getName();

    private final MoshiObjectReader<MapOfObjects> errorReader;
    private final String method;

    public SimpleRequest(Handler handler, HttpUrl url, OkHttpClient client, MoshiObjectMapper mapper, String httpMethod, Class<T> clazz) {
        super(handler, url, client, new MoshiObjectReader(mapper,clazz), new MoshiObjectWriter(mapper));
        this.errorReader = new MoshiObjectReader(mapper, MapOfObjects.class);
        this.method = httpMethod;
    }

    public SimpleRequest(Handler handler, HttpUrl url, OkHttpClient client, MoshiObjectMapper mapper, String httpMethod) {
        super(handler, url, client, new MoshiObjectReader(mapper,MapOfObjects.class),new MoshiObjectWriter(mapper));
        this.errorReader = new MoshiObjectReader(mapper,MapOfObjects.class);
        this.method = httpMethod;
    }

    @Override
    public void onResponse(Response response) throws IOException {
        Log.d(TAG, String.format("Received response from request to %s with status code %d", response.request().urlString(), response.code()));
        final InputStream byteStream = response.body().byteStream();
        if (!response.isSuccessful()) {
            Throwable throwable;
            try {
                MapOfObjects payload = errorReader.readValue(byteStream);
                throwable = new APIClientException("Request failed with response " + payload, response.code(), payload.map);
            } catch (Exception e) {
                throwable = new APIClientException("Request failed", response.code(), null);
            }
            postOnFailure(throwable);
            return;
        }

        try {
            Log.d(TAG, "Received successful response from " + response.request().urlString());
            T payload = getReader().readValue(byteStream);
            postOnSuccess(payload);
        } catch (Exception e) {
            postOnFailure(new APIClientException("Request failed", response.code(), null));
        }
    }

    @Override
    protected com.squareup.okhttp.Request doBuildRequest(com.squareup.okhttp.Request.Builder builder) throws RequestBodyBuildException {
        RequestBody body = buildBody();
        return newBuilder()
                .method(method, body)
                .build();
    }
}
