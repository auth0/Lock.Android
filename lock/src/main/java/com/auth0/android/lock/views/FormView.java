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
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.events.DbChangePasswordEvent;
import com.auth0.android.lock.events.DbLoginEvent;
import com.auth0.android.lock.events.DbSignUpEvent;
import com.squareup.otto.Bus;

public class FormView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = FormView.class.getSimpleName();
    private ValidatedInputView input1;
    private ValidatedInputView input2;
    private ValidatedInputView input3;
    private ValidatedInputView input4;
    private Button actionButton;
    private Mode currentMode;
    private Bus bus;

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    private enum Mode {LOGIN, SIGN_UP, CHANGE_PASSWORD}

    public FormView(Context context) {
        super(context);
        init();
    }

    public FormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_form_view, this);
        input1 = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_1);
        input1.setDataType(ValidatedInputView.DataType.USERNAME_OR_EMAIL);
        input2 = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_2);
        input2.setDataType(ValidatedInputView.DataType.EMAIL);
        input3 = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_3);
        input3.setDataType(ValidatedInputView.DataType.PASSWORD);
        input4 = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_4);
        input4.setDataType(ValidatedInputView.DataType.PASSWORD);
        actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setOnClickListener(this);
        if (isInEditMode()) {
            return;
        }
    }

    private void setUsernameStyle(UsernameStyle style) {
        switch (style) {
            default:
            case DEFAULT:
                input1.setDataType(ValidatedInputView.DataType.USERNAME_OR_EMAIL);
                break;
            case EMAIL:
                input1.setDataType(ValidatedInputView.DataType.EMAIL);
                break;
            case USERNAME:
                input1.setDataType(ValidatedInputView.DataType.USERNAME);
                break;
        }
    }

    public String getUsernameOrEmail() {
        return input1.getText();
    }

    public String getPassword() {
        return input3.getText();
    }

    public boolean hasValidData() {
        boolean valid = true;
        if (input1.getVisibility() == VISIBLE) {
            valid = valid && input1.validate();
        }
        if (input2.getVisibility() == VISIBLE) {
            valid = valid && input2.validate();
        }
        if (input3.getVisibility() == VISIBLE) {
            valid = valid && input3.validate();
        }
        if (input4.getVisibility() == VISIBLE) {
            valid = valid && input4.validate();
        }
        if (currentMode == Mode.CHANGE_PASSWORD) {
            boolean passwordsMatch = input2.getText().equals(input3.getText());
            if (!passwordsMatch) {
                Toast.makeText(getContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
            }
            valid = valid && passwordsMatch;
        }
        return valid;
    }

    public void showLogin(UsernameStyle style) {
        currentMode = Mode.LOGIN;
        setUsernameStyle(style);
        input2.setDataType(ValidatedInputView.DataType.PASSWORD);
        input3.setVisibility(View.GONE);
        input4.setVisibility(View.GONE);
        actionButton.setText(R.string.com_auth0_lock_action_login);
        clearInput();
    }

    public void showChangePassword(UsernameStyle style) {
        currentMode = Mode.CHANGE_PASSWORD;
        setUsernameStyle(style);
        input2.setDataType(ValidatedInputView.DataType.PASSWORD);
        input3.setDataType(ValidatedInputView.DataType.PASSWORD);
        input3.setVisibility(View.VISIBLE);
        input4.setVisibility(View.GONE);
        actionButton.setText(R.string.com_auth0_lock_action_change_password);
        clearInput();
    }

    public void showSignUp() {
        currentMode = Mode.SIGN_UP;
        input1.setDataType(ValidatedInputView.DataType.EMAIL);
        input2.setDataType(ValidatedInputView.DataType.USERNAME);
        input3.setDataType(ValidatedInputView.DataType.PASSWORD);
        input4.setDataType(ValidatedInputView.DataType.PASSWORD);
        input3.setVisibility(View.VISIBLE);
        input4.setVisibility(View.VISIBLE);
        actionButton.setText(R.string.com_auth0_lock_action_sign_up);
        clearInput();
    }

    private void clearInput() {
        input1.clearInput();
        input2.clearInput();
        input3.clearInput();
        input4.clearInput();
    }

    //TODO: This is a demo method, remove me soon!
    public void moveToNextForm(UsernameStyle style) {
        switch (currentMode) {
            case LOGIN:
                showSignUp();
                break;
            case SIGN_UP:
                showChangePassword(style);
                break;
            case CHANGE_PASSWORD:
                showLogin(style);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (!hasValidData()) {
            return;
        }
        Log.i(TAG, "Action Button clicked");
        switch (currentMode) {
            case SIGN_UP:
                bus.post(new DbSignUpEvent(getUsernameOrEmail(), getPassword()));
                break;
            case CHANGE_PASSWORD:
                bus.post(new DbChangePasswordEvent(getUsernameOrEmail(), getPassword()));
                break;
            default:
            case LOGIN:
                bus.post(new DbLoginEvent(getUsernameOrEmail(), getPassword()));
                break;
        }
    }
}
