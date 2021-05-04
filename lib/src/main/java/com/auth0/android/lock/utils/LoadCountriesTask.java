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
import android.util.Log;

import androidx.annotation.NonNull;

import com.auth0.android.lock.adapters.Country;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public abstract class LoadCountriesTask extends AsyncTask<Context, Void, List<Country>> {

    private static final String TAG = LoadCountriesTask.class.getName();
    private static final String COUNTRIES_JSON_FILE = "com_auth0_lock_passwordless_countries.json";

    @Override
    @NonNull
    protected List<Country> doInBackground(@NonNull Context... params) {
        final Type mapType = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> codes = Collections.emptyMap();
        try {
            final Reader reader = new InputStreamReader(params[0].getAssets().open(COUNTRIES_JSON_FILE));
            codes = new Gson().fromJson(reader, mapType);
            Log.d(TAG, String.format("Loaded %d countries", codes.size()));
        } catch (IOException e) {
            Log.e(TAG, "Failed to load the countries list from the JSON file", e);
        }
        final ArrayList<String> names = new ArrayList<>(codes.keySet());
        Collections.sort(names);
        List<Country> countries = new ArrayList<>(names.size());
        for (String name : names) {
            countries.add(new Country(name, codes.get(name)));
        }
        return countries;
    }

}