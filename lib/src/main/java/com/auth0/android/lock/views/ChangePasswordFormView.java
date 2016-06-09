/*
 * ChangePasswordFormView.java
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
import android.support.annotation.IdRes;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseChangePasswordEvent;
import com.auth0.android.lock.views.interfaces.InputValidationCallback;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class ChangePasswordFormView extends FormView implements TextView.OnEditorActionListener, InputValidationCallback {

    private static final String TAG = ChangePasswordFormView.class.getSimpleName();
    private final LockWidgetForm lockWidget;
    private ValidatedUsernameInputView emailInput;
    private View title;
    private View text;

    public ChangePasswordFormView(Context context) {
        super(context);
        lockWidget = null;
    }

    public ChangePasswordFormView(LockWidgetForm lockWidget, String email) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init(email);
    }

    private void init(String email) {
        inflate(getContext(), R.layout.com_auth0_lock_changepwd_form_view, this);
        title = findViewById(R.id.com_auth0_lock_title);
        text = findViewById(R.id.com_auth0_lock_text);
        emailInput = (ValidatedUsernameInputView) findViewById(R.id.com_auth0_lock_input_email);
        emailInput.setText(email);
        emailInput.setInputValidationCallback(this);
        emailInput.setOnEditorActionListener(this);
    }

    @Override
    public Object getActionEvent() {
        return new DatabaseChangePasswordEvent(getUsernameOrEmail());
    }

    private String getUsernameOrEmail() {
        return emailInput.getText();
    }

    @Override
    public boolean validateForm() {
        return emailInput.validate();
    }

    @Override
    public Object submitForm() {
        if (validateForm()) {
            Log.d(TAG, "Form submitted");
            return getActionEvent();
        }
        Log.w(TAG, "Form has some validation issues and won't be submitted.");
        return null;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        title.setVisibility(isOpen ? GONE : VISIBLE);
        text.setVisibility(isOpen ? GONE : VISIBLE);
    }

    @Override
    public void onValidOrEmptyInput(@IdRes int id, String currentValue) {
        lockWidget.onEmailChanged(currentValue);
    }
}
