/*
 * BaseAPIClient.java
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

import android.net.Uri;
import android.os.Build;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.loopj.android.http.AsyncHttpClient;

/**
 * Created by hernan on 1/14/15.
 */
public abstract class BaseAPIClient {

    public static final String BASE_URL_FORMAT = "https://%s.auth0.com";
    public static final String APP_INFO_CDN_URL_FORMAT = "https://cdn.auth0.com/client/%s.js";
    static final String APPLICATION_JSON = "application/json";

    private final String clientID;
    private final String configurationURL;
    private final String baseURL;
    private final String tenantName;
    final JsonEntityBuilder entityBuilder;
    final AsyncHttpClient client;

    public BaseAPIClient(String clientID, String baseURL, String configurationURL, String tenantName) {
        this.clientID = clientID;
        this.configurationURL = configurationURL;
        this.baseURL = baseURL;
        this.client = new AsyncHttpClient();
        if (tenantName == null) {
            Uri uri = Uri.parse(baseURL);
            this.tenantName = uri.getHost();
        } else {
            this.tenantName = tenantName;
        }
        this.client.setUserAgent(String.format("%s (%s Android %s)", tenantName, Build.MODEL, Build.VERSION.RELEASE));
        this.entityBuilder = new JsonEntityBuilder(new ObjectMapper());
    }

    public BaseAPIClient(String clientID, String baseURL, String configurationURL) {
        this(clientID, baseURL, configurationURL, null);
    }

    public BaseAPIClient(String clientID, String tenantName) {
        this(clientID, String.format(BASE_URL_FORMAT, tenantName), String.format(APP_INFO_CDN_URL_FORMAT, clientID), tenantName);
    }

    public String getClientID() {
        return clientID;
    }

    public String getTenantName() {
        return tenantName;
    }

    public String getBaseURL() {
        return baseURL;
    }

    public String getConfigurationURL() {
        return configurationURL;
    }

}
