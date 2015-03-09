/*
 * SignUpFormFragment.java
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
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.api.APIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.LockProvider;
import com.auth0.lock.R;
import com.auth0.lock.error.AuthenticationErrorBuilder;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.error.SignUpAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.event.SignUpEvent;
import com.auth0.lock.validation.SignUpValidator;
import com.auth0.lock.widget.CredentialField;
import com.squareup.otto.Bus;

import java.util.Map;

public class SignUpFormFragment extends Fragment {

    private static final String AUTHENTICATION_PARAMETER_ARGUMENT = "AUTHENTICATION_PARAMETER_ARGUMENT";
    private static final String USE_EMAIL_SIGNUP_ARGUMENT = "USE_EMAIL";
    private static final String LOGIN_AFTER_SIGNUP_ARGUMENT = "LOGIN_AFTER_SIGN_UP";

    AuthenticationErrorBuilder errorBuilder;
    SignUpValidator validator;

    CredentialField usernameField;
    CredentialField passwordField;

    Button accessButton;
    ProgressBar progressBar;
    private boolean loginAfterSignUp;
    private boolean useEmail;
    private APIClient client;
    private Bus bus;
    private Map<String, Object> authenticationParameters;

    public SignUpFormFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        final Lock lock = getLock();
        client = lock.getAPIClient();
        bus = lock.getBus();
        errorBuilder = new SignUpAuthenticationErrorBuilder();
        useEmail = arguments == null || arguments.getBoolean(USE_EMAIL_SIGNUP_ARGUMENT);
        loginAfterSignUp = arguments == null || arguments.getBoolean(LOGIN_AFTER_SIGNUP_ARGUMENT);
        authenticationParameters = arguments != null ? (Map<String, Object>) arguments.getSerializable(AUTHENTICATION_PARAMETER_ARGUMENT) : null;
        validator = new SignUpValidator(useEmail);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sign_up_form, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameField = (CredentialField) view.findViewById(R.id.db_signup_username_field);
        if (!useEmail) {
            usernameField.setHint(R.string.username_placeholder);
            usernameField.setIconResource(R.drawable.ic_person);
            usernameField.setErrorIconResource(R.drawable.ic_person_error);
            usernameField.refresh();
        }
        passwordField = (CredentialField) view.findViewById(R.id.db_signup_password_field);
        accessButton = (Button) view.findViewById(R.id.db_access_button);
        progressBar = (ProgressBar) view.findViewById(R.id.db_signup_progress_indicator);

        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    signUp();
                }
                return false;
            }
        });

        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
    }

    public static SignUpFormFragment newFragment(boolean useEmail, boolean loginAfterSignUp, Map<String, Object> parameters) {
        Bundle arguments = new Bundle();
        arguments.putBoolean(USE_EMAIL_SIGNUP_ARGUMENT, useEmail);
        arguments.putBoolean(LOGIN_AFTER_SIGNUP_ARGUMENT, loginAfterSignUp);
        arguments.putSerializable(AUTHENTICATION_PARAMETER_ARGUMENT, (java.io.Serializable) parameters);
        SignUpFormFragment fragment = new SignUpFormFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    private Lock getLock() {
        LockProvider provider = (LockProvider) getActivity().getApplication();
        return provider.getLock();
    }

    private void signUp() {
        AuthenticationError error = validator.validateFrom(this);
        boolean valid = error == null;
        if (valid) {
            performSignUp();
        } else {
            bus.post(error);
        }
    }

    private void performSignUp() {
        accessButton.setEnabled(false);
        accessButton.setText("");
        progressBar.setVisibility(View.VISIBLE);
        final String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();
        if (loginAfterSignUp) {
            client.signUp(username, password, authenticationParameters, new AuthenticationCallback() {
                @Override
                public void onSuccess(UserProfile profile, Token token) {
                    bus.post(new AuthenticationEvent(profile, token));
                    accessButton.setEnabled(true);
                    accessButton.setText(R.string.db_login_btn_text);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Throwable error) {
                    bus.post(errorBuilder.buildFrom(error));
                    accessButton.setEnabled(true);
                    accessButton.setText(R.string.db_login_btn_text);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            client.createUser(username, password, authenticationParameters, new BaseCallback<Void>() {
                @Override
                public void onSuccess(Void payload) {
                    bus.post(new SignUpEvent(username));
                    accessButton.setEnabled(true);
                    accessButton.setText(R.string.db_login_btn_text);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Throwable error) {
                    bus.post(errorBuilder.buildFrom(error));
                    accessButton.setEnabled(true);
                    accessButton.setText(R.string.db_login_btn_text);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
