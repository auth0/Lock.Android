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
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.utils.LoadCountriesTask;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Map;

public class CountryCodeSelectorView extends LinearLayout {
    private LoadCountriesTask task;
    private Country selectedCountry;

    private ImageView icon;
    private TextView countryTextView;

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
        countryTextView = (TextView) findViewById(R.id.com_auth0_lock_text);
        Country initialCountry = new Country("US", "+1");
        setSelectedCountry(initialCountry);
        prepareTask();
        setupBackground();
    }

    private void setupBackground() {
        Drawable leftBackground = ViewUtils.getRoundedBackground(getResources(), ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_input_field_border_normal), ViewUtils.Corners.ONLY_LEFT);
        Drawable rightBackground = ViewUtils.getRoundedBackground(getResources(), ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_input_country_code_background), ViewUtils.Corners.ONLY_RIGHT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            icon.setBackground(leftBackground);
            countryTextView.setBackground(rightBackground);
        } else {
            //noinspection deprecation
            icon.setBackgroundDrawable(leftBackground);
            //noinspection deprecation
            countryTextView.setBackgroundDrawable(rightBackground);
        }
        ViewGroup parent = ((ViewGroup) countryTextView.getParent());
        Drawable bg = parent.getBackground();
        GradientDrawable gd = bg == null ? new GradientDrawable() : (GradientDrawable) bg;
        gd.setCornerRadius(ViewUtils.dipToPixels(getResources(), ViewUtils.CORNER_RADIUS));
        gd.setStroke((int) getResources().getDimension(R.dimen.com_auth0_lock_input_field_stroke_width), ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_input_field_border_normal));
        gd.setColor(ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_input_field_border_normal));
        parent.setBackgroundDrawable(gd);
    }

    private void prepareTask() {
        task = new LoadCountriesTask(getContext()) {
            @Override
            protected void onPostExecute(Map<String, String> result) {
                String defaultCountry = Locale.getDefault().getCountry();
                task = null;
                if (result != null) {
                    final ArrayList<String> names = new ArrayList<>(result.keySet());
                    for (String name : names) {
                        if (name.equalsIgnoreCase(defaultCountry)) {
                            selectedCountry = new Country(name, result.get(name));
                            break;
                        }
                    }
                }
                setSelectedCountry(selectedCountry);
                countryTextView.setVisibility(VISIBLE);
            }
        };
        task.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
    }

    public void setSelectedCountry(Country country) {
        countryTextView.setText(country.getDialCode() + " " + country.getDisplayName());
        selectedCountry = country;
    }

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
