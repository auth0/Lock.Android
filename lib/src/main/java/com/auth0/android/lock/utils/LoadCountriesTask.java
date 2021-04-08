/*
 * LoadCountriesTask.java
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

package com.auth0.android.lock.utils;

import android.content.Context;
import android.os.AsyncTask;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;

public abstract class LoadCountriesTask extends AsyncTask<String, Void, Map<String, String>> {

    private static final String TAG = LoadCountriesTask.class.getName();
    public static final String COUNTRIES_JSON_FILE = "com_auth0_lock_passwordless_countries.json";

    private final ThreadLocal<Context> context = new ThreadLocal<>();

    public LoadCountriesTask(@NonNull Context context) {
        this.context.set(context);
    }

    @Override
    @NonNull
    protected Map<String, String> doInBackground(@NonNull String... params) {
        Map<String, String> codes = Collections.emptyMap();
        final Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Context ctx = context.get();
        if (ctx == null) {
            return codes;
        }
        try {
            final Reader reader = new InputStreamReader(ctx.getAssets().open(params[0]));
            codes = new Gson().fromJson(reader, mapType);
            Log.d(TAG, String.format("Loaded %d countries", codes.size()));
        } catch (IOException e) {
            Log.e(TAG, "Failed to load the countries list from the JSON file", e);
        }
        return codes;
    }

    @Nullable
    public Context getContext() {
        return context.get();
    }
}