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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.internal.configuration.AuthMode;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.Theme;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;
import com.squareup.otto.Bus;

@SuppressWarnings("NullableProblems")
@SuppressLint("ViewConstructor")
public class ClassicLockView extends LinearLayout implements LockWidgetForm {

    private static final String TAG = ClassicLockView.class.getSimpleName();
    private static final int FORM_INDEX = 2;
    private final Bus bus;
    private final Theme lockTheme;
    private Configuration configuration;

    private HeaderView headerView;
    private FormLayout formLayout;
    private FormView subForm;

    private View topBanner;
    private View bottomBanner;
    private ActionButton actionButton;
    private ProgressBar loadingProgressBar;

    private String lastEmailInput;

    public ClassicLockView(@NonNull Context context, @NonNull Bus lockBus, @NonNull Theme lockTheme) {
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
            showConfigurationMissingLayout(true);
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

        boolean displayTerms = configuration.showTerms() || configuration.mustAcceptTerms();

        headerView = new HeaderView(getContext(), lockTheme);
        resetHeaderTitle();
        addView(headerView, wrapHeightParams);

        topBanner = inflate(getContext(), R.layout.com_auth0_lock_sso_layout, null);
        topBanner.setVisibility(GONE);
        addView(topBanner, wrapHeightParams);

        formLayout = new FormLayout(this);
        addView(formLayout, formLayoutParams);

        if (displayTerms) {
            bottomBanner = inflate(getContext(), R.layout.com_auth0_lock_terms_layout, null);
            bottomBanner.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    showSignUpTermsDialog(null);
                }
            });
            bottomBanner.setVisibility(GONE);
            addView(bottomBanner, wrapHeightParams);
        }

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
        actionButton.showLabel(configuration.useLabeledSubmitButton() || configuration.hideMainScreenTitle());
        addView(actionButton, wrapHeightParams);

        boolean showDatabase = configuration.getDatabaseConnection() != null;
        boolean showEnterprise = !configuration.getEnterpriseConnections().isEmpty();
        boolean singleEnterprise = configuration.getEnterpriseConnections().size() == 1 && configuration.getSocialConnections().isEmpty();
        if (!showDatabase && (singleEnterprise || !showEnterprise)) {
            actionButton.setVisibility(GONE);
        }

        if (configuration.getInitialScreen() == InitialScreen.SIGN_UP) {
            showBottomBanner(displayTerms);
            updateButtonLabel(R.string.com_auth0_lock_action_sign_up);
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
            showConfigurationMissingLayout(configuration == null);
        }
    }

    private void showConfigurationMissingLayout(final boolean showRetry) {
        final View errorLayout = LayoutInflater.from(getContext()).inflate(R.layout.com_auth0_lock_error_layout, this, false);
        TextView tvTitle = errorLayout.findViewById(R.id.com_auth0_lock_error_title);
        TextView tvError = errorLayout.findViewById(R.id.com_auth0_lock_error_subtitle);
        TextView tvAction = errorLayout.findViewById(R.id.com_auth0_lock_error_action);

        if (showRetry) {
            tvTitle.setText(R.string.com_auth0_lock_recoverable_error_title);
            tvError.setText(R.string.com_auth0_lock_recoverable_error_subtitle);
            tvAction.setText(R.string.com_auth0_lock_recoverable_error_action);
            tvAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(new FetchApplicationEvent());
                    removeView(errorLayout);
                    showWaitForConfigurationLayout();
                }
            });
        } else if (configuration.getSupportURL() == null) {
            tvTitle.setText(R.string.com_auth0_lock_unrecoverable_error_title);
            tvError.setText(R.string.com_auth0_lock_unrecoverable_error_subtitle_without_action);
            tvAction.setVisibility(GONE);
        } else {
            tvTitle.setText(R.string.com_auth0_lock_unrecoverable_error_title);
            tvError.setText(R.string.com_auth0_lock_unrecoverable_error_subtitle);
            tvAction.setText(R.string.com_auth0_lock_unrecoverable_error_action);
            tvAction.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(configuration.getSupportURL())));
                }
            });
        }
        addView(errorLayout);
    }

    @Override
    public void showChangePasswordForm(boolean show) {
        if (show) {
            ChangePasswordFormView form = new ChangePasswordFormView(this, lastEmailInput);
            updateHeaderTitle(R.string.com_auth0_lock_title_change_password);
            addSubForm(form);
            updateButtonLabel(R.string.com_auth0_lock_action_send_email);
        } else {
            removeSubForm();
        }
    }

    private void updateHeaderTitle(@StringRes int titleRes) {
        headerView.setTitle(getContext().getString(titleRes));
        headerView.showTitle(true);
    }

    private void resetHeaderTitle() {
        headerView.setTitle(lockTheme.getHeaderTitle(getContext()));
        headerView.showTitle(!configuration.hideMainScreenTitle());
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
        updateButtonLabel(formLayout.getSelectedMode() == AuthMode.SIGN_UP ? R.string.com_auth0_lock_action_sign_up : R.string.com_auth0_lock_action_log_in);
        resetHeaderTitle();
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
                resetHeaderTitle();
                showSignUpTerms(subForm instanceof CustomFieldsFormView);
                removeSubForm();
                clearFocus();
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
        if (formLayout != null) {
            formLayout.setEnabled(!show);
        }
    }

    private void showSignUpTerms(boolean show) {
        if (bottomBanner != null) {
            bottomBanner.setVisibility(show ? VISIBLE : GONE);
        }
    }

    /**
     * Create a dialog to show the Privacy Policy and Terms of Service text.
     * If the provided callback it's not null, it will ask for acceptance.
     *
     * @param acceptCallback the callback to receive the acceptance. Can be null.
     */
    @SuppressLint("StringFormatInvalid")
    private void showSignUpTermsDialog(@Nullable DialogInterface.OnClickListener acceptCallback) {
        final String content = getResources().getString(R.string.com_auth0_lock_sign_up_terms_dialog_message, configuration.getTermsURL(), configuration.getPrivacyURL());
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
        final TextView message = builder.show().findViewById(android.R.id.message);
        if (message != null) {
            message.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    @NonNull
    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onFormSubmit() {
        actionButton.callOnClick();
    }

    @Override
    public void showCustomFieldsForm(@NonNull DatabaseSignUpEvent event) {
        //noinspection ConstantConditions
        CustomFieldsFormView form = new CustomFieldsFormView(this, event.getEmail(), event.getPassword(), event.getUsername());
        addSubForm(form);
        updateHeaderTitle(R.string.com_auth0_lock_action_sign_up);
        showSignUpTerms(false);
    }

    public void showMFACodeForm(@NonNull DatabaseLoginEvent event) {
        MFACodeFormView form = new MFACodeFormView(this, event.getUsernameOrEmail(), event.getPassword(), event.getMFAToken());
        updateHeaderTitle(R.string.com_auth0_lock_title_mfa_input_code);
        addSubForm(form);
    }

    @Override
    public void onOAuthLoginRequest(@NonNull OAuthLoginEvent event) {
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
    public void updateButtonLabel(@StringRes int stringRes) {
        if (actionButton != null) {
            actionButton.setLabel(stringRes);
        }
    }

    @Override
    public void onEmailChanged(@NonNull String email) {
        lastEmailInput = email;
        formLayout.onEmailChanged(email);
    }
}
