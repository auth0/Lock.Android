/*
 * PasswordlessSendCodeFormView.java
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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;

public class PasswordlessRequestCodeFormView extends FormView implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = PasswordlessRequestCodeFormView.class.getSimpleName();

    private final LockWidgetPasswordless lockWidget;
    private final OnAlreadyGotCodeListener listener;
    private ValidatedInputView passwordlessInput;
    private PasswordlessMode choosenMode;
    private TextView topMessage;
    private CountryCodeSelectorView countryCodeSelector;
    private TextView gotCodeButton;
    private String submittedEmailOrCode;

    public PasswordlessRequestCodeFormView(LockWidgetPasswordless lockWidget, @NonNull OnAlreadyGotCodeListener listener) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        this.listener = listener;
        choosenMode = lockWidget.getConfiguration().getPasswordlessMode();
        boolean showTitle = lockWidget.getConfiguration().getSocialStrategies().isEmpty();
        Log.v(TAG, "New instance with mode " + choosenMode);
        init(showTitle);
    }

    private void init(boolean showTitle) {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_request_code_form_view, this);
        topMessage = (TextView) findViewById(R.id.com_auth0_lock_text);
        passwordlessInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_passwordless);
        passwordlessInput.setOnEditorActionListener(this);
        countryCodeSelector = (CountryCodeSelectorView) findViewById(R.id.com_auth0_lock_country_code_selector);
        countryCodeSelector.setOnClickListener(this);
        gotCodeButton = (TextView) findViewById(R.id.com_auth0_lock_got_code);
        gotCodeButton.setOnClickListener(this);

        selectPasswordlessMode(showTitle);
    }

    private void selectPasswordlessMode(boolean showTitle) {
        int titleMessage = 0;
        switch (choosenMode) {
            case EMAIL_CODE:
                titleMessage = R.string.com_auth0_lock_title_passwordless_email;
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
        topMessage.setVisibility(showTitle ? VISIBLE : GONE);
        topMessage.setText(showTitle ? getResources().getString(titleMessage) : null);

        if (listener.shouldShowGotCodeButton()) {
            Log.v(TAG, "Showing the Got Code button");
            gotCodeButton.setVisibility(VISIBLE);
        }
    }

    @Override
    public Object getActionEvent() {
        String emailOrNumber = getInputText();
        if (choosenMode == PasswordlessMode.SMS_CODE || choosenMode == PasswordlessMode.SMS_LINK) {
            setLastEmailOrNumber(countryCodeSelector.getSelectedCountry().getDialCode() + emailOrNumber);
            Log.d(TAG, "Starting a SMS Passwordless flow");
            return PasswordlessLoginEvent.requestCode(choosenMode, emailOrNumber, countryCodeSelector.getSelectedCountry());
        } else {
            setLastEmailOrNumber(emailOrNumber);
            Log.d(TAG, "Starting an Email Passwordless flow");
            return PasswordlessLoginEvent.requestCode(choosenMode, emailOrNumber);
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
     * Notifies the form that a new country code was selected by the user.
     *
     * @param isoCode  the selected country iso code (2 chars).
     * @param dialCode the dial code for this country
     */
    @SuppressWarnings("unused")
    public void onCountryCodeSelected(String isoCode, String dialCode) {
        Country selectedCountry = new Country(isoCode, dialCode);
        countryCodeSelector.setSelectedCountry(selectedCountry);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.com_auth0_lock_got_code) {
            listener.onAlreadyGotCode(submittedEmailOrCode);
        } else if (id == R.id.com_auth0_lock_country_code_selector) {
            lockWidget.onCountryCodeChangeRequest();
        }
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
        if (choosenMode == PasswordlessMode.SMS_LINK || choosenMode == PasswordlessMode.SMS_CODE) {
            countryCodeSelector.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (listener.shouldShowGotCodeButton()) {
            gotCodeButton.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (topMessage.getText().length() > 0) {
            topMessage.setVisibility(isOpen ? GONE : VISIBLE);
        }
    }

    public void setInputText(String text) {
        passwordlessInput.setText(text);
    }

    public void showGotCodeButton() {
        gotCodeButton.setVisibility(VISIBLE);
    }

    public void setLastEmailOrNumber(String emailOrNumber) {
        submittedEmailOrCode = emailOrNumber;
    }

    public interface OnAlreadyGotCodeListener {
        void onAlreadyGotCode(String emailOrNumber);

        boolean shouldShowGotCodeButton();
    }
}