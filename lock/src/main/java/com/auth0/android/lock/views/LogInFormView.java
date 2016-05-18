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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class LogInFormView extends FormView implements TextView.OnEditorActionListener {

    private static final String TAG = LogInFormView.class.getSimpleName();
    private final LockWidgetForm lockWidget;
    private ValidatedUsernameInputView usernameEmailInput;
    private ValidatedInputView passwordInput;
    private View changePasswordBtn;
    private boolean changePasswordEnabled;

    public LogInFormView(Context context) {
        super(context);
        lockWidget = null;
    }

    public LogInFormView(LockWidgetForm lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_login_form_view, this);
        Configuration configuration = lockWidget.getConfiguration();
        changePasswordEnabled = configuration.isChangePasswordEnabled();
        usernameEmailInput = (ValidatedUsernameInputView) findViewById(R.id.com_auth0_lock_input_username_email);
        usernameEmailInput.chooseDataType(configuration);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setDataType(ValidatedInputView.DataType.PASSWORD);
        passwordInput.setOnEditorActionListener(this);
        changePasswordBtn = findViewById(R.id.com_auth0_lock_change_password_btn);
        changePasswordBtn.setVisibility(configuration.isChangePasswordEnabled() ? View.VISIBLE : View.GONE);
        changePasswordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lockWidget.showChangePasswordForm(true);
            }
        });
    }

    @Override
    public Object getActionEvent() {
        return new DatabaseLoginEvent(getUsernameOrEmail(), getPassword());
    }

    private String getUsernameOrEmail() {
        return usernameEmailInput.getText();
    }

    private String getPassword() {
        return passwordInput.getText();
    }

    @Override
    public boolean validateForm() {
        boolean valid = usernameEmailInput.validate(true);
        valid = passwordInput.validate(true) && valid;
        return valid;
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

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int changePasswordHeight = 0;
        if (changePasswordBtn.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams changePasswordParams = (MarginLayoutParams) changePasswordBtn.getLayoutParams();
            changePasswordHeight = changePasswordBtn.getMeasuredHeight() + changePasswordParams.topMargin + changePasswordParams.bottomMargin;
        }

        int usernameHeight = 0;
        if (usernameEmailInput.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams usernameParams = (MarginLayoutParams) usernameEmailInput.getLayoutParams();
            usernameHeight = usernameEmailInput.getMeasuredHeight() + usernameParams.topMargin + usernameParams.bottomMargin;
        }
        int passwordHeight = 0;
        if (passwordInput.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams passwordParams = (MarginLayoutParams) passwordInput.getLayoutParams();
            passwordHeight = passwordInput.getMeasuredHeight() + passwordParams.topMargin + passwordParams.bottomMargin;
        }

        int sumHeight = changePasswordHeight + usernameHeight + passwordHeight;
        Log.e(TAG, String.format("Parent height %d, FormReal height %d (%d + %d + %d)", parentHeight, sumHeight, changePasswordHeight, usernameHeight, passwordHeight));
        setMeasuredDimension(getMeasuredWidth(), sumHeight);
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        changePasswordBtn.setVisibility(!isOpen && changePasswordEnabled ? VISIBLE : GONE);
    }
}
