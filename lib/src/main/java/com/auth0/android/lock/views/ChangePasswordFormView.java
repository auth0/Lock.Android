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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseChangePasswordEvent;
import com.auth0.android.lock.views.interfaces.IdentityListener;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class ChangePasswordFormView extends FormView implements TextView.OnEditorActionListener, IdentityListener {

    private static final String TAG = ChangePasswordFormView.class.getSimpleName();
    private final LockWidgetForm lockWidget;
    private ValidatedInputView emailInput;

    public ChangePasswordFormView(@NonNull Context context) {
        super(context);
        lockWidget = null;
    }

    public ChangePasswordFormView(@NonNull LockWidgetForm lockWidget, @Nullable String email) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init(email);
    }

    private void init(String email) {
        inflate(getContext(), R.layout.com_auth0_lock_changepwd_form_view, this);
        emailInput = findViewById(R.id.com_auth0_lock_input_email);
        emailInput.setText(email);
        emailInput.setIdentityListener(this);
        emailInput.setOnEditorActionListener(this);
        requestFocus();
    }

    @NonNull
    @Override
    public Object getActionEvent() {
        return new DatabaseChangePasswordEvent(getUsernameOrEmail());
    }

    @NonNull
    private String getUsernameOrEmail() {
        return emailInput.getText();
    }

    @Override
    public boolean validateForm() {
        return emailInput.validate();
    }

    @Nullable
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
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    @Override
    public void onEmailChanged(@NonNull String email) {
        lockWidget.onEmailChanged(email);
    }

}
