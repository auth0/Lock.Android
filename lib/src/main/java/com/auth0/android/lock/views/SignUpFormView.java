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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class SignUpFormView extends FormView implements TextView.OnEditorActionListener {

    private static final String TAG = SignUpFormView.class.getSimpleName();
    private final LockWidgetForm lockWidget;
    private ValidatedInputView usernameInput;
    private ValidatedInputView emailInput;
    private ValidatedInputView passwordInput;

    public SignUpFormView(Context context) {
        super(context);
        this.lockWidget = null;
    }

    public SignUpFormView(LockWidgetForm lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init();
    }

    private void init() {
        Configuration configuration = lockWidget.getConfiguration();
        inflate(getContext(), R.layout.com_auth0_lock_signup_form_view, this);

        usernameInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.setDataType(ValidatedInputView.DataType.USERNAME);
        emailInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_email);
        emailInput.setDataType(ValidatedInputView.DataType.EMAIL);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setDataType(ValidatedInputView.DataType.PASSWORD);
        passwordInput.setOnEditorActionListener(this);

        if (configuration.getExtraSignUpFields().size() == 1) {
            Log.d(TAG, "There is just 1 extra field. Showing special SignUp layout.");
            boolean askEmail = configuration.isUsernameRequired() || configuration.getUsernameStyle() == UsernameStyle.EMAIL || configuration.getUsernameStyle() == UsernameStyle.DEFAULT;
            if (askEmail) {
                usernameInput.setVisibility(View.GONE);
                emailInput.setVisibility(View.VISIBLE);
                emailInput.setOnEditorActionListener(this);
            } else {
                emailInput.setVisibility(View.GONE);
                usernameInput.setVisibility(View.VISIBLE);
                usernameInput.setOnEditorActionListener(this);
            }
            passwordInput.setVisibility(View.GONE);
            return;
        }

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
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int usernameHeight = ViewUtils.measureViewHeight(usernameInput);
        int emailHeight = ViewUtils.measureViewHeight(emailInput);
        int passwordHeight = ViewUtils.measureViewHeight(passwordInput);
        int sumHeight = usernameHeight + emailHeight + passwordHeight;

        Log.v(TAG, String.format("Parent height %d, Children height %d (%d + %d + %d)", parentHeight, sumHeight, usernameHeight, emailHeight, passwordHeight));
        setMeasuredDimension(getMeasuredWidth(), sumHeight);
    }

    @Override
    public Object getActionEvent() {
        Log.d(TAG, String.format("Triggered sign up with email %s and username %s", getEmail(), getUsername()));
        return new DatabaseSignUpEvent(getEmail(), getUsername(), getPassword());
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
    public boolean validateForm() {
        boolean valid = true;
        if (usernameInput.getVisibility() == VISIBLE) {
            valid = usernameInput.validate(true);
        }
        if (emailInput.getVisibility() == VISIBLE) {
            valid = emailInput.validate(true) && valid;
        }
        if (passwordInput.getVisibility() == VISIBLE) {
            valid = passwordInput.validate(true) && valid;
        }
        return valid;
    }

    @Nullable
    @Override
    public Object submitForm() {
        if (validateForm()) {
            DatabaseSignUpEvent event = (DatabaseSignUpEvent) getActionEvent();
            if (!lockWidget.getConfiguration().hasExtraFields()) {
                return event;
            }
            lockWidget.showCustomFieldsForm(event);
        }
        return null;
    }

    @Override
    public void onKeyboardStateChanged(boolean isOpen) {
        //Do nothing
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }
}
