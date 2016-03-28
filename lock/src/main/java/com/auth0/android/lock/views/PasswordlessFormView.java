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
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;

public class PasswordlessFormView extends FormView implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = PasswordlessFormView.class.getSimpleName();
    private final LockWidgetPasswordless lockWidget;
    private final OnPasswordlessRetryListener callback;
    private ValidatedInputView passwordlessInput;
    private PasswordlessMode choosenMode;
    private boolean waitingForCode;
    private final boolean showTitle;
    private TextView topMessage;
    private TextView resendButton;
    private int sentMessage;
    private CountryCodeSelectorView countryCodeSelector;
    private String submittedEmailOrNumber;
    private String previousInput;

    public PasswordlessFormView(LockWidgetPasswordless lockWidget, OnPasswordlessRetryListener callback) {
        super(lockWidget.getContext());
        choosenMode = lockWidget.getConfiguration().getPasswordlessMode();
        showTitle = lockWidget.getConfiguration().getSocialStrategies().isEmpty();
        this.lockWidget = lockWidget;
        this.callback = callback;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_form_view, this);
        topMessage = (TextView) findViewById(R.id.com_auth0_lock_text);
        resendButton = (TextView) findViewById(R.id.com_auth0_lock_resend);
        resendButton.setOnClickListener(this);
        passwordlessInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_passwordless);
        passwordlessInput.setOnEditorActionListener(this);
        countryCodeSelector = (CountryCodeSelectorView) findViewById(R.id.com_auth0_lock_country_code_selector);
        countryCodeSelector.setOnClickListener(this);

        selectPasswordlessMode();
    }


    private void selectPasswordlessMode() {
        int titleMessage = 0;
        switch (choosenMode) {
            case EMAIL_CODE:
                titleMessage = R.string.com_auth0_lock_title_passwordless_email;
                sentMessage = R.string.com_auth0_lock_title_passwordless_code_email_sent;
                passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);
                countryCodeSelector.setVisibility(GONE);
                break;
            case EMAIL_LINK:
                titleMessage = R.string.com_auth0_lock_title_passwordless_email;
                passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);
                countryCodeSelector.setVisibility(GONE);
                break;
            case SMS_CODE:
                titleMessage = R.string.com_auth0_lock_title_passwordless_sms;
                sentMessage = R.string.com_auth0_lock_title_passwordless_code_sms_sent;
                passwordlessInput.setDataType(ValidatedInputView.DataType.PHONE_NUMBER);
                countryCodeSelector.setVisibility(VISIBLE);
                break;
            case SMS_LINK:
                titleMessage = R.string.com_auth0_lock_title_passwordless_sms;
                passwordlessInput.setDataType(ValidatedInputView.DataType.PHONE_NUMBER);
                countryCodeSelector.setVisibility(VISIBLE);
                break;
        }
        passwordlessInput.setVisibility(VISIBLE);
        passwordlessInput.clearInput();
        resendButton.setVisibility(GONE);
        setTopMessage(showTitle ? getResources().getString(titleMessage) : null);
    }

    @Override
    public Object getActionEvent() {
        if (waitingForCode) {
            return new PasswordlessLoginEvent(choosenMode, submittedEmailOrNumber, getInputText());
        } else {
            previousInput = getInputText();
            submittedEmailOrNumber = countryCodeSelector.getSelectedCountry().getDialCode() + previousInput;
            return new PasswordlessLoginEvent(choosenMode, submittedEmailOrNumber);
        }
    }

    private String getInputText() {
        return passwordlessInput.getText().replace(" ", "");
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
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (waitingForCode) {
            waitingForCode = false;
            selectPasswordlessMode();
            return true;
        }
        return false;
    }

    /**
     * Notifies the form that the code was correctly sent and it should now wait
     * for the user to input the valid code.
     */
    public void codeSent() {
        countryCodeSelector.setVisibility(GONE);
        resendButton.setVisibility(VISIBLE);
        if (choosenMode == PasswordlessMode.EMAIL_CODE || choosenMode == PasswordlessMode.SMS_CODE) {
            setTopMessage(String.format(getResources().getString(sentMessage), submittedEmailOrNumber));
            passwordlessInput.setDataType(ValidatedInputView.DataType.NUMBER);
            passwordlessInput.clearInput();
        } else {
            passwordlessInput.setVisibility(GONE);
        }

        waitingForCode = true;
    }

    /**
     * Notifies the form that a new country code was selected by the user.
     *
     * @param country  the selected country iso code (2 chars).
     * @param dialCode the dial code for this country
     */
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
                passwordlessInput.setText(previousInput);
                callback.onPasswordlessRetry();
            }
        } else if (id == R.id.com_auth0_lock_country_code_selector) {
            lockWidget.onCountryCodeChangeRequest();
        }
    }

    private void setTopMessage(String text) {
        if (text == null) {
            topMessage.setVisibility(View.GONE);
        } else {
            topMessage.setText(text);
            topMessage.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    public void onKeyboardStateChanged(boolean isOpen) {
        if (choosenMode == PasswordlessMode.SMS_LINK || choosenMode == PasswordlessMode.SMS_CODE) {
            countryCodeSelector.setVisibility(isOpen ? GONE : VISIBLE);
        }
    }

    public interface OnPasswordlessRetryListener {
        /**
         * Called when the form needs to remove the "Waiting for the code" view and show
         * the email/phone input again.
         */
        void onPasswordlessRetry();
    }
}