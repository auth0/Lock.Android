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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.R;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.google.inject.Inject;

import roboguice.inject.InjectView;

public class DatabaseLoginFragment extends BaseTitledFragment {

    @Inject LoginAuthenticationErrorBuilder errorBuilder;

    @InjectView(tag = "db_login_username_field") EditText usernameField;
    @InjectView(tag = "db_login_password_field") EditText passwordField;

    @InjectView(tag = "db_access_button") Button accessButton;
    @InjectView(tag = "db_login_progress_indicator") ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_database_login, container, false);
    }

    @Override
    protected int getTitleResource() {
        return R.string.database_login_title;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        accessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        Button signUpButton = (Button) view.findViewById(R.id.db_signup_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provider.getBus().post(NavigationEvent.SIGN_UP);
            }
        });
        Button resetPassword = (Button) view.findViewById(R.id.db_reset_pass_button);
        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                provider.getBus().post(NavigationEvent.RESET_PASSWORD);
            }
        });
    }

    private void performLogin() {
        accessButton.setEnabled(false);
        accessButton.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        client.login(username, password, null, new AuthenticationCallback() {

            @Override
            public void onSuccess(UserProfile userProfile, Token token) {
                provider.getBus().post(new AuthenticationEvent(userProfile, token));
                accessButton.setEnabled(true);
                accessButton.setText(R.string.db_login_btn_text);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable throwable) {
                provider.getBus().post(errorBuilder.buildFrom(throwable));
                accessButton.setEnabled(true);
                accessButton.setText(R.string.db_login_btn_text);
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}
