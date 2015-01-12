/*
 * CountryAdapter.java
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

package com.auth0.lock.sms.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.auth0.lock.sms.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hernan on 1/12/15.
 */
public class CountryAdapter extends BaseAdapter {

    private final Filter filter;
    private final Context context;
    private List<Country> data;

    public CountryAdapter(Context context, List<Country> countries) {
        this.context = context;
        this.data = new ArrayList<>(countries);
        this.filter = new CountryFilter(countries);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Country getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Country country = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_country_code, parent, false);
        }

        TextView countryNameTextView = (TextView) convertView.findViewById(R.id.sms_country_name_text_view);
        countryNameTextView.setText(country.getDisplayName());
        TextView countryCodeTextView = (TextView) convertView.findViewById(R.id.sms_country_code_text_view);
        countryCodeTextView.setText(country.getDialCode());
        return convertView;
    }

    public Filter getFilter() {
        return filter;
    }

    private class CountryFilter extends Filter {

        private final List<Country> countries;

        private CountryFilter(List<Country> countries) {
            this.countries = countries;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String prefix = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            List<Country> filtered = new ArrayList<>(countries.size());
            for (Country country : countries) {
                if (country.getDisplayName().toLowerCase().contains(prefix)) {
                    filtered.add(country);
                }
            }
            results.values = filtered;
            results.count = filtered.size();
            return results;
        }

        @Override
        @SuppressWarnings("unchecked")
        protected void publishResults(CharSequence constraint, FilterResults results) {
            data = (List<Country>) results.values;
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
