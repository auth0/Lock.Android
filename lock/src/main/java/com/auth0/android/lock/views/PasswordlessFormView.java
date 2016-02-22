/*
 * PasswordlessFormView.java
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
import com.auth0.android.lock.enums.PasswordlessMode;
import com.squareup.otto.Bus;

public class PasswordlessFormView extends FormView {

    private static final String TAG = PasswordlessFormView.class.getSimpleName();
    private ValidatedInputView passwordlessInput;
    private Button actionButton;
    private final PasswordlessMode choosenMode;
    private boolean waitingForCode;
    private String emailOrPhone;

    public PasswordlessFormView(Context context, Bus lockBus, PasswordlessMode passwordlessMode) {
        super(context, lockBus, null);
        choosenMode = passwordlessMode;
        selectPasswordlessMode();
    }

    @Override
    protected void init(Configuration configuration) {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_form_view, this);
        passwordlessInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_passwordless);
        passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);

        actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setText(R.string.com_auth0_lock_action_send_code);
        actionButton.setOnClickListener(this);
    }

    private void selectPasswordlessMode() {
        switch (choosenMode) {
            case EMAIL_CODE:
            case EMAIL_LINK:
                passwordlessInput.setDataType(ValidatedInputView.DataType.EMAIL);
                break;
            case SMS_CODE:
            case SMS_LINK:
                break;
        }
    }

    @Override
    protected Object getActionEvent() {
        if (waitingForCode) {
            return new PasswordlessLoginEvent(choosenMode, emailOrPhone, getInputText());
        } else {
            PasswordlessLoginEvent event = new PasswordlessLoginEvent(choosenMode, getInputText());
            if (choosenMode == PasswordlessMode.EMAIL_CODE || choosenMode == PasswordlessMode.SMS_CODE) {
                waitingForCode = true;
                emailOrPhone = getInputText();
                passwordlessInput.setDataType(ValidatedInputView.DataType.CODE);
                passwordlessInput.clearInput();
                actionButton.setText(R.string.com_auth0_lock_action_login);
            }
            return event;
        }
    }

    public String getInputText() {
        return passwordlessInput.getText();
    }

    @Override
    protected boolean hasValidData() {
        return passwordlessInput.validate();
    }
}
