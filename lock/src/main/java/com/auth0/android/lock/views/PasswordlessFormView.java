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
import android.os.AsyncTask;
import android.support.v7.widget.AppCompatSpinner;
import android.widget.Button;

import com.auth0.android.lock.R;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.adapters.CountryCodeAdapter;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.utils.LoadCountriesTask;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PasswordlessFormView extends FormView {

    private static final String TAG = PasswordlessFormView.class.getSimpleName();
    private ValidatedInputView passwordlessInput;
    private PasswordlessMode choosenMode;
    private Button actionButton;
    private boolean waitingForCode;
    private String emailOrNumber;
    private AppCompatSpinner countryCodesSpinner;
    private LoadCountriesTask loadCountriesTask;

    public PasswordlessFormView(Context context, Bus lockBus, PasswordlessMode passwordlessMode) {
        super(context, lockBus);
        choosenMode = passwordlessMode;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_form_view, this);
        passwordlessInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_passwordless);
        countryCodesSpinner = (AppCompatSpinner) findViewById(R.id.com_auth0_lock_input_country_codes);

        if (choosenMode == PasswordlessMode.SMS_LINK || choosenMode == PasswordlessMode.SMS_CODE) {
            loadCountryCodes();
        }

        actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setOnClickListener(this);
        selectPasswordlessMode();
    }

    private void loadCountryCodes() {
        if (loadCountriesTask != null && !loadCountriesTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
            return;
        }
        loadCountriesTask = new LoadCountriesTask(getContext()) {
            @Override
            protected void onPostExecute(Map<String, String> result) {
                loadCountriesTask = null;
                if (result != null) {
                    final ArrayList<String> names = new ArrayList<>(result.keySet());
                    Collections.sort(names);
                    final List<Country> countries = new ArrayList<>(names.size());
                    for (String name : names) {
                        countries.add(new Country(name, result.get(name)));
                    }
                    getHandler().post(new Runnable() {
                        @Override
                        public void run() {
                            if (countryCodesSpinner != null) {
                                countryCodesSpinner.setAdapter(new CountryCodeAdapter(getContext(), countries));
                            }
                        }
                    });
                }
            }
        };
        loadCountriesTask.execute(LoadCountriesTask.COUNTRIES_JSON_FILE);
    }

    private void selectPasswordlessMode() {
        switch (choosenMode) {
            case EMAIL_CODE:
                passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);
                actionButton.setText(R.string.com_auth0_lock_action_send_code);
                countryCodesSpinner.setVisibility(GONE);
                break;
            case EMAIL_LINK:
                passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);
                actionButton.setText(R.string.com_auth0_lock_action_send_link);
                countryCodesSpinner.setVisibility(GONE);
                break;
            case SMS_CODE:
                passwordlessInput.setDataType(ValidatedInputView.DataType.PHONE_NUMBER);
                actionButton.setText(R.string.com_auth0_lock_action_send_code);
                countryCodesSpinner.setVisibility(VISIBLE);
                break;
            case SMS_LINK:
                passwordlessInput.setDataType(ValidatedInputView.DataType.PHONE_NUMBER);
                actionButton.setText(R.string.com_auth0_lock_action_send_link);
                countryCodesSpinner.setVisibility(VISIBLE);
                break;
        }
        passwordlessInput.clearInput();
    }

    @Override
    protected Object getActionEvent() {
        if (waitingForCode) {
            return new PasswordlessLoginEvent(choosenMode, emailOrNumber, getInputText());
        } else {
            countryCodesSpinner.setVisibility(GONE);
            PasswordlessLoginEvent event = new PasswordlessLoginEvent(choosenMode, getInputText());
            if (choosenMode == PasswordlessMode.EMAIL_CODE || choosenMode == PasswordlessMode.SMS_CODE) {
                waitingForCode = true;
                emailOrNumber = getInputText();
                passwordlessInput.setDataType(ValidatedInputView.DataType.NUMBER);
                passwordlessInput.clearInput();
                actionButton.setText(R.string.com_auth0_lock_action_login);
            } else {
                actionButton.setText(R.string.com_auth0_lock_action_click_link);
                actionButton.setEnabled(false);
            }
            return event;
        }
    }

    private String getInputText() {
        if (choosenMode == PasswordlessMode.SMS_CODE || choosenMode == PasswordlessMode.SMS_LINK) {
            if (countryCodesSpinner != null && countryCodesSpinner.getCount() > 0) {
                Country country = (Country) countryCodesSpinner.getSelectedItem();
                return country.getCode() + passwordlessInput.getText().replace(" ", "").replace("-", "");
            } else {
                return passwordlessInput.getText().replace(" ", "").replace("-", "");
            }
        } else {
            return passwordlessInput.getText().replace(" ", "");
        }
    }

    @Override
    protected boolean hasValidData() {
        return passwordlessInput.validate();
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
        } else {
            if (loadCountriesTask != null) {
                loadCountriesTask.cancel(true);
                loadCountriesTask = null;
            }
            return false;
        }
    }
}