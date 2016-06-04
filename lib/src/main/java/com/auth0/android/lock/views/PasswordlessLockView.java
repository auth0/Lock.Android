/*
 * PasswordlessLockView.java
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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.events.CountryCodeChangeEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;
import com.auth0.android.lock.views.interfaces.LockWidgetSocial;
import com.squareup.otto.Bus;

public class PasswordlessLockView extends LinearLayout implements LockWidgetSocial, LockWidgetPasswordless, View.OnClickListener {

    private static final String TAG = PasswordlessLockView.class.getSimpleName();
    private final Bus bus;
    private Configuration configuration;
    private PasswordlessFormLayout formLayout;
    private ActionButton actionButton;
    private ProgressBar loadingProgressBar;
    private HeaderView headerView;


    public PasswordlessLockView(Context context) {
        super(context);
        bus = null;
    }

    public PasswordlessLockView(Context context, Bus lockBus) {
        super(context);
        this.bus = lockBus;
        showWaitForConfigurationLayout();
    }

    private void init() {
        setOrientation(VERTICAL);
        if (configuration == null) {
            Log.w(TAG, "Configuration is missing, the view won't init.");
            showConfigurationMissingLayout();
        } else {
            showContentLayout();
        }
    }

    private void showWaitForConfigurationLayout() {
        LayoutParams wrapHeightParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        wrapHeightParams.gravity = Gravity.CENTER;
        loadingProgressBar = new ProgressBar(getContext());
        loadingProgressBar.setIndeterminate(true);
        addView(loadingProgressBar, wrapHeightParams);
    }

    private void showContentLayout() {
        LayoutParams wrapHeightParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        headerView = new HeaderView(getContext());
        addView(headerView, wrapHeightParams);

        int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);
        formLayout = new PasswordlessFormLayout(this);
        LayoutParams formLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        formLayoutParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
        addView(formLayout, formLayoutParams);

        boolean showPasswordless = configuration.getDefaultPasswordlessStrategy() != null;
        if (showPasswordless) {
            actionButton = new ActionButton(getContext());
            actionButton.setOnClickListener(this);
            addView(actionButton, wrapHeightParams);
        }
    }

    public void configure(@Nullable Configuration configuration) {
        removeView(loadingProgressBar);
        loadingProgressBar = null;
        this.configuration = configuration;
        if (configuration != null) {
            init();
            return;
        }
        showConfigurationMissingLayout();
    }

    private void showConfigurationMissingLayout() {
        int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);
        final LinearLayout errorLayout = new LinearLayout(getContext());
        errorLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(horizontalMargin, 0, horizontalMargin, 0);
        params.gravity = Gravity.CENTER;

        TextView errorText = new TextView(getContext());
        errorText.setText(R.string.com_auth0_lock_configuration_retrieving_error);
        errorText.setGravity(Gravity.CENTER);

        Button retryButton = new Button(getContext());
        retryButton.setText(R.string.com_auth0_lock_action_retry);
        retryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new FetchApplicationEvent());
                removeView(errorLayout);
                showWaitForConfigurationLayout();
            }
        });
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childParams.gravity = Gravity.CENTER;
        errorLayout.addView(errorText, childParams);
        errorLayout.addView(retryButton, childParams);
        addView(errorLayout, params);
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        return formLayout.onBackPressed();
    }

    /**
     * Displays a progress bar on top of the action button. This will also
     * enable or disable the action button.
     *
     * @param show whether to show or hide the action bar.
     */
    public void showProgress(boolean show) {
        if (actionButton != null) {
            actionButton.showProgress(show);
        }
    }

    /**
     * Notifies the form that the code was correctly sent and it should now wait
     * for the user to input the valid code.
     */
    @Override
    public void onPasswordlessCodeSent(String emailOrNumber) {
        formLayout.codeSent(emailOrNumber);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onFormSubmit() {
        actionButton.callOnClick();
    }

    @Override
    public void onSocialLogin(SocialConnectionEvent event) {
        Log.d(TAG, "Social login triggered for connection " + event.getConnectionName());
        bus.post(event);
    }

    @Override
    public void onClick(View v) {
        Object event = formLayout.onActionPressed();
        if (event != null) {
            bus.post(event);
            actionButton.showProgress(true);
        }
    }

    @Override
    public void onCountryCodeChangeRequest() {
        bus.post(new CountryCodeChangeEvent());
    }

    /**
     * Notifies the form that a new country code was selected by the user.
     *
     * @param country  the selected country iso code (2 chars).
     * @param dialCode the dial code for this country
     */
    public void onCountryCodeSelected(String country, String dialCode) {
        formLayout.onCountryCodeSelected(country, dialCode);
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        formLayout.onKeyboardStateChanged(isOpen);
        headerView.setVisibility(isOpen ? GONE : VISIBLE);
    }

    public void loadPasswordlessData(String input, @Nullable Country country) {
        formLayout.loadPasswordlessData(input, country);
    }
}
