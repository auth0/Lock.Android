/*
 * Telemetry.java
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

package com.auth0.util;

import android.util.Base64;
import android.util.Log;

import com.auth0.android.BuildConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class Telemetry {

    private static final String TAG = Telemetry.class.getName();
    private final String name;
    private final String version;
    private final String libraryVersion;
    private final Map<String, Object> extra;

    public Telemetry(String name, String version) {
        this(name, version, null, null);
    }

    public Telemetry(String name, String version, String libraryVersion, Map<String, Object> extra) {
        this.name = name;
        this.version = version;
        this.libraryVersion = libraryVersion;
        this.extra = extra;
    }

    public String asBase64() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", getName("Lock.Android"));
        info.put("version", getVersion(BuildConfig.VERSION_NAME));
        if (isNonEmpty(this.libraryVersion)) {
            info.put("lib_version", this.libraryVersion);
        }
        if (this.extra != null) {
            info.putAll(this.extra);
        }
        String clientInfo = null;
        try {
            String json = new ObjectMapper().writeValueAsString(info);
            Log.v(TAG, "Telemetry JSON is " + json);
            clientInfo = Base64.encodeToString(json.getBytes(Charset.defaultCharset()), Base64.URL_SAFE | Base64.NO_WRAP);
        } catch (JsonProcessingException e) {
            Log.w(TAG, "Failed to build client info", e);
        }
        return clientInfo;
    }

    private String getName(String defaultName) {
        return isNonEmpty(this.name) ? this.name : defaultName;
    }

    private String getVersion(String defaultVersion) {
        return isNonEmpty(this.version) ? this.version: defaultVersion;
    }

    private boolean isNonEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
