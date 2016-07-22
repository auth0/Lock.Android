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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.enums.SocialButtonStyle;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;

public class PasswordlessFormLayout extends LinearLayout implements PasswordlessInputCodeFormView.OnCodeResendListener, PasswordlessRequestCodeFormView.OnAlreadyGotCodeListener {

    private static final String TAG = PasswordlessFormLayout.class.getSimpleName();
    private static final int MAX_SOCIAL_BIG_BUTTONS_WITH_PASSWORDLESS = 3;

    private final LockWidgetPasswordless lockWidget;
    private SocialView socialLayout;
    private TextView orSeparatorMessage;
    private PasswordlessRequestCodeFormView passwordlessRequestCodeLayout;
    private PasswordlessInputCodeFormView passwordlessInputCodeLayout;
    private boolean showGotCodeButton;

    public PasswordlessFormLayout(Context context) {
        super(context);
        lockWidget = null;
    }

    public PasswordlessFormLayout(LockWidgetPasswordless lockWidget) {
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
            addPasswordlessRequestCodeLayout();
        }
    }

    private void addSocialLayout(boolean passwordlessAvailable) {
        int style = lockWidget.getConfiguration().getSocialButtonStyle();
        boolean fewConnections = lockWidget.getConfiguration().getSocialStrategies().size() <= MAX_SOCIAL_BIG_BUTTONS_WITH_PASSWORDLESS;

        if (style == SocialButtonStyle.UNSPECIFIED) {
            socialLayout = new SocialView(lockWidget, passwordlessAvailable && !fewConnections);
        } else {
            socialLayout = new SocialView(lockWidget, style == SocialButtonStyle.SMALL);
        }

        addView(socialLayout);
    }

    private void addSeparator() {
        orSeparatorMessage = new LineSpacingTextView(getContext());
        int passwordlessMode = lockWidget.getConfiguration().getPasswordlessMode();
        int stringRes = R.string.com_auth0_lock_passwordless_email_forms_separator;
        if (passwordlessMode == PasswordlessMode.SMS_LINK || passwordlessMode == PasswordlessMode.SMS_CODE) {
            stringRes = R.string.com_auth0_lock_passwordless_sms_forms_separator;
        }
        orSeparatorMessage.setText(stringRes);
        orSeparatorMessage.setLineSpacing(getResources().getDimension(R.dimen.com_auth0_lock_separator_text_spacing), 1);
        orSeparatorMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.com_auth0_lock_text));
        orSeparatorMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.com_auth0_lock_title_text));
        orSeparatorMessage.setGravity(Gravity.CENTER);
        int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, verticalPadding, 0, verticalPadding);
        addView(orSeparatorMessage, params);
    }

    private void addPasswordlessRequestCodeLayout() {
        if (passwordlessRequestCodeLayout == null) {
            passwordlessRequestCodeLayout = new PasswordlessRequestCodeFormView(lockWidget, this);
        }
        addView(passwordlessRequestCodeLayout);
    }

    private void addPasswordlessInputCodeLayout(String emailOrNumber) {
        passwordlessInputCodeLayout = new PasswordlessInputCodeFormView(lockWidget, this, emailOrNumber);
        addView(passwordlessInputCodeLayout);
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (passwordlessInputCodeLayout != null) {
            Log.d(TAG, "Removing the Code Input Form, going back to the Social/Passwordless Form.");
            if (socialLayout != null) {
                socialLayout.setVisibility(VISIBLE);
            }
            if (orSeparatorMessage != null) {
                orSeparatorMessage.setVisibility(VISIBLE);
            }
            removeView(passwordlessInputCodeLayout);
            addView(passwordlessRequestCodeLayout);
            passwordlessInputCodeLayout = null;
            return true;
        }
        return false;
    }

    /**
     * Notifies the form that the code was correctly sent and it should now wait
     * for the user to input the valid code.
     */
    public void codeSent(String emailOrNumber) {
        Log.d(TAG, "Now showing the Code Input Form");
        if (passwordlessRequestCodeLayout != null) {
            removeView(passwordlessRequestCodeLayout);
            if (socialLayout != null) {
                socialLayout.setVisibility(GONE);
            }
            if (orSeparatorMessage != null) {
                orSeparatorMessage.setVisibility(GONE);
            }
        }
        addPasswordlessInputCodeLayout(emailOrNumber);
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
    public void onCodeNeedToResend() {
        if (socialLayout != null) {
            socialLayout.setVisibility(VISIBLE);
        }
        if (orSeparatorMessage != null) {
            orSeparatorMessage.setVisibility(VISIBLE);
        }
        if (passwordlessInputCodeLayout != null) {
            removeView(passwordlessInputCodeLayout);
            passwordlessInputCodeLayout = null;
        }
        addView(passwordlessRequestCodeLayout);
        passwordlessRequestCodeLayout.showGotCodeButton();
        showGotCodeButton = true;
    }

    @Override
    public void onAlreadyGotCode(String emailOrCode) {
        codeSent(emailOrCode);
    }

    @Override
    public boolean shouldShowGotCodeButton() {
        return showGotCodeButton;
    }

    /**
     * Notifies the form that a new country code was selected by the user.
     *
     * @param country  the selected country iso code (2 chars).
     * @param dialCode the dial code for this country
     */
    public void onCountryCodeSelected(String country, String dialCode) {
        if (passwordlessRequestCodeLayout != null) {
            passwordlessRequestCodeLayout.onCountryCodeSelected(country, dialCode);
        }
    }

    public void loadPasswordlessData(String emailOrNumber, @Nullable Country country) {
        if (passwordlessRequestCodeLayout != null) {
            Log.d(TAG, String.format("Loading recent passwordless data into the form. Identity %s with Country %s", emailOrNumber, country));
            showGotCodeButton = true;
            passwordlessRequestCodeLayout.setInputText(emailOrNumber);
            if (country != null) {
                passwordlessRequestCodeLayout.onCountryCodeSelected(country.getIsoCode(), country.getDialCode());
                emailOrNumber = country.getDialCode() + emailOrNumber;
            }
            passwordlessRequestCodeLayout.setLastEmailOrNumber(emailOrNumber);
            passwordlessRequestCodeLayout.showGotCodeButton();
        }
    }
}
