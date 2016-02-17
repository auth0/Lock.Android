/*
 * FormView.java
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
import android.widget.Button;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.squareup.otto.Bus;

public class LoginFormView extends FormView {

    private static final String TAG = LoginFormView.class.getSimpleName();
    private ValidatedUsernameInputView usernameEmailInput;
    private ValidatedInputView passwordInput;

    public LoginFormView(Context context) {
        super(context);
    }

    public LoginFormView(Context context, Bus lockBus, Configuration configuration) {
        super(context, lockBus, configuration);
    }

    @Override
    protected void init(Configuration configuration) {
        inflate(getContext(), R.layout.com_auth0_lock_login_form_view, this);
        usernameEmailInput = (ValidatedUsernameInputView) findViewById(R.id.com_auth0_lock_input_username_email);
        usernameEmailInput.chooseDataType(configuration);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setDataType(ValidatedInputView.DataType.PASSWORD);

        Button actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setText(R.string.com_auth0_lock_action_login);
        actionButton.setOnClickListener(this);
    }

    @Override
    protected Object getActionEvent() {
        return new DatabaseLoginEvent(getUsernameOrEmail(), getPassword());
    }

    public String getUsernameOrEmail() {
        return usernameEmailInput.getText();
    }

    public String getPassword() {
        return passwordInput.getText();
    }

    @Override
    protected boolean hasValidData() {
        return usernameEmailInput.validate() && passwordInput.validate();
    }
}
