/*
 * CountryCodeActivity.java
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

package com.auth0.android.lock;
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


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.adapters.CountryAdapter;
import com.auth0.android.lock.utils.ActivityUIHelper;
import com.auth0.android.lock.utils.LoadCountriesTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class CountryCodeActivity extends AppCompatActivity {

    public static final String COUNTRY_CODE_EXTRA = "COUNTRY_CODE";
    public static final String COUNTRY_DIAL_CODE_EXTRA = "COUNTRY_DIAL_CODE";
    public static final String FULLSCREEN_EXTRA = "fullscreen";

    private static final String TAG = CountryCodeActivity.class.getName();

    AsyncTask<String, Void, Map<String, String>> task;

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent().getBooleanExtra(FULLSCREEN_EXTRA, false)) {
            ActivityUIHelper.setFullscreenMode(this);
        }

        setContentView(R.layout.com_auth0_lock_passwordless_activity_country_code);

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setIcon(android.R.color.transparent);
            bar.setDisplayShowTitleEnabled(false);
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowCustomEnabled(true);
            bar.setCustomView(R.layout.com_auth0_lock_passwordless_bar_country_search);
            final EditText searchText = (EditText) bar.getCustomView().findViewById(R.id.com_auth0_lock_passwordless_sms_search_country);
            searchText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    Log.v(TAG, String.format("Filtering with string (%s)", s));
                    CountryAdapter adapter = (CountryAdapter) listView.getAdapter();
                    adapter.getFilter().filter(s);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        }

        task = new LoadCountriesTask(this) {
            @Override
            protected void onPostExecute(Map<String, String> result) {
                task = null;
                if (result == null) {
                    return;
                }

                final ArrayList<String> names = new ArrayList<>(result.keySet());
                Collections.sort(names);
                List<Country> countries = new ArrayList<>(names.size());
                for (String name : names) {
                    countries.add(new Country(name, result.get(name)));
                }
                listView.setAdapter(new CountryAdapter(getContext(), countries));
            }
        };
        task.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
        listView = (ListView) findViewById(R.id.com_auth0_lock_passwordless_sms_country_code_list);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Country country = (Country) parent.getItemAtPosition(position);
                Intent data = new Intent();
                data.putExtra(COUNTRY_CODE_EXTRA, country.getIsoCode());
                data.putExtra(COUNTRY_DIAL_CODE_EXTRA, country.getDialCode());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @Override
    public void onStop() {
        super.onStop();
        if (task != null) {
            Log.v(TAG, "Task was cancelled");
            task.cancel(true);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (getIntent().getBooleanExtra(FULLSCREEN_EXTRA, false)) {
            ActivityUIHelper.setFullscreenMode(this);
        }
    }
}
