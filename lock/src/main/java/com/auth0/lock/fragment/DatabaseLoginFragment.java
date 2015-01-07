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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.core.Connection;
import com.auth0.core.Strategies;
import com.auth0.core.Strategy;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.R;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.IdentityProviderAuthenticationEvent;
import com.auth0.lock.event.IdentityProviderAuthenticationRequestEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.util.DomainMatcher;
import com.auth0.lock.validation.LoginValidator;
import com.auth0.lock.widget.CredentialField;

public class DatabaseLoginFragment extends BaseTitledFragment {

    LoginAuthenticationErrorBuilder errorBuilder;
    LoginValidator validator;
    DomainMatcher matcher;

    CredentialField usernameField;
    CredentialField passwordField;
    View separator;
    View singleSignOnMessage;

    Button accessButton;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        errorBuilder = new LoginAuthenticationErrorBuilder();
        validator = new LoginValidator(useEmail);
        matcher = new DomainMatcher(client.getApplication().getEnterpriseStrategies());
    }

    @Override
    protected int getTitleResource() {
        return R.string.database_login_title;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameField = (CredentialField) view.findViewById(R.id.db_login_username_field);
        if (!useEmail) {
            usernameField.setHint(R.string.username_placeholder);
            usernameField.setIconResource(R.drawable.ic_person);
            usernameField.setErrorIconResource(R.drawable.ic_person_error);
            usernameField.refresh();
        }
        usernameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                final boolean matches = matcher.matches(s.toString()    );
                if (matches) {
                    final Connection connection = matcher.getConnection();
                    Log.i(DatabaseLoginFragment.class.getName(), "Matched with domain of connection " + connection.getName());
                    final String domain = connection.getValueForKey("domain");
                    String singleSignOnButtonText = String.format(getString(R.string.db_single_sign_on_button), domain.toUpperCase());
                    accessButton.setText(singleSignOnButtonText);
                } else {
                    accessButton.setText(R.string.db_login_btn_text);
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
        separator = view.findViewById(R.id.db_separator_view);
        passwordField = (CredentialField) view.findViewById(R.id.db_login_password_field);
        singleSignOnMessage = view.findViewById(R.id.single_sign_on_view);
        accessButton = (Button) view.findViewById(R.id.db_access_button);
        progressBar = (ProgressBar) view.findViewById(R.id.db_login_progress_indicator);
        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    login();
                }
                return false;
            }
        });

        Button signUpButton = (Button) view.findViewById(R.id.db_signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(NavigationEvent.SIGN_UP);
            }
        });
        Button resetPassword = (Button) view.findViewById(R.id.db_reset_pass_button);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(NavigationEvent.RESET_PASSWORD);
            }
        });
    }

    private void login() {
        final Connection connection = matcher.getConnection();
        if (connection != null) {
            Strategy strategy = client.getApplication().strategyForConnection(connection);
            if (Strategies.ActiveDirectory.getName().equals(strategy.getName())) {
                //Show DB Form for AD
            } else {
                bus.post(new IdentityProviderAuthenticationRequestEvent(connection.getName()));
            }
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
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        client.login(username, password, authenticationParameters, new AuthenticationCallback() {

            @Override
            public void onSuccess(UserProfile userProfile, Token token) {
                bus.post(new AuthenticationEvent(userProfile, token));
                accessButton.setEnabled(true);
                accessButton.setText(R.string.db_login_btn_text);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable throwable) {
                bus.post(errorBuilder.buildFrom(throwable));
                accessButton.setEnabled(true);
                accessButton.setText(R.string.db_login_btn_text);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
