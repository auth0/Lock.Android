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

import android.content.Context;
import android.content.SharedPreferences;
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
    private static final int CODE_TTL = 1000 * 60 * 2;
    private static final String LAST_PASSWORDLESS_TIME_KEY = "last_passwordless_time";
    private static final String LAST_PASSWORDLESS_EMAIL_NUMBER_KEY = "last_passwordless_email_number";
    private static final String LAST_PASSWORDLESS_COUNTRY_KEY = "last_passwordless_country";
    private static final String LOCK_PREFERENCES_NAME = "Lock";
    private static final String COUNTRY_DATA_DIV = "@";
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
    private TextView gotCodeButton;
    private SharedPreferences sp;

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
        sp = getContext().getSharedPreferences(LOCK_PREFERENCES_NAME, Context.MODE_PRIVATE);
        topMessage = (TextView) findViewById(R.id.com_auth0_lock_text);
        resendButton = (TextView) findViewById(R.id.com_auth0_lock_resend);
        resendButton.setOnClickListener(this);
        passwordlessInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_passwordless);
        passwordlessInput.setOnEditorActionListener(this);
        countryCodeSelector = (CountryCodeSelectorView) findViewById(R.id.com_auth0_lock_country_code_selector);
        countryCodeSelector.setOnClickListener(this);
        gotCodeButton = (TextView) findViewById(R.id.com_auth0_lock_got_code);
        gotCodeButton.setOnClickListener(this);

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
        if (shouldShowGotCodeButton()) {
            gotCodeButton.setVisibility(VISIBLE);
            reloadPreviouslyUsedData();
        }
    }

    private boolean shouldShowGotCodeButton() {
        long d = sp.getLong(LAST_PASSWORDLESS_TIME_KEY, 0) - System.currentTimeMillis() + CODE_TTL;
        return d > 0;
    }

    private void reloadPreviouslyUsedData() {
        String text = sp.getString(LAST_PASSWORDLESS_EMAIL_NUMBER_KEY, "");
        submittedEmailOrNumber = text;
        String countryInfo = sp.getString(LAST_PASSWORDLESS_COUNTRY_KEY, null);
        if (countryInfo != null) {
            String isoCode = countryInfo.split(COUNTRY_DATA_DIV)[0];
            String dialCode = countryInfo.split(COUNTRY_DATA_DIV)[1];
            if (text.startsWith(dialCode)) {
                text = text.substring(dialCode.length());
            }
            countryCodeSelector.setSelectedCountry(new Country(isoCode, dialCode));
        }
        passwordlessInput.setText(text);
    }

    private void persistRecentlyUsedEmailOrNumber() {
        String countryData = null;
        if (choosenMode == PasswordlessMode.SMS_LINK || choosenMode == PasswordlessMode.SMS_CODE) {
            countryData = countryCodeSelector.getSelectedCountry().getIsoCode() + COUNTRY_DATA_DIV + countryCodeSelector.getSelectedCountry().getDialCode();
        }
        sp.edit()
                .putLong(LAST_PASSWORDLESS_TIME_KEY, System.currentTimeMillis())
                .putString(LAST_PASSWORDLESS_EMAIL_NUMBER_KEY, submittedEmailOrNumber)
                .putString(LAST_PASSWORDLESS_COUNTRY_KEY, countryData)
                .apply();
    }

    /**
     * Notifies the form that the authentication has succeed, for it to erase state data.
     */
    public void onAuthenticationSucceed() {
        sp.edit()
                .putLong(LAST_PASSWORDLESS_TIME_KEY, 0)
                .putString(LAST_PASSWORDLESS_EMAIL_NUMBER_KEY, "")
                .putString(LAST_PASSWORDLESS_COUNTRY_KEY, null)
                .apply();
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
        codeSent(true);
    }

    private void codeSent(boolean persistTime) {
//        submittedEmailOrNumber = getInputText();
        if (persistTime) {
            persistRecentlyUsedEmailOrNumber();
        }
        countryCodeSelector.setVisibility(GONE);
        setTopMessage(String.format(getResources().getString(sentMessage), submittedEmailOrNumber));
        resendButton.setVisibility(VISIBLE);
        gotCodeButton.setVisibility(GONE);
        passwordlessInput.setDataType(ValidatedInputView.DataType.NUMBER);
        passwordlessInput.clearInput();

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
        } else if (id == R.id.com_auth0_lock_got_code) {
            codeSent(false);
        } else if (id == R.id.com_auth0_lock_country_code_selector) {
            lockWidget.onCountryCodeChangeRequest();
        }
    }

    private void setTopMessage(String text) {
        topMessage.setText(text);
        topMessage.setVisibility(text == null ? GONE : VISIBLE);
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        if (!waitingForCode && (choosenMode == PasswordlessMode.SMS_LINK || choosenMode == PasswordlessMode.SMS_CODE)) {
            countryCodeSelector.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (waitingForCode) {
            resendButton.setVisibility(isOpen ? GONE : VISIBLE);
        } else if (shouldShowGotCodeButton()) {
            gotCodeButton.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (topMessage.getText().length() > 0) {
            topMessage.setVisibility(isOpen ? GONE : VISIBLE);
        }
    }

    /**
     * Getter for the "waiting for code" state.
     *
     * @return Whether the form is waiting for the user to input a code or not.
     */
    public boolean isWaitingForCode() {
        return waitingForCode;
    }

    public interface OnPasswordlessRetryListener {
        /**
         * Called when the form needs to remove the "Waiting for the code" view and show
         * the email/phone input again.
         */
        void onPasswordlessRetry();
    }
}