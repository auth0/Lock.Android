/*
 * RequestFactory.java
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

import com.auth0.api.ParameterizableRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;

public class RequestFactory {

    private RequestFactory() {}

    public static <T> ParameterizableRequest<T> GET(HttpUrl url, OkHttpClient client, Handler handler, ObjectMapper mapper, Class<T> clazz) {
        return new SimpleRequest<>(handler, url, client, mapper, "GET", clazz);
    }

    public static <T> ParameterizableRequest<T> POST(HttpUrl url, OkHttpClient client, Handler handler, ObjectMapper mapper, Class<T> clazz) {
        return new SimpleRequest<>(handler, url, client, mapper, "POST", clazz);
    }

    public static <T> ParameterizableRequest<T> PUT(HttpUrl url, OkHttpClient client, Handler handler, ObjectMapper mapper, Class<T> clazz) {
        return new SimpleRequest<>(handler, url, client, mapper, "PUT", clazz);
    }

    public static <T> ParameterizableRequest<T> PATCH(HttpUrl url, OkHttpClient client, Handler handler, ObjectMapper mapper, Class<T> clazz) {
        return new SimpleRequest<>(handler, url, client, mapper, "GET", clazz);
    }

    public static <T> ParameterizableRequest<T> DELETE(HttpUrl url, OkHttpClient client, Handler handler, ObjectMapper mapper, Class<T> clazz) {
        return new SimpleRequest<>(handler, url, client, mapper, "DELETE", clazz);
    }

}
