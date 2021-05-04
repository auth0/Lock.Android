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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.adapters.CountryAdapter;
import com.auth0.android.lock.utils.LoadCountriesTask;

import java.util.ArrayList;
import java.util.List;

public class CountryCodeActivity extends AppCompatActivity {

    public static final String COUNTRY_CODE_EXTRA = "COUNTRY_CODE";
    public static final String COUNTRY_DIAL_CODE_EXTRA = "COUNTRY_DIAL_CODE";

    private static final String TAG = CountryCodeActivity.class.getName();

    private static LoadCountriesTask task;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.com_auth0_lock_passwordless_activity_country_code);

        final ActionBar bar = getSupportActionBar();
        if (bar != null) {
            //If actionBar is present, remove it as the custom search view is already in the layout
            bar.hide();
        }

        final EditText searchText = findViewById(R.id.com_auth0_lock_passwordless_sms_search_country);
        final ListView listView = findViewById(R.id.com_auth0_lock_passwordless_sms_country_code_list);
        List<Country> countryList = new ArrayList<>();
        CountryAdapter countryAdapter = new CountryAdapter(this, countryList);
        listView.setAdapter(countryAdapter);

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.v(TAG, String.format("Filtering with string (%s)", s));
                countryAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Country country = countryAdapter.getItem(position);
            Intent data = new Intent();
            data.putExtra(COUNTRY_CODE_EXTRA, country.getIsoCode());
            data.putExtra(COUNTRY_DIAL_CODE_EXTRA, country.getDialCode());
            setResult(RESULT_OK, data);
            finish();
        });

        task = new LoadCountriesTask() {
            @Override
            protected void onPostExecute(List<Country> result) {
                task = null;
                countryList.addAll(result);
                countryAdapter.notifyDataSetChanged();
            }
        };
        task.execute(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (task != null) {
            Log.v(TAG, "Task was cancelled");
            task.cancel(true);
        }
    }

}
