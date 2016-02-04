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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.LockActivity;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DbLoginEvent;
import com.squareup.otto.Bus;

public class LoginFormView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = LoginFormView.class.getSimpleName();
    private ValidatedInputView input1;
    private ValidatedInputView input2;
    private Bus bus;

    public LoginFormView(Context context) {
        super(context);
    }

    public LoginFormView(LockActivity context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        init(configuration);
    }

    private void init(Configuration configuration) {
        inflate(getContext(), R.layout.com_auth0_lock_login_form_view, this);
        input1 = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_1);
        input1.setDataType(configuration.isUsernameRequired() ? ValidatedInputView.DataType.USERNAME_OR_EMAIL : ValidatedInputView.DataType.EMAIL);
        input2 = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_2);
        input2.setDataType(ValidatedInputView.DataType.PASSWORD);
        Button actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setOnClickListener(this);
        actionButton.setText(R.string.com_auth0_lock_action_login);
    }

    public String getUsernameOrEmail() {
        return input1.getText();
    }

    public String getPassword() {
        return input2.getText();
    }

    public boolean hasValidData() {
        boolean valid = true;
        if (input1.getVisibility() == VISIBLE) {
            valid = input1.validate();
        }
        if (input2.getVisibility() == VISIBLE) {
            valid = valid && input2.validate();
        }
        return valid;
    }

    @Override
    public void onClick(View v) {
        if (!hasValidData()) {
            return;
        }
        Log.i(TAG, "Action Button clicked");
        bus.post(new DbLoginEvent(getUsernameOrEmail(), getPassword()));
    }
}
