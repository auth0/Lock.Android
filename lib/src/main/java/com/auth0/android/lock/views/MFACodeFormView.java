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

import android.annotation.SuppressLint;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.views.interfaces.LockWidget;

@SuppressLint("ViewConstructor")
public class MFACodeFormView extends FormView implements TextView.OnEditorActionListener {

    private final String usernameOrEmail;
    private final String password;
    private final String mfaToken;

    private final LockWidget lockWidget;
    private ValidatedInputView codeInput;


    public MFACodeFormView(@NonNull LockWidget lockWidget, @Nullable String usernameOrEmail, @Nullable String password, @Nullable String mfaToken) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        this.mfaToken = mfaToken;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_mfa_input_code_form_view, this);
        codeInput = findViewById(R.id.com_auth0_lock_input_code);
        codeInput.setHint(R.string.com_auth0_lock_hint_mfa_code);
        codeInput.setOnEditorActionListener(this);
    }

    @NonNull
    @Override
    public Object getActionEvent() {
        DatabaseLoginEvent event = new DatabaseLoginEvent(usernameOrEmail, password);
        event.setVerificationCode(getInputText());
        event.setMFAToken(mfaToken);
        return event;
    }

    private String getInputText() {
        return codeInput.getText().replace(" ", "");
    }

    @Override
    public boolean validateForm() {
        return codeInput.validate();
    }

    @Nullable
    @Override
    public Object submitForm() {
        return validateForm() ? getActionEvent() : null;
    }

    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }
}