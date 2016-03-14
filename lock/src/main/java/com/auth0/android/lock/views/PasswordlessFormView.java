/*
 * PasswordlessFormView.java
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

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;

public class PasswordlessFormView extends FormView implements View.OnClickListener {

    private static final String TAG = PasswordlessFormView.class.getSimpleName();
    private final LockWidgetPasswordless lockWidget;
    private final OnPasswordlessRetryListener callback;
    private ValidatedInputView passwordlessInput;
    private PasswordlessMode choosenMode;
    private boolean waitingForCode;
    private String emailOrNumber;
    private TextView topMessage;
    private TextView resendButton;
    private int sentMessage;
    private CountryCodeSelectorView countryCodeSelector;

    public PasswordlessFormView(LockWidgetPasswordless lockWidget, OnPasswordlessRetryListener callback) {
        super(lockWidget.getContext());
        choosenMode = lockWidget.getConfiguration().getPasswordlessMode();
        this.lockWidget = lockWidget;
        this.callback = callback;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_form_view, this);
        topMessage = (TextView) findViewById(R.id.com_auth0_lock_text);
        resendButton = (TextView) findViewById(R.id.com_auth0_lock_resend);
        resendButton.setOnClickListener(this);
        topMessage.setVisibility(GONE);
        passwordlessInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_passwordless);
        countryCodeSelector = (CountryCodeSelectorView) findViewById(R.id.com_auth0_lock_country_code_selector);
        countryCodeSelector.setOnClickListener(this);

        selectPasswordlessMode();
    }


    private void selectPasswordlessMode() {
        switch (choosenMode) {
            case EMAIL_CODE:
                passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);
                countryCodeSelector.setVisibility(GONE);
                sentMessage = R.string.com_auth0_lock_title_passwordless_code_email_sent;
                break;
            case EMAIL_LINK:
                passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);
                countryCodeSelector.setVisibility(GONE);
                sentMessage = R.string.com_auth0_lock_title_passwordless_link_email_sent;
                break;
            case SMS_CODE:
                passwordlessInput.setDataType(ValidatedInputView.DataType.PHONE_NUMBER);
                countryCodeSelector.setVisibility(VISIBLE);
                sentMessage = R.string.com_auth0_lock_title_passwordless_code_sms_sent;
                break;
            case SMS_LINK:
                passwordlessInput.setDataType(ValidatedInputView.DataType.PHONE_NUMBER);
                countryCodeSelector.setVisibility(VISIBLE);
                sentMessage = R.string.com_auth0_lock_title_passwordless_code_sms_sent;
                break;
        }
        passwordlessInput.setVisibility(VISIBLE);
        passwordlessInput.clearInput();
        showTopMessage(false);
    }

    @Override
    public Object getActionEvent() {
        if (waitingForCode) {
            return new PasswordlessLoginEvent(choosenMode, emailOrNumber, getInputText());
        } else {
            return new PasswordlessLoginEvent(choosenMode, getInputText());
        }
    }

    private String getInputText() {
        if (choosenMode == PasswordlessMode.SMS_CODE || choosenMode == PasswordlessMode.SMS_LINK) {
            if (countryCodeSelector != null) {
                return countryCodeSelector.getSelectedCountry().getDialCode().replace(" ", "").replace("-", "") + passwordlessInput.getText().replace(" ", "").replace("-", "");
            } else {
                return passwordlessInput.getText().replace(" ", "").replace("-", "");
            }
        } else {
            return passwordlessInput.getText().replace(" ", "");
        }
    }

    private void showTopMessage(boolean show) {
        if (show) {
            String text = String.format(getResources().getString(sentMessage), emailOrNumber);
            topMessage.setText(text);
        }
        topMessage.setVisibility(show ? VISIBLE : GONE);
        resendButton.setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public boolean validateForm() {
        return passwordlessInput.validate(true);
    }

    @Nullable
    @Override
    public Object submitForm() {
        return validateForm() ? getActionEvent() : null;
    }

    /**
     * Triggers the 'back' action on this form
     *
     * @return true if the event was handled.
     */
    public boolean onBackPressed() {
        if (waitingForCode) {
            waitingForCode = false;
            selectPasswordlessMode();
            return true;
        }
        return false;
    }

    public void codeSent() {
        countryCodeSelector.setVisibility(GONE);
        emailOrNumber = getInputText();
        showTopMessage(true);
        if (choosenMode == PasswordlessMode.EMAIL_CODE || choosenMode == PasswordlessMode.SMS_CODE) {
            passwordlessInput.setDataType(ValidatedInputView.DataType.NUMBER);
            passwordlessInput.clearInput();
        } else {
            passwordlessInput.setVisibility(GONE);
        }

        waitingForCode = true;
    }

    public void onCountryCodeSelected(String country, String dialCode) {
        Country selectedCountry = new Country(country, dialCode);
        countryCodeSelector.setSelectedCountry(selectedCountry);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.com_auth0_lock_resend) {
            if (callback != null) {
                waitingForCode = false;
                selectPasswordlessMode();
                passwordlessInput.setText(emailOrNumber);
                callback.onPasswordlessRetry();
            }
        } else if (id == R.id.com_auth0_lock_country_code_selector) {
            lockWidget.onCountryCodeChangeRequest();
        }
    }

    public interface OnPasswordlessRetryListener {
        void onPasswordlessRetry();
    }
}