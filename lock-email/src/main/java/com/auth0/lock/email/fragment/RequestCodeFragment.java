/*
 * RequestCodeFragment.java
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

package com.auth0.lock.email.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.auth0.lock.email.R;
import com.auth0.lock.email.event.EmailVerificationCodeRequestedEvent;
import com.auth0.lock.email.event.EmailVerificationCodeSentEvent;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.fragment.BaseTitledFragment;
import com.auth0.lock.validation.EmailValidator;
import com.auth0.lock.validation.Validator;
import com.auth0.lock.widget.CredentialField;
import com.squareup.otto.Subscribe;

public class RequestCodeFragment extends BaseTitledFragment {

    private static final String TAG = RequestCodeFragment.class.getName();
    private static final String LAST_EMAIL_KEY = "LAST_EMAIL_NUMBER_KEY";

    Validator validator;
    CredentialField emailField;
    Button sendButton;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus.register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
    }

    @Override
    protected int getTitleResource() {
        return R.string.com_auth0_email_title_send_passcode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_email_fragment_request_code, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        validator = new EmailValidator(R.id.com_auth0_email_email_field, R.string.com_auth0_email_send_code_error_tile, R.string.com_auth0_email_send_code_no_phone_message);
        emailField = (CredentialField) view.findViewById(R.id.com_auth0_email_email_field);
        final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        final String storedEmail = preferences.getString(LAST_EMAIL_KEY, null);
        emailField.setText(storedEmail);
        sendButton = (Button) view.findViewById(R.id.com_auth0_email_access_button);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestSmsCode();
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.com_auth0_email_send_code_progress_indicator);
        final Button hasCodeButton = (Button) view.findViewById(R.id.com_auth0_email_already_has_code_button);
        hasCodeButton.setVisibility(storedEmail != null ? View.VISIBLE : View.GONE);
        hasCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences preferences = getActivity().getPreferences(Context.MODE_PRIVATE);
                final String phoneNumber = preferences.getString(LAST_EMAIL_KEY, null);
                if (phoneNumber != null) {
                    bus.post(new EmailVerificationCodeSentEvent(phoneNumber));
                }
            }
        });
    }

    private void requestSmsCode() {
        AuthenticationError error = validator.validateFrom(this);
        if (error == null) {
            sendRequestCode();
        } else {
            bus.post(error);
        }
    }

    private void sendRequestCode() {
        sendButton.setEnabled(false);
        sendButton.setText("");
        progressBar.setVisibility(View.VISIBLE);

        final String email = emailField.getText().toString();
        bus.post(new EmailVerificationCodeRequestedEvent(email));
    }

    @SuppressWarnings("unused")
    @Subscribe public void onPasscodeSentEvent(EmailVerificationCodeSentEvent event) {
        final SharedPreferences.Editor editor = getActivity().getPreferences(Context.MODE_PRIVATE).edit();
        editor.putString(LAST_EMAIL_KEY, event.getEmail());
        editor.apply();
        sendButton.setEnabled(true);
        sendButton.setText(R.string.com_auth0_email_send_passcode_btn_text);
        progressBar.setVisibility(View.GONE);
    }

    @SuppressWarnings("unused")
    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        sendButton.setEnabled(true);
        sendButton.setText(R.string.com_auth0_email_send_passcode_btn_text);
        progressBar.setVisibility(View.GONE);
    }
}
