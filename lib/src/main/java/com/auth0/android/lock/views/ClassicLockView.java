/*
 * ClassicLockView.java
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
import android.support.annotation.NonNull;
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
import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.internal.Configuration;
import com.auth0.android.lock.internal.Theme;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;
import com.squareup.otto.Bus;

public class ClassicLockView extends LinearLayout implements LockWidgetForm {

    private static final String TAG = ClassicLockView.class.getSimpleName();
    private static final int FORM_INDEX = 2;
    private final Bus bus;
    private final Theme lockTheme;
    private Configuration configuration;

    private FormLayout formLayout;
    private FormView subForm;

    private View topBanner;
    private View bottomBanner;
    private ActionButton actionButton;
    private ProgressBar loadingProgressBar;

    private String lastEmailInput;

    public ClassicLockView(Context context, Bus lockBus, Theme lockTheme) {
        super(context);
        this.bus = lockBus;
        this.configuration = null;
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
        ViewUtils.tintWidget(loadingProgressBar, lockTheme.getPrimaryColor(getContext()));
        addView(loadingProgressBar, wrapHeightParams);
    }

    private void showContentLayout() {
        LayoutParams wrapHeightParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        LayoutParams formLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

        HeaderView headerView = new HeaderView(getContext(), lockTheme);
        addView(headerView, wrapHeightParams);

        topBanner = inflate(getContext(), R.layout.com_auth0_lock_sso_layout, null);
        topBanner.setVisibility(GONE);
        addView(topBanner, wrapHeightParams);

        formLayout = new FormLayout(this);
        addView(formLayout, formLayoutParams);

        bottomBanner = inflate(getContext(), R.layout.com_auth0_lock_terms_layout, null);
        bottomBanner.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showSignUpTermsDialog(null);
            }
        });
        bottomBanner.setVisibility(GONE);
        addView(bottomBanner, wrapHeightParams);

        actionButton = new ActionButton(getContext(), lockTheme);
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final Object event = subForm != null ? subForm.submitForm() : formLayout.onActionPressed();
                if (event == null) {
                    return;
                }
                if (!configuration.mustAcceptTerms() || !(event instanceof DatabaseSignUpEvent)) {
                    bus.post(event);
                    return;
                }
                showSignUpTermsDialog(new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bus.post(event);
                    }
                });
            }
        });
        addView(actionButton, wrapHeightParams);

        boolean showDatabase = configuration.getDatabaseConnection() != null;
        boolean showEnterprise = !configuration.getEnterpriseConnections().isEmpty();
        if (!showDatabase && !showEnterprise) {
            actionButton.setVisibility(GONE);
        }

        if (configuration.getInitialScreen() == InitialScreen.SIGN_UP) {
            showBottomBanner(true);
        } else if (configuration.allowForgotPassword() && configuration.getInitialScreen() == InitialScreen.FORGOT_PASSWORD) {
            showChangePasswordForm(true);
        }
    }

    /**
     * Setup the panel to show the correct forms by reading the Auth0 Configuration.
     *
     * @param configuration the configuration to use on this view, or null if it is missing.
     */
    public void configure(@Nullable Configuration configuration) {
        removeView(loadingProgressBar);
        loadingProgressBar = null;
        this.configuration = configuration;
        if (configuration != null && configuration.hasClassicConnections()) {
            init();
        } else {
            int errorRes = 0;
            if (configuration == null) {
                errorRes = R.string.com_auth0_lock_configuration_retrieving_error;
            } else if (!configuration.hasClassicConnections()) {
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
    public void showChangePasswordForm(boolean show) {
        if (show) {
            addSubForm(new ChangePasswordFormView(this, lastEmailInput));
        } else {
            removeSubForm();
        }
    }

    private void addSubForm(@NonNull FormView form) {
        if (subForm != null) {
            return;
        }
        removeView(formLayout);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        this.subForm = form;
        addView(subForm, FORM_INDEX, params);
    }

    private void removeSubForm() {
        if (subForm == null) {
            return;
        }
        removeView(subForm);
        subForm = null;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
        formLayout.refreshIdentityInput();
        addView(formLayout, FORM_INDEX, params);
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (subForm != null) {
            final boolean shouldDisplayPreviousForm = configuration.allowLogIn() || configuration.allowSignUp();
            if (shouldDisplayPreviousForm) {
                showSignUpTerms(subForm instanceof CustomFieldsFormView);

                removeSubForm();
                return true;
            }
        }

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
        formLayout.setEnabled(!show);
    }

    private void showSignUpTerms(boolean show) {
        bottomBanner.setVisibility(show ? VISIBLE : GONE);
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

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onFormSubmit() {
        actionButton.callOnClick();
    }

    @Override
    public void showCustomFieldsForm(DatabaseSignUpEvent event) {
        addSubForm(new CustomFieldsFormView(this, event.getEmail(), event.getPassword(), event.getUsername()));
        showSignUpTerms(false);
    }

    public void showMFACodeForm(DatabaseLoginEvent event) {
        addSubForm(new MFACodeFormView(this, event.getUsernameOrEmail(), event.getPassword()));
    }

    @Override
    public void onSocialLogin(SocialConnectionEvent event) {
        Log.d(TAG, "Social login triggered for connection " + event.getConnectionName());
        bus.post(event);
    }

    @Override
    public void showTopBanner(boolean show) {
        topBanner.setVisibility(show ? VISIBLE : GONE);
        if (formLayout != null) {
            formLayout.showOnlyEnterprise(show);
        }
    }

    @Override
    public void showBottomBanner(boolean show) {
        if (bottomBanner != null) {
            bottomBanner.setVisibility(show ? VISIBLE : GONE);
        }
    }

    @Override
    public void onEmailChanged(String email) {
        lastEmailInput = email;
        formLayout.onEmailChanged(email);
    }
}
