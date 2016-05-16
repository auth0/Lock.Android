/*
 * LoadCountriesTask.java
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

package com.auth0.lock.passwordless.task;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;


import com.auth0.util.moshi.MoshiObjectMapper;
import com.auth0.util.moshi.MapOfStrings;

import java.io.IOException;
import java.util.Map;

public abstract class LoadCountriesTask extends AsyncTask<String, Void, Map<String, String>> {

    public static final String TAG = LoadCountriesTask.class.getName();
    public static final String COUNTRIES_JSON_FILE = "com_auth0_passwordless_countries.json";

    private final Context context;

    public LoadCountriesTask(Context context) {
        this.context = context;
    }

    @Override
    protected Map<String, String> doInBackground(String... params) {

        MapOfStrings codes;
        try {
            codes = new MoshiObjectMapper().readValue(context.getAssets().open(params[0]), MapOfStrings.class);
            Log.d(TAG, "Loaded " + codes.map.size() + " countries");
        } catch (IOException e) {
            codes = new MapOfStrings();
            Log.e(TAG, "Failed to load countries JSON file", e);
        }
        return codes.map;
    }

    public Context getContext() {
        return context;
    }
}
