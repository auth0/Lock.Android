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
import android.widget.TextView;

import com.auth0.api.APIClient;
import com.auth0.api.AuthenticationCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.provider.BusProvider;
import com.google.inject.Inject;

import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class DatabaseLoginFragment extends RoboFragment {

    @Inject private APIClient client;
    @Inject private BusProvider provider;

    @InjectView(tag = "db_login_username_field") private EditText usernameField;
    @InjectView(tag = "db_login_password_field") private EditText passwordField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_database_login, container, false);
        TextView titleView = (TextView) rootView.findViewById(R.id.title_textView);
        titleView.setText(R.string.database_login_title);
        Button accessButton = (Button) rootView.findViewById(R.id.db_access_button);
        accessButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                performLogin();
            }
        });
        return rootView;
    }

    private void performLogin() {
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();
        client.login(username, password, null, new AuthenticationCallback() {

            @Override
            public void onSuccess(UserProfile userProfile, Token token) {
                provider.getBus().post(new AuthenticationEvent(userProfile, token));
            }

            @Override
            public void onFailure(Throwable throwable) {
                AuthenticationError error = new AuthenticationError(R.string.db_login_error_title, R.string.db_login_error_message, throwable);
                provider.getBus().post(error);
            }
        });
    }
}
