/*
 * PasscodeLoginFragment.java
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

package com.auth0.lock.fragment;


import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.R;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.validation.PasscodeValidator;
import com.auth0.lock.validation.Validator;
import com.auth0.lock.widget.CredentialField;
import com.squareup.otto.Subscribe;

import java.util.HashMap;
import java.util.Map;

public class MFACodeLoginFragment extends BaseTitledFragment {

    private static final String MESSAGE_FORMAT_ARGUMENT = "MESSAGE_FORMAT_ARGUMENT";
    private static final String USERNAME_ARGUMENT = "USERNAME_ARGUMENT";
    private static final String PASSWORD_ARGUMENT = "PASSWORD_ARGUMENT";

    private int messageFormatResId;
    private String username;
    private String password;
    private Validator validator;

    LoginAuthenticationErrorBuilder errorBuilder;
    Button accessButton;
    ProgressBar progressBar;
    CredentialField mfaCodeField;

    public static MFACodeLoginFragment newInstance(int messageFormatResId, String username, String password) {
        MFACodeLoginFragment fragment = new MFACodeLoginFragment();
        Bundle args = new Bundle();
        args.putInt(MESSAGE_FORMAT_ARGUMENT, messageFormatResId);
        args.putString(USERNAME_ARGUMENT, username);
        args.putString(PASSWORD_ARGUMENT, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        if (arguments != null) {
            messageFormatResId = getArguments().getInt(MESSAGE_FORMAT_ARGUMENT);
            username = arguments.getString(USERNAME_ARGUMENT);
            password = arguments.getString(PASSWORD_ARGUMENT);
        }
        errorBuilder = new LoginAuthenticationErrorBuilder();
        validator = new PasscodeValidator(R.id.com_auth0_passwordless_passcode_login_code_field, R.string.com_auth0_db_login_error_title, R.string.com_auth0_db_login_invalid_mfa_code_message);

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
        return inflater.inflate(R.layout.com_auth0_db_mfa_code_form, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView messageTextView = (TextView) view.findViewById(R.id.com_auth0_db_mfa_code_enter_code_message);
        String messageFormat = getString(messageFormatResId);
        messageTextView.setText(Html.fromHtml(String.format(messageFormat, username)));
        mfaCodeField = (CredentialField) view.findViewById(R.id.com_auth0_passwordless_passcode_login_code_field);
        accessButton = (Button) view.findViewById(R.id.com_auth0_passwordless_passcode_access_button);
        progressBar = (ProgressBar) view.findViewById(R.id.com_auth0_passwordless_passcode_login_progress_indicator);
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
        String mfaCode = mfaCodeField.getText().toString().trim();
        Map<String, Object> mfaParameters = new HashMap<>();
        mfaParameters.put("mfa_code", mfaCode);
        client.login(username, password)
                .addParameters(authenticationParameters)
                .addParameters(mfaParameters)
                .start(new AuthenticationCallback() {
                    @Override
                    public void onSuccess(UserProfile userProfile, Token token) {
                        bus.post(new AuthenticationEvent(userProfile, token));
                        accessButton.setEnabled(true);
                        accessButton.setText(R.string.com_auth0_db_login_btn_text);
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        bus.post(errorBuilder.buildFrom(throwable));
                        accessButton.setEnabled(true);
                        accessButton.setText(R.string.com_auth0_db_login_btn_text);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthenticationError(AuthenticationError error) {
        accessButton.setEnabled(true);
        accessButton.setText(R.string.com_auth0_db_login_btn_text);
        progressBar.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe
    public void onAuthentication(AuthenticationEvent event) {
        accessButton.setEnabled(true);
        accessButton.setText(R.string.com_auth0_db_login_btn_text);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    protected int getTitleResource() {
        return R.string.com_auth0_mfa_code_title_enter_code;
    }
}
