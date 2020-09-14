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

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.internal.configuration.PasswordlessMode;
import com.auth0.android.lock.views.interfaces.LockWidgetPasswordless;

import static com.auth0.android.lock.internal.configuration.PasswordlessMode.DISABLED;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_LINK;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_LINK;

@SuppressLint("ViewConstructor")
public class PasswordlessInputCodeFormView extends FormView implements View.OnClickListener, TextView.OnEditorActionListener {

    private static final String TAG = PasswordlessInputCodeFormView.class.getSimpleName();
    private static final long RESEND_TIMEOUT = 20 * 1000;

    private final LockWidgetPasswordless lockWidget;
    private final OnCodeResendListener listener;
    private ValidatedInputView passwordlessInput;
    @PasswordlessMode
    private final int passwordlessMode;
    private TextView topMessage;
    private TextView resendButton;

    /**
     * Creates a new FormView to request input of the received code.
     *
     * @param lockWidget the base passwordless widget
     * @param listener   a listener to notify when the user clicks "Resend code" button.
     * @param identity   the email or number used to send the passwordless code.
     */
    @SuppressLint("LambdaLast")
    public PasswordlessInputCodeFormView(@NonNull LockWidgetPasswordless lockWidget, @NonNull OnCodeResendListener listener, @NonNull String identity) {
        super(lockWidget.getContext());
        passwordlessMode = lockWidget.getConfiguration().getPasswordlessMode();
        this.lockWidget = lockWidget;
        this.listener = listener;
        Log.v(TAG, String.format("New instance with mode %s for Identity %s", passwordlessMode, identity));
        init(identity);
    }

    private void init(@NonNull String identity) {
        inflate(getContext(), R.layout.com_auth0_lock_passwordless_input_code_form_view, this);
        topMessage = findViewById(R.id.com_auth0_lock_text);
        resendButton = findViewById(R.id.com_auth0_lock_resend);
        resendButton.setOnClickListener(this);
        passwordlessInput = findViewById(R.id.com_auth0_lock_input_passwordless);
        passwordlessInput.setOnEditorActionListener(this);

        selectPasswordlessMode(identity);
        removeCallbacks(resendTimeoutShower);
        postDelayed(resendTimeoutShower, RESEND_TIMEOUT);
    }


    private void selectPasswordlessMode(@NonNull String identity) {
        int sentMessage = 0;
        switch (passwordlessMode) {
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
            case DISABLED:
                break;
        }
        topMessage.setText(String.format(getContext().getString(sentMessage), identity));
        passwordlessInput.setDataType(ValidatedInputView.DataType.MFA_CODE);
        passwordlessInput.setVisibility(VISIBLE);
        passwordlessInput.clearInput();
        resendButton.setVisibility(GONE);
    }


    @NonNull
    @Override
    public Object getActionEvent() {
        return PasswordlessLoginEvent.submitCode(passwordlessMode, getInputText());
    }

    private String getInputText() {
        return passwordlessInput.getText().replace(" ", "");
    }

    @Override
    public boolean validateForm() {
        return passwordlessInput.validate();
    }

    @Nullable
    @Override
    public Object submitForm() {
        return validateForm() ? getActionEvent() : null;
    }

    final Runnable resendTimeoutShower = new Runnable() {
        @Override
        public void run() {
            resendButton.setVisibility(View.VISIBLE);
        }
    };

    @Override
    public void onClick(@NonNull View v) {
        int id = v.getId();
        if (id == R.id.com_auth0_lock_resend) {
            listener.onCodeNeedToResend();
        }
    }

    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    public interface OnCodeResendListener {
        /**
         * Called when the form needs to remove the "Waiting for the code" view and show
         * the email/phone input again.
         */
        void onCodeNeedToResend();
    }
}