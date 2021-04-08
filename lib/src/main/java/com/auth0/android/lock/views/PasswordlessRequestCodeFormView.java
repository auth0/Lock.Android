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

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.internal.configuration.PasswordlessMode;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;

import static com.auth0.android.lock.internal.configuration.PasswordlessMode.DISABLED;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_LINK;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_LINK;

@SuppressLint("ViewConstructor")
public class PasswordlessRequestCodeFormView extends FormView implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = PasswordlessRequestCodeFormView.class.getSimpleName();

    private final LockWidgetPasswordless lockWidget;
    private ValidatedInputView passwordlessInput;
    @PasswordlessMode
    private final int passwordlessMode;
    private TextView topMessage;
    private CountryCodeSelectorView countryCodeSelector;

    public PasswordlessRequestCodeFormView(@NonNull LockWidgetPasswordless lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        passwordlessMode = lockWidget.getConfiguration().getPasswordlessMode();
        boolean showTitle = lockWidget.getConfiguration().getSocialConnections().isEmpty();
        Log.v(TAG, "New instance with mode " + passwordlessMode);
        init(showTitle);
    }

    private void init(boolean showTitle) {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_request_code_form_view, this);
        topMessage = findViewById(R.id.com_auth0_lock_text);
        passwordlessInput = findViewById(R.id.com_auth0_lock_input_passwordless);
        passwordlessInput.setOnEditorActionListener(this);
        countryCodeSelector = findViewById(R.id.com_auth0_lock_country_code_selector);
        countryCodeSelector.setOnClickListener(this);

        selectPasswordlessMode(showTitle);
    }

    private void selectPasswordlessMode(boolean showTitle) {
        int titleMessage = 0;
        switch (passwordlessMode) {
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
                passwordlessInput.setDataType(ValidatedInputView.DataType.MOBILE_PHONE);
                countryCodeSelector.setVisibility(VISIBLE);
                break;
            case SMS_LINK:
                titleMessage = R.string.com_auth0_lock_title_passwordless_sms;
                passwordlessInput.setDataType(ValidatedInputView.DataType.MOBILE_PHONE);
                countryCodeSelector.setVisibility(VISIBLE);
                break;
            case DISABLED:
                break;
        }
        passwordlessInput.setVisibility(VISIBLE);
        passwordlessInput.clearInput();
        topMessage.setVisibility(showTitle ? VISIBLE : GONE);
        topMessage.setText(showTitle ? getResources().getString(titleMessage) : null);
    }

    @NonNull
    @Override
    public Object getActionEvent() {
        String emailOrNumber = getInputText();
        if (passwordlessMode == SMS_CODE || passwordlessMode == SMS_LINK) {
            Log.d(TAG, "Starting a SMS Passwordless flow");
            //noinspection ConstantConditions
            return PasswordlessLoginEvent.requestCode(passwordlessMode, emailOrNumber, countryCodeSelector.getSelectedCountry());
        } else {
            Log.d(TAG, "Starting an Email Passwordless flow");
            return PasswordlessLoginEvent.requestCode(passwordlessMode, emailOrNumber);
        }
    }

    @SuppressLint("KotlinPropertyAccess")
    private String getInputText() {
        return passwordlessInput.getText().replace(" ", "");
    }

    @Override
    public boolean validateForm() {
        return passwordlessInput.validate();
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
    public void onCountryCodeSelected(@NonNull String isoCode, @NonNull String dialCode) {
        Country selectedCountry = new Country(isoCode, dialCode);
        countryCodeSelector.setSelectedCountry(selectedCountry);
    }

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == R.id.com_auth0_lock_country_code_selector) {
            lockWidget.onCountryCodeChangeRequest();
        }
    }

    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    public void setInputText(@NonNull String text) {
        passwordlessInput.setText(text);
    }

}