/*
 * PasswordlessFormLayout.java
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.views.interfaces.LockWidget;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;
import com.auth0.android.lock.views.interfaces.LockWidgetSocial;

public class PasswordlessFormLayout extends LinearLayout implements PasswordlessFormView.OnPasswordlessRetryListener {
    private final LockWidget lockWidget;
    private SocialView socialLayout;
    private TextView orSeparatorMessage;
    private PasswordlessFormView passwordlessLayout;

    public PasswordlessFormLayout(Context context) {
        super(context);
        lockWidget = null;
    }

    public PasswordlessFormLayout(LockWidget lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        boolean showSocial = !lockWidget.getConfiguration().getSocialStrategies().isEmpty();
        boolean showPasswordless = lockWidget.getConfiguration().getDefaultPasswordlessStrategy() != null;

        if (showSocial) {
            addSocialLayout(showPasswordless);
        }
        if (showPasswordless) {
            if (showSocial) {
                addSeparator();
            }
            addPasswordlessLayout();
        }
    }

    private void addSocialLayout(boolean smallButtons) {
        socialLayout = new SocialView((LockWidgetSocial) lockWidget, smallButtons);
        addView(socialLayout);
    }

    private void addSeparator() {
        orSeparatorMessage = new TextView(getContext());
        PasswordlessMode passwordlessMode = lockWidget.getConfiguration().getPasswordlessMode();
        int stringRes = R.string.com_auth0_lock_passwordless_email_forms_separator;
        if (passwordlessMode != null && (passwordlessMode == PasswordlessMode.SMS_LINK || passwordlessMode == PasswordlessMode.SMS_CODE)) {
            stringRes = R.string.com_auth0_lock_passwordless_sms_forms_separator;
        }
        orSeparatorMessage.setText(stringRes);
        orSeparatorMessage.setLineSpacing(getResources().getDimension(R.dimen.com_auth0_lock_normal_text_spacing), 1);
        orSeparatorMessage.setGravity(Gravity.CENTER);
        int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_small);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, verticalPadding, 0, verticalPadding);
        addView(orSeparatorMessage, params);
    }

    private void addPasswordlessLayout() {
        passwordlessLayout = new PasswordlessFormView((LockWidgetPasswordless) lockWidget, this);
        addView(passwordlessLayout);
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (passwordlessLayout != null && passwordlessLayout.onBackPressed()) {
            if (socialLayout != null) {
                socialLayout.setVisibility(VISIBLE);
            }
            if (orSeparatorMessage != null) {
                orSeparatorMessage.setVisibility(VISIBLE);
            }
            return true;
        }
        return false;
    }

    /**
     * Notifies the form that the code was correctly sent and it should now wait
     * for the user to input the valid code.
     */
    public void codeSent() {
        if (passwordlessLayout != null) {
            if (socialLayout != null) {
                socialLayout.setVisibility(GONE);
            }
            if (orSeparatorMessage != null) {
                orSeparatorMessage.setVisibility(GONE);
            }
            passwordlessLayout.codeSent();
        }
    }

    /**
     * Notifies the form that the authentication has succeed, for it to erase state data.
     */
    public void onAuthenticationSucceed() {
        if (passwordlessLayout != null) {
            passwordlessLayout.onAuthenticationSucceed();
        }
    }

    /**
     * ActionButton has been clicked, and validation should be run on the current
     * visible form. If this validation passes, an action event will be returned.
     *
     * @return the action event of the current visible form or null if validation failed
     */
    @Nullable
    public Object onActionPressed() {
        View existingForm = getChildAt(getChildCount() == 1 ? 0 : 2);
        if (existingForm != null) {
            FormView form = (FormView) existingForm;
            return form.submitForm();
        }
        return null;
    }

    @Override
    public void onPasswordlessRetry() {
        if (socialLayout != null) {
            socialLayout.setVisibility(VISIBLE);
        }
        if (orSeparatorMessage != null) {
            orSeparatorMessage.setVisibility(VISIBLE);
        }
    }

    /**
     * Notifies the form that a new country code was selected by the user.
     *
     * @param country  the selected country iso code (2 chars).
     * @param dialCode the dial code for this country
     */
    public void onCountryCodeSelected(String country, String dialCode) {
        passwordlessLayout.onCountryCodeSelected(country, dialCode);
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        boolean waitingForCode = passwordlessLayout != null && passwordlessLayout.isWaitingForCode();
        if (orSeparatorMessage != null && !waitingForCode) {
            orSeparatorMessage.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (socialLayout != null && !waitingForCode) {
            socialLayout.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (passwordlessLayout != null) {
            passwordlessLayout.onKeyboardStateChanged(isOpen);
        }
    }
}
