/*
 * ValidatedPhoneNumber.java
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

package com.auth0.android.lock.views;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.utils.json.LoadCountriesTask;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class CountryCodeSelectorView extends LinearLayout {
    private static final String TAG = CountryCodeSelectorView.class.getSimpleName();
    private LoadCountriesTask task;
    private Country selectedCountry;

    private ImageView icon;
    private TextView countryNameTextView;
    private TextView countryCodeTextView;
    private ImageView chevron;

    public CountryCodeSelectorView(Context context) {
        super(context);
        init();
    }

    public CountryCodeSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CountryCodeSelectorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_country_code_selector, this);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        chevron = (ImageView) findViewById(R.id.com_auth0_lock_chevron);
        countryNameTextView = (TextView) findViewById(R.id.com_auth0_lock_country_name);
        countryCodeTextView = (TextView) findViewById(R.id.com_auth0_lock_country_code);
        prepareTask();
        setupBackground();
    }

    private void setupBackground() {
        Drawable leftBackground = ViewUtils.getRoundedBackground(getResources(), ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_field_border_normal), ViewUtils.Corners.ONLY_LEFT);
        Drawable rightBackground = ViewUtils.getRoundedBackground(getResources(), ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_country_code_background), ViewUtils.Corners.ONLY_RIGHT);
        ViewUtils.setBackground(icon, leftBackground);
        ViewUtils.setBackground(chevron, rightBackground);
        ViewGroup parent = ((ViewGroup) countryNameTextView.getParent());
        Drawable bg = parent.getBackground();
        GradientDrawable gd = bg == null ? new GradientDrawable() : (GradientDrawable) bg;
        gd.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_corner_radius));
        gd.setStroke(getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_input_field_stroke_width), ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_field_border_normal));
        gd.setColor(ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_field_border_normal));
        ViewUtils.setBackground(parent, gd);
    }

    private void prepareTask() {
        task = new LoadCountriesTask(getContext()) {
            @Override
            protected void onPostExecute(Map<String, String> result) {
                task = null;
                String defaultCountry = Locale.getDefault().getCountry();
                Country country = new Country(getContext().getString(R.string.com_auth0_lock_default_country_name_fallback), getContext().getString(R.string.com_auth0_lock_default_country_code_fallback));
                if (result != null) {
                    final ArrayList<String> names = new ArrayList<>(result.keySet());
                    for (String name : names) {
                        if (name.equalsIgnoreCase(defaultCountry)) {
                            country = new Country(name, result.get(name));
                            break;
                        }
                    }
                }
                if (selectedCountry == null) {
                    setSelectedCountry(country);
                }
            }
        };
        task.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
    }

    /**
     * Sets the current Country to the one given.
     *
     * @param country the country to set.
     */
    public void setSelectedCountry(@NonNull Country country) {
        Log.d(TAG, "Selected country changed to " + country.getDisplayName());
        countryNameTextView.setText(country.getDisplayName());
        countryCodeTextView.setText(country.getDialCode());
        selectedCountry = country;
    }

    /**
     * Gets the currently selected Country.
     *
     * @return the country that is currently set.
     */
    public Country getSelectedCountry() {
        return selectedCountry;
    }

    @Override
    protected void onDetachedFromWindow() {
        if (task != null) {
            task.cancel(true);
            task = null;
        }
        super.onDetachedFromWindow();
    }
}
