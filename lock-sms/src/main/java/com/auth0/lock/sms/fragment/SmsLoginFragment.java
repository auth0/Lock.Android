/*
 * SmsLoginFragment.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.lock.sms.fragment;


import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.fragment.BaseTitledFragment;
import com.auth0.lock.sms.R;
import com.auth0.lock.sms.event.LoginRequestEvent;
import com.auth0.lock.sms.validation.SmsPasscodeValidator;
import com.auth0.lock.validation.Validator;
import com.auth0.lock.widget.CredentialField;
import com.squareup.otto.Subscribe;

public class SmsLoginFragment extends BaseTitledFragment {

    private static final String PHONE_NUMBER_ARGUMENT = "PHONE_NUMBER_ARGUMENT";

    private String phoneNumber;
    private Validator validator;

    Button accessButton;
    ProgressBar progressBar;
    CredentialField passcodeField;

    public static SmsLoginFragment newInstance(String phoneNumber) {
        SmsLoginFragment fragment = new SmsLoginFragment();
        Bundle args = new Bundle();
        args.putString(PHONE_NUMBER_ARGUMENT, phoneNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            phoneNumber = arguments.getString(PHONE_NUMBER_ARGUMENT);
        }
        validator = new SmsPasscodeValidator(R.id.com_auth0_sms_login_code_field, R.string.com_auth0_sms_login_error_title, R.string.com_auth0_sms_login_invalid_passcode_message);

        bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        bus.unregister(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_fragment_sms_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button noCodeButton = (Button) view.findViewById(R.id.com_auth0_sms_no_code_button);
        noCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(NavigationEvent.BACK);
            }
        });
        TextView messageTextView = (TextView) view.findViewById(R.id.com_auth0_sms_enter_code_message);
        String messageFormat = getString(R.string.com_auth0_sms_login_message);
        messageTextView.setText(Html.fromHtml(String.format(messageFormat, phoneNumber)));
        passcodeField = (CredentialField) view.findViewById(R.id.com_auth0_sms_login_code_field);
        accessButton = (Button) view.findViewById(R.id.com_auth0_sms_access_button);
        progressBar = (ProgressBar) view.findViewById(R.id.com_auth0_sms_login_progress_indicator);
        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
    }

    private void login() {
        AuthenticationError error = validator.validateFrom(this);
        boolean valid = error == null;
        if (valid) {
            performLogin();
        } else {
            bus.post(error);
        }
    }

    private void performLogin() {
        accessButton.setEnabled(false);
        accessButton.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String passcode = passcodeField.getText().toString();
        bus.post(new LoginRequestEvent(phoneNumber, passcode));
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthenticationError(AuthenticationError error) {
        accessButton.setEnabled(true);
        accessButton.setText(R.string.com_auth0_sms_login_access_btn_text);
        progressBar.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        accessButton.setEnabled(true);
        accessButton.setText(R.string.com_auth0_sms_login_access_btn_text);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected int getTitleResource() {
        return R.string.com_auth0_sms_title_enter_passcode;
    }
}
