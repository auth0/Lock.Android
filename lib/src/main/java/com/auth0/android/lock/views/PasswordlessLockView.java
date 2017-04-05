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
import android.content.DialogInterface;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.events.CountryCodeChangeEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.Theme;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;
import com.squareup.otto.Bus;

public class PasswordlessLockView extends LinearLayout implements LockWidgetPasswordless, View.OnClickListener {

    private static final String TAG = PasswordlessLockView.class.getSimpleName();
    private final Bus bus;
    private final Theme lockTheme;
    private Configuration configuration;
    private PasswordlessFormLayout formLayout;
    private ActionButton actionButton;
    private ProgressBar loadingProgressBar;
    private HeaderView headerView;
    private View bottomBanner;

    public PasswordlessLockView(Context context, Bus lockBus, Theme lockTheme) {
        super(context);
        this.bus = lockBus;
        this.lockTheme = lockTheme;
        showWaitForConfigurationLayout();
    }

    private void init() {
        setOrientation(VERTICAL);
        if (configuration == null) {
            Log.w(TAG, "Configuration is missing, the view won't init.");
            showConfigurationMissingLayout(R.string.com_auth0_lock_configuration_retrieving_error);
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

        headerView = new HeaderView(getContext(), lockTheme);
        resetHeaderTitle();
        addView(headerView, wrapHeightParams);

        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_horizontal_margin);
        formLayout = new PasswordlessFormLayout(this);
        LayoutParams formLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        formLayout.setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        addView(formLayout, formLayoutParams);

        boolean showPasswordless = configuration.getPasswordlessConnection() != null;
        if (showPasswordless) {
            // add terms of use bottom banner if url's are present
            if (configuration.showTermsOfUse()) {
                bottomBanner = inflate(getContext(), R.layout.com_auth0_lock_terms_layout, null);
                bottomBanner.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showSignUpTermsDialog(null);
                    }
                });
                bottomBanner.setVisibility(VISIBLE);
                addView(bottomBanner, wrapHeightParams);
            }

            actionButton = new ActionButton(getContext(), lockTheme);
            actionButton.setOnClickListener(this);
            addView(actionButton, wrapHeightParams);

        }
    }

    /**
     * Create a dialog to show the Privacy Policy and Terms of Service text.
     * If the provided callback it's not null, it will ask for acceptance.
     *
     * @param acceptCallback the callback to receive the acceptance. Can be null.
     */
    private void showSignUpTermsDialog(@Nullable DialogInterface.OnClickListener acceptCallback) {
        final String content = String.format(getResources().getString(R.string.com_auth0_lock_sign_up_terms_dialog_message), configuration.getTermsURL(), configuration.getPrivacyURL());
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext())
                .setTitle(getResources().getString(R.string.com_auth0_lock_sign_up_terms_dialog_title))
                .setPositiveButton(R.string.com_auth0_lock_action_ok, null)
                .setMessage(Html.fromHtml(content));
        if (acceptCallback != null) {
            builder.setNegativeButton(R.string.com_auth0_lock_action_cancel, null)
                    .setPositiveButton(R.string.com_auth0_lock_action_accept, acceptCallback)
                    .setCancelable(false);
        }

        //the dialog needs to be shown before we can get it's view.
        final TextView message = (TextView) builder.show().findViewById(android.R.id.message);
        if (message != null) {
            message.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    public void configure(@Nullable Configuration configuration) {
        removeView(loadingProgressBar);
        loadingProgressBar = null;
        this.configuration = configuration;
        if (configuration != null && configuration.hasPasswordlessConnections()) {
            init();
        } else {
            int errorRes = 0;
            if (configuration == null) {
                errorRes = R.string.com_auth0_lock_configuration_retrieving_error;
            } else if (!configuration.hasPasswordlessConnections()) {
                errorRes = R.string.com_auth0_lock_missing_connections_message;
            }
            showConfigurationMissingLayout(errorRes);
        }
    }

    private void showConfigurationMissingLayout(@StringRes int errorMessage) {
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_horizontal_margin);
        final LinearLayout errorLayout = new LinearLayout(getContext());
        errorLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(horizontalMargin, 0, horizontalMargin, 0);
        params.gravity = Gravity.CENTER;

        TextView errorText = new TextView(getContext());
        errorText.setText(errorMessage);
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

    @Override
    public void updateHeaderTitle(@StringRes int titleRes) {
        headerView.setTitle(getContext().getString(titleRes));
        headerView.showTitle(true);
    }

    @Override
    public void resetHeaderTitle() {
        headerView.setTitle(lockTheme.getHeaderTitle(getContext()));
        headerView.showTitle(!configuration.hideMainScreenTitle());
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        return formLayout != null && formLayout.onBackPressed();
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
    public void onOAuthLoginRequest(OAuthLoginEvent event) {
        Log.d(TAG, "Social login triggered for connection " + event.getConnection());
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

    public void loadPasswordlessData(String input, @Nullable Country country) {
        formLayout.loadPasswordlessData(input, country);
    }
}
