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

package com.auth0.android.lock.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.auth0.android.lock.R;

import java.util.ArrayList;
import java.util.List;

public class CountryAdapter extends BaseAdapter {

    private final CountryFilter filter;
    private final Context context;
    protected @NonNull
    List<Country> allData;
    protected @NonNull
    List<Country> filteredData;

    public CountryAdapter(@NonNull Context context, @NonNull List<Country> countries) {
        this.context = context;
        this.allData = countries;
        this.filteredData = countries;
        this.filter = new CountryFilter();
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Nullable
    @Override
    public Country getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.com_auth0_lock_passwordless_item_country_code, parent, false);
        }

        TextView countryNameTextView = convertView.findViewById(R.id.com_auth0_lock_passwordless_sms_country_name_text_view);
        TextView countryCodeTextView = convertView.findViewById(R.id.com_auth0_lock_passwordless_sms_country_code_text_view);
        Country country = getItem(position);
        countryNameTextView.setText(country.getDisplayName());
        countryCodeTextView.setText(country.getDialCode());
        return convertView;
    }

    @NonNull
    public Filter getFilter() {
        return filter;
    }

    private class CountryFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            String search = constraint.toString().toLowerCase();
            FilterResults results = new FilterResults();
            List<Country> filtered = new ArrayList<>();
            for (Country country : allData) {
                if (country.getDisplayName().toLowerCase().contains(search) || country.getIsoCode().toLowerCase().contains(search)) {
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
            filteredData = (List<Country>) results.values;
            notifyDataSetChanged();
        }
    }
}
