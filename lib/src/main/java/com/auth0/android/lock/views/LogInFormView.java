/*
 * LogInFormView.java
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.EnterpriseLoginEvent;
import com.auth0.android.lock.utils.Connection;
import com.auth0.android.lock.utils.EnterpriseConnectionMatcher;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class LogInFormView extends FormView implements TextView.OnEditorActionListener {

    private static final String TAG = LogInFormView.class.getSimpleName();
    private final LockWidgetForm lockWidget;
    private ValidatedUsernameInputView emailInput;
    private ValidatedUsernameInputView usernameInput;
    private ValidatedInputView passwordInput;
    private View changePasswordBtn;
    private TextView topMessage;
    private Connection currentConnection;
    private String currentUsername;
    private EnterpriseConnectionMatcher domainParser;
    private boolean singleConnection;
    private boolean fallbackToDatabase;
    private boolean corporateSSO;
    private boolean changePasswordEnabled;
    private boolean keyboardIsOpen;

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
        changePasswordBtn = findViewById(R.id.com_auth0_lock_change_password_btn);
        topMessage = (TextView) findViewById(R.id.com_auth0_lock_text);
        domainParser = new EnterpriseConnectionMatcher(lockWidget.getConfiguration().getEnterpriseStrategies());
        usernameInput = (ValidatedUsernameInputView) findViewById(R.id.com_auth0_lock_input_username);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setOnEditorActionListener(this);

        emailInput = (ValidatedUsernameInputView) findViewById(R.id.com_auth0_lock_input_username_email);
        emailInput.chooseDataType(lockWidget.getConfiguration());
        usernameInput.setDataType(ValidatedInputView.DataType.USERNAME);

        fallbackToDatabase = lockWidget.getConfiguration().getDefaultDatabaseConnection() != null;
        changePasswordEnabled = fallbackToDatabase && lockWidget.getConfiguration().isChangePasswordEnabled();
        changePasswordBtn.setVisibility(changePasswordEnabled ? VISIBLE : GONE);
        changePasswordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lockWidget.showChangePasswordForm(true);
            }
        });
        if (!fallbackToDatabase && lockWidget.getConfiguration().getEnterpriseStrategies().size() == 1 && lockWidget.getConfiguration().getEnterpriseStrategies().get(0).getConnections().size() == 1) {
            singleConnection = true;
            Log.v(TAG, "Only one enterprise connection was found.");
            setupSingleConnectionUI(lockWidget.getConfiguration().getEnterpriseStrategies().get(0).getConnections().get(0));
        } else {
            Log.v(TAG, "Multiple enterprise/database connections found.");
            setupMultipleConnectionUI();
        }
    }

    private void setupMultipleConnectionUI() {
        usernameInput.setVisibility(View.GONE);
        passwordInput.setVisibility(fallbackToDatabase ? VISIBLE : GONE);
        emailInput.setOnEditorActionListener(this);
        emailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.isEmpty()) {
                    return;
                }

                currentConnection = domainParser.parse(text);
                currentUsername = domainParser.extractUsername(text);
                if (currentConnection != null) {
                    Log.v(TAG, String.format("Matched results are connection %s with username %s", currentConnection, currentUsername));
                    passwordInput.setVisibility(GONE);
                    showSSOMessage(true);
                } else if (fallbackToDatabase) {
                    passwordInput.setVisibility(VISIBLE);
                    showSSOMessage(false);
                } else {
                    resetDomain();
                }
            }
        });
    }

    private void setupSingleConnectionUI(Connection connection) {
        usernameInput.setVisibility(VISIBLE);
        passwordInput.setVisibility(connection.isActiveFlowEnabled() ? View.VISIBLE : GONE);
        String loginWithCorporate = String.format(getResources().getString(R.string.com_auth0_lock_action_login_with_corporate), domainParser.domainForConnection(connection));
        topMessage.setText(loginWithCorporate);
        showSSOMessage(true);
        emailInput.setVisibility(GONE);
        topMessage.setVisibility(View.VISIBLE);
        currentConnection = connection;
    }

    private void resetDomain() {
        emailInput.setVisibility(View.VISIBLE);
        passwordInput.setVisibility(View.GONE);
        passwordInput.clearInput();
        usernameInput.setVisibility(View.GONE);
        usernameInput.clearInput();
        topMessage.setText(null);
        topMessage.setVisibility(View.GONE);
        corporateSSO = false;
        showSSOMessage(false);
    }

    private String getUsername() {
        return currentConnection == null && fallbackToDatabase ? emailInput.getText() : usernameInput.getText();
    }

    private String getPassword() {
        return passwordInput.getText();
    }

    @Nullable
    @Override
    public Object submitForm() {
        if (!validateForm()) {
            Log.w(TAG, "Form has some validation issues and won't be submitted.");
            return null;
        }

        if (currentConnection != null && currentConnection.isActiveFlowEnabled() && (passwordInput.getVisibility() == VISIBLE || singleConnection)) {
            Log.d(TAG, String.format("Form submitted. Logging in with enterprise connection %s using active flow", currentConnection));
            return getActionEvent();
        } else if (currentConnection == null) {
            Log.d(TAG, "Form submitted. Logging in with database connection using active flow");
            return getActionEvent();
        } else {
            Log.d(TAG, "Now showing SSO Login Form for connection " + currentConnection);
            String loginWithCorporate = String.format(getResources().getString(R.string.com_auth0_lock_action_login_with_corporate), domainParser.domainForConnection(currentConnection));
            topMessage.setText(loginWithCorporate);
            topMessage.setVisibility(View.VISIBLE);
            emailInput.setVisibility(GONE);
            passwordInput.setVisibility(View.VISIBLE);
            usernameInput.setVisibility(VISIBLE);
            if (currentUsername != null && !currentUsername.isEmpty()) {
                usernameInput.setText(currentUsername);
            }
            changePasswordBtn.setVisibility(GONE);
            corporateSSO = true;
            usernameInput.clearFocus();
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getWindowToken(), InputMethodManager.RESULT_UNCHANGED_SHOWN);
        }
        return null;
    }

    @Override
    public Object getActionEvent() {
        if (currentConnection != null && currentConnection.isActiveFlowEnabled()) {
            return new EnterpriseLoginEvent(currentConnection.getName(), getUsername(), getPassword());
        } else if (currentConnection != null) {
            return new EnterpriseLoginEvent(currentConnection.getName());
        } else {
            return fallbackToDatabase ? new DatabaseLoginEvent(getUsername(), getPassword()) : new EnterpriseLoginEvent(null);
        }
    }

    @Override
    public boolean validateForm() {
        boolean valid = true;
        if (emailInput.getVisibility() == VISIBLE) {
            valid = emailInput.validate(true);
        }
        if (usernameInput.getVisibility() == VISIBLE) {
            valid = usernameInput.validate(true) && valid;
        }
        if (passwordInput.getVisibility() == VISIBLE) {
            valid = passwordInput.validate(true) && valid;
        }
        return valid;
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (!singleConnection && corporateSSO) {
            Log.d(TAG, "Removing the SSO Login Form, going back to the Username/Password Form.");
            resetDomain();
            showSSOMessage(true);
            return true;
        }
        return false;
    }

    private void showSSOMessage(boolean show) {
        lockWidget.showTopBanner(show);
        if (changePasswordEnabled && !keyboardIsOpen) {
            changePasswordBtn.setVisibility(show ? GONE : VISIBLE);
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT && currentConnection != null) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int topMessageHeight = ViewUtils.measureViewHeight(topMessage);
        int changePasswordHeight = ViewUtils.measureViewHeight(changePasswordBtn);
        int usernameHeight = ViewUtils.measureViewHeight(usernameInput);
        int emailHeight = ViewUtils.measureViewHeight(emailInput);
        int passwordHeight = ViewUtils.measureViewHeight(passwordInput);
        int sumHeight = topMessageHeight + changePasswordHeight + usernameHeight + emailHeight + passwordHeight;

        Log.v(TAG, String.format("Parent height %d, FormReal height %d (%d + %d + %d + %d + %d)", parentHeight, sumHeight, topMessageHeight, changePasswordHeight, usernameHeight, emailHeight, passwordHeight));
        setMeasuredDimension(getMeasuredWidth(), sumHeight);
    }

    /**
     * Getter for the current state of enterprise matched domain.
     *
     * @return whether there is currently a domain match or not.
     */
    public boolean isEnterpriseDomainMatch() {
        return currentConnection != null && !singleConnection;
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        keyboardIsOpen = isOpen;
        changePasswordBtn.setVisibility(!isOpen && !isEnterpriseDomainMatch() && changePasswordEnabled ? VISIBLE : GONE);
        topMessage.setVisibility(topMessage.getText().length() > 0 ? isOpen ? GONE : VISIBLE : GONE);
    }
}
