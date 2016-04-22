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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;

public class PasswordlessInputCodeFormView extends FormView implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = PasswordlessInputCodeFormView.class.getSimpleName();
    private static final long RESEND_TIMEOUT = 20 * 1000;

    private final LockWidgetPasswordless lockWidget;
    private final OnCodeResendListener listener;
    private ValidatedInputView passwordlessInput;
    private PasswordlessMode choosenMode;
    private boolean resendShown;
    private boolean keyboardIsOpen;
    private TextView topMessage;
    private TextView resendButton;

    /**
     * @param lockWidget
     * @param listener   a listener to notify when the user clicks "Resend code" button.
     * @param identity   the email or number used to send the passwordless code.
     */
    public PasswordlessInputCodeFormView(LockWidgetPasswordless lockWidget, @NonNull OnCodeResendListener listener, String identity) {
        super(lockWidget.getContext());
        choosenMode = lockWidget.getConfiguration().getPasswordlessMode();
        this.lockWidget = lockWidget;
        this.listener = listener;
        Log.v(TAG, String.format("New instance with mode %s for %s", choosenMode, identity));
        init(identity);
    }

    private void init(String identity) {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_input_code_form_view, this);
        topMessage = (TextView) findViewById(R.id.com_auth0_lock_text);
        resendButton = (TextView) findViewById(R.id.com_auth0_lock_resend);
        resendButton.setOnClickListener(this);
        passwordlessInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_passwordless);
        passwordlessInput.setOnEditorActionListener(this);

        selectPasswordlessMode(identity);
        removeCallbacks(resendTimeoutShower);
        postDelayed(resendTimeoutShower, RESEND_TIMEOUT);
    }


    private void selectPasswordlessMode(String identity) {
        int sentMessage = 0;
        switch (choosenMode) {
            case EMAIL_CODE:
                sentMessage = R.string.com_auth0_lock_title_passwordless_code_email_sent;
                break;
            case EMAIL_LINK:
                sentMessage = R.string.com_auth0_lock_title_passwordless_link_sent;
                break;
            case SMS_CODE:
                sentMessage = R.string.com_auth0_lock_title_passwordless_code_sms_sent;
                break;
            case SMS_LINK:
                sentMessage = R.string.com_auth0_lock_title_passwordless_link_sent;
                break;
        }
        topMessage.setText(String.format(getContext().getString(sentMessage), identity));
        passwordlessInput.setDataType(ValidatedInputView.DataType.NUMBER);
        passwordlessInput.setVisibility(VISIBLE);
        passwordlessInput.clearInput();
        resendButton.setVisibility(GONE);
    }


    @Override
    public Object getActionEvent() {
        return PasswordlessLoginEvent.submitCode(choosenMode, getInputText());
    }

    private String getInputText() {
        return passwordlessInput.getText().replace(" ", "");
    }

    @Override
    public boolean validateForm() {
        return passwordlessInput.validate(true);
    }

    @Nullable
    @Override
    public Object submitForm() {
        return validateForm() ? getActionEvent() : null;
    }

    final Runnable resendTimeoutShower = new Runnable() {
        @Override
        public void run() {
            Log.v(TAG, "Timeout reached. Showing Resend Code button");
            resendShown = true;
            if (!keyboardIsOpen) {
                resendButton.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.com_auth0_lock_resend) {
            listener.onCodeNeedToResend();
        }
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
        keyboardIsOpen = isOpen;
        if (resendShown) {
            resendButton.setVisibility(isOpen ? GONE : VISIBLE);
        }
        topMessage.setVisibility(isOpen ? GONE : VISIBLE);
    }

    public interface OnCodeResendListener {
        /**
         * Called when the form needs to remove the "Waiting for the code" view and show
         * the email/phone input again.
         */
        void onCodeNeedToResend();
    }
}