/*
 * SignUpFormView.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
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

package com.auth0.android.lock.views;

import android.content.Context;
import android.view.View;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.squareup.otto.Bus;

public class SignUpFormView extends FormView {

    private static final String TAG = SignUpFormView.class.getSimpleName();
    private ValidatedInputView usernameInput;
    private ValidatedInputView emailInput;
    private ValidatedInputView passwordInput;
    private boolean loginAfterSignUp;
    private ActionButton actionButton;

    public SignUpFormView(Context context) {
        super(context);
    }

    public SignUpFormView(Context context, Bus lockBus, Configuration configuration) {
        super(context, lockBus);
        init(configuration);
    }

    private void init(Configuration configuration) {
        inflate(getContext(), R.layout.com_auth0_lock_signup_form_view, this);
        loginAfterSignUp = configuration.loginAfterSignUp();

        usernameInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.setDataType(ValidatedInputView.DataType.USERNAME);
        emailInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_email);
        emailInput.setDataType(ValidatedInputView.DataType.EMAIL);

        if (configuration.isUsernameRequired()) {
            emailInput.setVisibility(View.VISIBLE);
            usernameInput.setVisibility(View.VISIBLE);
        } else if (configuration.getUsernameStyle() == UsernameStyle.USERNAME) {
            emailInput.setVisibility(View.GONE);
            usernameInput.setVisibility(View.VISIBLE);
        } else if (configuration.getUsernameStyle() == UsernameStyle.EMAIL || configuration.getUsernameStyle() == UsernameStyle.DEFAULT) {
            emailInput.setVisibility(View.VISIBLE);
            usernameInput.setVisibility(View.GONE);
        }

        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setDataType(ValidatedInputView.DataType.PASSWORD);
        actionButton = (ActionButton) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setOnClickListener(this);
    }

    @Override
    protected Object getActionEvent() {
        return new DatabaseSignUpEvent(getEmail(), getUsername(), getPassword(), loginAfterSignUp);
    }

    private String getUsername() {
        if (usernameInput.getVisibility() == VISIBLE) {
            return usernameInput.getText();
        } else {
            return emailInput.getText();
        }
    }

    private String getEmail() {
        return emailInput.getText();
    }

    private String getPassword() {
        return passwordInput.getText();
    }

    @Override
    protected boolean hasValidData() {
        boolean valid = passwordInput.validate(true);
        if (usernameInput.getVisibility() == VISIBLE) {
            valid = usernameInput.validate(true) && valid;
        }
        if (emailInput.getVisibility() == VISIBLE) {
            valid = emailInput.validate(true) && valid;
        }
        return valid;
    }

    @Override
    public void showProgress(boolean show) {
        actionButton.showProgress(show);
    }
}
