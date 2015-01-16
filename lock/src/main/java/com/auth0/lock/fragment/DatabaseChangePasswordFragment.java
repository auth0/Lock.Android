/*
 * DatabaseChangePasswordFragment.java
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.api.callback.BaseCallback;
import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.ChangePasswordEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.validation.ResetPasswordValidator;
import com.auth0.lock.validation.Validator;
import com.auth0.lock.widget.CredentialField;

public class DatabaseChangePasswordFragment extends BaseTitledFragment {

    CredentialField usernameField;
    CredentialField passwordField;
    CredentialField repeatPasswordField;

    Button sendButton;
    ProgressBar progressBar;

    private Validator validator;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_database_change_password, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        usernameField = (CredentialField) view.findViewById(R.id.db_change_password_username_field);
        if (!useEmail) {
            usernameField.setHint(R.string.username_placeholder);
            usernameField.setIconResource(R.drawable.ic_person);
            usernameField.setErrorIconResource(R.drawable.ic_person_error);
            usernameField.refresh();
        }
        passwordField = (CredentialField) view.findViewById(R.id.db_change_password_password_field);
        repeatPasswordField = (CredentialField) view.findViewById(R.id.db_change_password_repeat_password_field);
        sendButton = (Button) view.findViewById(R.id.db_reset_button);
        progressBar = (ProgressBar) view.findViewById(R.id.db_change_password_progress_indicator);
        Button cancelButton = (Button) view.findViewById(R.id.db_change_password_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(NavigationEvent.BACK);
            }
        });
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changePassword();
            }
        });
        repeatPasswordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    changePassword();
                }
                return false;
            }
        });
        validator = new ResetPasswordValidator(useEmail);
    }

    @Override
    protected int getTitleResource() {
        return R.string.database_change_password_title;
    }

    private void changePassword() {
        AuthenticationError error = validator.validateFrom(this);
        boolean valid = error == null;
        if (valid) {
            performChange();
        } else {
            bus.post(error);
        }
    }
    private void performChange() {
        sendButton.setEnabled(false);
        sendButton.setText("");
        progressBar.setVisibility(View.VISIBLE);
        String username = usernameField.getText().toString().trim();
        String password = passwordField.getText().toString();
        client.changePassword(username, password, authenticationParameters, new BaseCallback<Void>() {
            @Override
            public void onSuccess(Void payload) {
                bus.post(new ChangePasswordEvent());
                sendButton.setEnabled(true);
                sendButton.setText(R.string.db_change_password_btn_text);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Throwable error) {
                bus.post(new AuthenticationError(R.string.db_change_password_error_title, R.string.db_change_password_error_message, error));
                sendButton.setEnabled(true);
                sendButton.setText(R.string.db_change_password_btn_text);
                progressBar.setVisibility(View.GONE);
            }
        });
    }

}
