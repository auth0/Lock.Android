/*
 * MFACodeFormView.java
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

import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseChangePasswordEvent;
import com.auth0.android.lock.views.interfaces.LockWidget;

public class PasswordExpiredFormView extends FormView implements TextView.OnEditorActionListener {

    private final String usernameOrEmail;
    private final String oldPassword;
    private final LockWidget lockWidget;

    private ValidatedPasswordInputView passwordInput;
    private ValidatedConfirmationInputView confirmPasswordInput;


    public PasswordExpiredFormView(LockWidget lockWidget, String usernameOrEmail, String oldPassword) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        this.usernameOrEmail = usernameOrEmail;
        this.oldPassword = oldPassword;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_pwdexpired_form_view, this);
        passwordInput = (ValidatedPasswordInputView) findViewById(R.id.com_auth0_lock_input_password);

        passwordInput.setPasswordPolicy(lockWidget.getConfiguration().getPasswordPolicy());
        if (lockWidget.getConfiguration().allowShowPassword()) {
            passwordInput.setOnEditorActionListener(this);
            return;
        }
        passwordInput.setAllowShowPassword(false);
        confirmPasswordInput = (ValidatedConfirmationInputView) findViewById(R.id.com_auth0_lock_input_confirm_password);
        confirmPasswordInput.attachToInput(passwordInput);
        confirmPasswordInput.setAllowShowPassword(false);
        confirmPasswordInput.setVisibility(VISIBLE);
        confirmPasswordInput.setOnEditorActionListener(this);
        confirmPasswordInput.setHint(R.string.com_auth0_lock_hint_confirm_password);
        confirmPasswordInput.setErrorDescription(getResources().getString(R.string.com_auth0_lock_input_error_password_do_not_match));
    }

    @Override
    public Object getActionEvent() {
        return new DatabaseChangePasswordEvent(usernameOrEmail, oldPassword, getInputText());
    }

    private String getInputText() {
        return passwordInput.getText();
    }

    @Override
    public boolean validateForm() {
        //New password must:
        //- Be the same as confirmPassword
        //- Be different from the oldPassword
        //- Comply with the password policy set for this connection
        if (confirmPasswordInput.getVisibility() != VISIBLE) {
            return passwordInput.validate();
        }
        return passwordInput.validate() && confirmPasswordInput.validate();
    }

    @Nullable
    @Override
    public Object submitForm() {
        return validateForm() ? getActionEvent() : null;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }
}