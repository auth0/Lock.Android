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

package com.auth0.lock.sms.fragment;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.auth0.lock.sms.R;
import com.auth0.lock.sms.task.LoadCountriesTask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CountryCodeActivity extends ListActivity {

    public static final String COUNTRY_CODE = "COUNTRY_CODE";
    public static final String COUNTRY_DIAL_CODE = "COUNTRY_DIAL_CODE";
    AsyncTask<String, Void, Map<String, String>> task;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    setListAdapter(new CountryAdapter(getContext(), countries));
                    getListView().setTextFilterEnabled(true);
                }
            }
        };
        task.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
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

    private static class Country {
        private String isoCode;
        private String dialCode;

        private Country(String isoCode, String dialCode) {
            this.isoCode = isoCode;
            this.dialCode = dialCode;
        }

        public String getDialCode() {
            return dialCode;
        }

        public String getIsoCode() {
            return isoCode;
        }

        public String getDisplayName() {
            Locale locale = new Locale("", isoCode);
            return locale.getDisplayName();
        }
    }

    private static class CountryAdapter extends ArrayAdapter<Country> {

        public CountryAdapter(Context context, List<Country> countries) {
            super(context, 0, countries);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Country country = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_country_code, parent, false);
            }

            TextView countryNameTextView = (TextView) convertView.findViewById(R.id.sms_country_name_text_view);
            countryNameTextView.setText(country.getDisplayName());
            TextView countryCodeTextView = (TextView) convertView.findViewById(R.id.sms_country_code_text_view);
            countryCodeTextView.setText(country.getDialCode());
            return convertView;
        }
    }
}
