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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.api.APIClient;
import com.auth0.api.ParameterBuilder;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Connection;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.LockProvider;
import com.auth0.lock.R;
import com.auth0.lock.credentials.CredentialStore;
import com.auth0.lock.credentials.CredentialStoreCallback;
import com.auth0.lock.error.AuthenticationErrorBuilder;
import com.auth0.lock.error.SignUpAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.SignUpEvent;
import com.auth0.lock.validation.SignUpValidator;
import com.auth0.lock.widget.CredentialField;
import com.squareup.otto.Bus;

import java.util.Map;

public class SignUpFormFragment extends Fragment {

    private static final String AUTHENTICATION_PARAMETER_ARGUMENT = "AUTHENTICATION_PARAMETER_ARGUMENT";
    private static final String USE_EMAIL_SIGNUP_ARGUMENT = "USE_EMAIL";
    private static final String LOGIN_AFTER_SIGNUP_ARGUMENT = "LOGIN_AFTER_SIGN_UP";
    private static final String TAG = SignUpFormFragment.class.getName();

    AuthenticationErrorBuilder errorBuilder;
    SignUpValidator validator;

    CredentialField usernameField;
    CredentialField emailField;
    CredentialField passwordField;

    Button accessButton;
    ProgressBar progressBar;
    private boolean loginAfterSignUp;
    private boolean useEmail;
    private boolean requiresUsername;
    private APIClient client;
    private Bus bus;
    private Map<String, Object> authenticationParameters;

    public SignUpFormFragment() {}

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
        final Connection connection = lock.getConfiguration().getDefaultDatabaseConnection();
        if (connection != null) {
            authenticationParameters = ParameterBuilder.newBuilder()
                    .setConnection(connection.getName())
                    .addAll(authenticationParameters)
                    .asDictionary();
            Log.d(TAG, "Specified DB connection with name " + connection.getName());
            requiresUsername = connection.booleanForKey("requires_username");
        }
        validator = new SignUpValidator(useEmail, requiresUsername);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_fragment_sign_up_form, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        emailField = (CredentialField) view.findViewById(R.id.com_auth0_db_signup_email_field);
        usernameField = (CredentialField) view.findViewById(R.id.com_auth0_db_signup_username_field);
        if (!requiresUsername && useEmail) {
            usernameField.setVisibility(View.GONE);
            View separator = view.findViewById(R.id.com_auth0_db_signup_username_separator);
            separator.setVisibility(View.GONE);
        }
        if (!useEmail && !requiresUsername) {
            emailField.setVisibility(View.GONE);
            usernameField.setNextFocusDownId(R.id.com_auth0_db_signup_password_field);
            View separator = view.findViewById(R.id.com_auth0_db_signup_email_separator);
            separator.setVisibility(View.GONE);
        }
        passwordField = (CredentialField) view.findViewById(R.id.com_auth0_db_signup_password_field);
        accessButton = (Button) view.findViewById(R.id.com_auth0_db_access_button);
        progressBar = (ProgressBar) view.findViewById(R.id.com_auth0_db_signup_progress_indicator);

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
        final String username = !useEmail || requiresUsername ? usernameField.getText().toString().trim() : null;
        final String email = useEmail || requiresUsername ? emailField.getText().toString().trim() : null;
        final String password = passwordField.getText().toString();
        if (loginAfterSignUp) {
            client.signUp(email, username, password, authenticationParameters, new AuthenticationCallback() {
                @Override
                public void onSuccess(final UserProfile profile, final Token token) {
                    CredentialStore store = getLock().getCredentialStore();
                    store.save(email, password, new CredentialStoreCallback() {
                        @Override
                        public void onSuccess() {
                            postAuthEvent();
                        }

                        @Override
                        public void onError(int errorCode, Throwable e) {
                            Log.w(TAG, "Failed to save credentials with code " + errorCode, e);
                            postAuthEvent();
                        }

                        private void postAuthEvent() {
                            bus.post(new AuthenticationEvent(profile, token));
                            accessButton.setEnabled(true);
                            accessButton.setText(R.string.com_auth0_db_login_btn_text);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }

                @Override
                public void onFailure(Throwable error) {
                    bus.post(errorBuilder.buildFrom(error));
                    accessButton.setEnabled(true);
                    accessButton.setText(R.string.com_auth0_db_login_btn_text);
                    progressBar.setVisibility(View.GONE);
                }
            });
        } else {
            client.createUser(email, username, password, authenticationParameters, new BaseCallback<Void>() {
                @Override
                public void onSuccess(Void payload) {
                    bus.post(new SignUpEvent(username));
                    accessButton.setEnabled(true);
                    accessButton.setText(R.string.com_auth0_db_login_btn_text);
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Throwable error) {
                    bus.post(errorBuilder.buildFrom(error));
                    accessButton.setEnabled(true);
                    accessButton.setText(R.string.com_auth0_db_login_btn_text);
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }
}
