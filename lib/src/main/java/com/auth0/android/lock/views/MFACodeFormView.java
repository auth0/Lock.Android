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
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.views.interfaces.LockWidget;

public class MFACodeFormView extends FormView implements TextView.OnEditorActionListener {

    private static final String TAG = MFACodeFormView.class.getSimpleName();
    private final String usernameOrEmail;
    private final String password;

    private LockWidget lockWidget;
    private TextView title;
    private TextView description;
    private ValidatedInputView codeInput;


    public MFACodeFormView(LockWidget lockWidget, String usernameOrEmail, String password) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        this.usernameOrEmail = usernameOrEmail;
        this.password = password;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_mfa_input_code_form_view, this);
        title = (TextView) findViewById(R.id.com_auth0_lock_title);
        description = (TextView) findViewById(R.id.com_auth0_lock_text);
        codeInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_code);
        codeInput.setHint(R.string.com_auth0_lock_hint_mfa_code);
        codeInput.setOnEditorActionListener(this);
    }


    @Override
    public Object getActionEvent() {
        DatabaseLoginEvent event = new DatabaseLoginEvent(usernameOrEmail, password);
        event.setVerificationCode(getInputText());
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
        description.setVisibility(isOpen ? GONE : VISIBLE);
    }

}