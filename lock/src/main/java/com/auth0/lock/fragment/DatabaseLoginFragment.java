/*
 * DatabaseLoginFragment.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.api.ParameterBuilder;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.core.Connection;
import com.auth0.core.Strategy;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Configuration;
import com.auth0.lock.Lock;
import com.auth0.lock.R;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.EnterpriseAuthenticationRequest;
import com.auth0.lock.event.IdentityProviderAuthenticationRequestEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.util.DomainMatcher;
import com.auth0.lock.validation.LoginValidator;
import com.auth0.lock.widget.CredentialField;

public class DatabaseLoginFragment extends BaseTitledFragment {

    public static final String AD_ENTERPRISE_CONNECTION_ARGUMENT = "AD_ENTERPRISE_CONNECTION_ARGUMENT";
    public static final String DEFAULT_CONNECTION_ARGUMENT = "DEFAULT_CONNECTION_ARGUMENT";
    public static final String IS_MAIN_LOGIN_ARGUMENT = "IS_MAIN_LOGIN_ARGUMENT";
    private static final String TAG = DatabaseLoginFragment.class.getName();

    LoginAuthenticationErrorBuilder errorBuilder;
    LoginValidator validator;
    DomainMatcher matcher;

    CredentialField usernameField;
    CredentialField passwordField;
    View separator;
    View singleSignOnMessage;

    Button accessButton;
    ProgressBar progressBar;

    private boolean showADForm;
    private boolean hasDB;
    private boolean showSignUp;
    private boolean showResetPassword;
    private boolean showCancel;
    private Connection enterpriseConnection;
    private Connection defaultConnection;
    private boolean requiresUsername;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments() != null ? getArguments() : new Bundle();
        final Lock lock = Lock.getLock(getActivity());
        Configuration configuration = lock.getConfiguration();
        if (arguments.containsKey(AD_ENTERPRISE_CONNECTION_ARGUMENT)) {
            enterpriseConnection = arguments.getParcelable(AD_ENTERPRISE_CONNECTION_ARGUMENT);
            defaultConnection = enterpriseConnection;
            showADForm = true;
            useEmail = false;
        } else if (arguments.containsKey(DEFAULT_CONNECTION_ARGUMENT)) {
            defaultConnection = arguments.getParcelable(DEFAULT_CONNECTION_ARGUMENT);
        } else {
            defaultConnection = configuration.getDefaultDatabaseConnection();
        }
        String connection = defaultConnection != null ? defaultConnection.getName() : null;
        requiresUsername = defaultConnection != null && defaultConnection.booleanForKey("requires_username");
        authenticationParameters = ParameterBuilder.newBuilder()
                .setConnection(connection)
                .addAll(authenticationParameters)
                .asDictionary();
        Log.d(TAG, "Specified default connection " + connection);

        showCancel = !arguments.getBoolean(IS_MAIN_LOGIN_ARGUMENT);
        hasDB = configuration.getDefaultDatabaseConnection() != null;
        useEmail = useEmail && hasDB;
        showSignUp = hasDB && configuration.getDefaultDatabaseConnection().booleanForKey("showSignup") && lock.isSignUpEnabled();
        showResetPassword = hasDB && configuration.getDefaultDatabaseConnection().booleanForKey("showForgot") && lock.isChangePasswordEnabled();
        errorBuilder = new LoginAuthenticationErrorBuilder();
        validator = new LoginValidator(useEmail, requiresUsername);
        matcher = new DomainMatcher(configuration.getEnterpriseStrategies());
        matcher.filterConnection(defaultConnection);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (showADForm) {
            return inflater.inflate(R.layout.com_auth0_fragment_enterprise_login, container, false);
        }
        return inflater.inflate(R.layout.com_auth0_fragment_database_login, container, false);
    }

    @Override
    protected int getTitleResource() {
        return R.string.com_auth0_database_login_title;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameField = (CredentialField) view.findViewById(R.id.com_auth0_db_login_username_field);
        if (requiresUsername) {
            usernameField.setHint(R.string.com_auth0_username_email_placeholder);
        } if (!useEmail) {
            usernameField.setHint(R.string.com_auth0_username_placeholder);
            usernameField.setIconResource(R.drawable.com_auth0_ic_person);
            usernameField.setErrorIconResource(R.drawable.com_auth0_ic_person_error);
            usernameField.refresh();
        }
        if (!showADForm) {
            usernameField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    final boolean matches = matcher.matches(s.toString());
                    if (matches) {
                        final Connection connection = matcher.getConnection();
                        Log.i(DatabaseLoginFragment.class.getName(), "Matched with domain of connection " + connection.getName());
                        final String domain = connection.getValueForKey("domain");
                        String singleSignOnButtonText = String.format(getString(R.string.com_auth0_db_single_sign_on_button), domain.toUpperCase());
                        accessButton.setText(singleSignOnButtonText);
                    } else {
                        accessButton.setText(R.string.com_auth0_db_login_btn_text);
                    }
                    final int passwordVisibility = matches ? View.GONE : View.VISIBLE;
                    passwordField.setVisibility(passwordVisibility);
                    separator.setVisibility(passwordVisibility);
                    singleSignOnMessage.setVisibility(!matches ? View.GONE : View.VISIBLE);
                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            });
        } else {
            TextView message = (TextView) view.findViewById(R.id.com_auth0_db_enterprise_login_message);
            message.setText(String.format(getString(R.string.com_auth0_db_enterprise_login_message), enterpriseConnection.getValueForKey("domain")));
            Button cancelButton = (Button) view.findViewById(R.id.com_auth0_db_enterprise_cancel_button);
            cancelButton.setVisibility(showCancel ? View.VISIBLE : View.GONE);
        }
        separator = view.findViewById(R.id.com_auth0_db_separator_view);
        passwordField = (CredentialField) view.findViewById(R.id.com_auth0_db_login_password_field);
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        singleSignOnMessage = view.findViewById(R.id.com_auth0_single_sign_on_view);

        accessButton = (Button) view.findViewById(R.id.com_auth0_db_access_button);
        progressBar = (ProgressBar) view.findViewById(R.id.com_auth0_db_login_progress_indicator);
        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        initNavButtons(view);
    }

    private void initNavButtons(View view) {
        Button signUpButton = (Button) view.findViewById(R.id.com_auth0_db_signup_button);
        if (signUpButton != null) {
            if (!showSignUp) {
                signUpButton.setVisibility(View.GONE);
            } else {
                signUpButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bus.post(NavigationEvent.SIGN_UP);
                    }
                });
            }
        }

        Button resetPassword = (Button) view.findViewById(R.id.com_auth0_db_reset_pass_button);
        if (resetPassword != null) {
            if (!showResetPassword) {
                resetPassword.setVisibility(View.GONE);
            } else {
                resetPassword.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bus.post(NavigationEvent.RESET_PASSWORD);
                    }
                });
            }
        }

        Button cancelButton = (Button) view.findViewById(R.id.com_auth0_db_enterprise_cancel_button);
        if (cancelButton != null) {
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bus.post(NavigationEvent.BACK);
                }
            });
        }
    }

    private void login() {
        final Connection connection = matcher.getConnection();
        if (connection != null) {
            final Configuration configuration = Lock.getLock(getActivity()).getConfiguration();
            final Strategy strategy = configuration.getApplication().strategyForConnection(connection);
            if (strategy.isResourceOwnerEnabled()) {
                bus.post(new EnterpriseAuthenticationRequest(connection));
            } else {
                bus.post(new IdentityProviderAuthenticationRequestEvent(connection.getName()));
            }
            return;
        }

        if (defaultConnection == null && !hasDB) {
            bus.post(new AuthenticationError(R.string.com_auth0_db_login_error_title, R.string.com_auth0_enterprise_no_connection_message));
            return;
        }

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
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();
        client.login(username, password)
                .addParameters(authenticationParameters)
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
}
