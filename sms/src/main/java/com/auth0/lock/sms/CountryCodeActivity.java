/*
 * CountryCodeActivity.java
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

package com.auth0.lock.sms;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.auth0.lock.sms.adapter.Country;
import com.auth0.lock.sms.adapter.CountryAdapter;
import com.auth0.lock.sms.task.LoadCountriesTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CountryCodeActivity extends ActionBarActivity {

    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String COUNTRY_DIAL_CODE = "COUNTRY_DIAL_CODE";

    private static final String TAG = CountryCodeActivity.class.getName();

    AsyncTask<String, Void, Map<String, String>> task;

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_country_code);

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setIcon(android.R.color.transparent);
            bar.setDisplayShowTitleEnabled(false);
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowCustomEnabled(true);
            bar.setCustomView(R.layout.bar_country_search);
            final EditText searchText = (EditText) bar.getCustomView().findViewById(R.id.sms_search_country);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    CountryAdapter adapter = (CountryAdapter) listView.getAdapter();
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                    Log.v(TAG, "Filtering with string (" + s + ")");
                }
            });
        }

        task = new LoadCountriesTask(this) {
            @Override
            protected void onPostExecute(Map<String, String> result) {
                task = null;
                if (result != null) {
                    final ArrayList<String> names = new ArrayList<>(result.keySet());
                    Collections.sort(names);
                    List<Country> countries = new ArrayList<>(names.size());
                    for (String name : names) {
                        countries.add(new Country(name, result.get(name)));
                    }
                    listView.setAdapter(new CountryAdapter(getContext(), countries));
                }
            }
        };
        task.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
        listView = (ListView) findViewById(R.id.sms_country_code_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country) parent.getItemAtPosition(position);
                Intent data = new Intent();
                data.putExtra(COUNTRY_CODE, country.getIsoCode());
                data.putExtra(COUNTRY_DIAL_CODE, country.getDialCode());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (task != null) {
            task.cancel(true);
        }
    }
}
