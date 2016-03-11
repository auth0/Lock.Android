/*
 * DomainFormView.java
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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.EnterpriseLoginEvent;
import com.auth0.android.lock.utils.Connection;
import com.auth0.android.lock.utils.EnterpriseConnectionMatcher;
import com.auth0.android.lock.views.interfaces.LockWidgetDatabase;

public class DomainFormView extends FormView {

    private static final String TAG = DomainFormView.class.getSimpleName();
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

    public DomainFormView(Context context) {
        super(context);
    }

    public DomainFormView(LockWidgetDatabase lockWidget) {
        super(lockWidget.getContext());
        init(lockWidget);
    }

    private void init(final LockWidgetDatabase lockWidget) {
        inflate(getContext(), R.layout.com_auth0_lock_domain_form_view, this);
        changePasswordBtn = findViewById(R.id.com_auth0_lock_change_password_btn);
        topMessage = (TextView) findViewById(R.id.com_auth0_lock_sso_message);
        domainParser = new EnterpriseConnectionMatcher(lockWidget.getConfiguration().getEnterpriseStrategies());
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setVisibility(View.GONE);
        usernameInput = (ValidatedUsernameInputView) findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.setVisibility(View.GONE);

        emailInput = (ValidatedUsernameInputView) findViewById(R.id.com_auth0_lock_input_username_email);
        emailInput.chooseDataType(lockWidget.getConfiguration());
        usernameInput.setDataType(ValidatedInputView.DataType.USERNAME);

        fallbackToDatabase = lockWidget.getConfiguration().getDefaultDatabaseConnection() != null;
        changePasswordEnabled = fallbackToDatabase && lockWidget.getConfiguration().isChangePasswordEnabled();
        changePasswordBtn.setVisibility(changePasswordEnabled ? VISIBLE : GONE);
        changePasswordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lockWidget.showChangePasswordForm();
            }
        });
        if (!fallbackToDatabase && lockWidget.getConfiguration().getEnterpriseStrategies().size() == 1 && lockWidget.getConfiguration().getEnterpriseStrategies().get(0).getConnections().size() == 1) {
            singleConnection = true;
            setupSingleConnectionUI(lockWidget.getConfiguration().getEnterpriseStrategies().get(0).getConnections().get(0));
        } else {
            setupMultipleConnectionUI();
        }
    }

    private void setupMultipleConnectionUI() {
        if (fallbackToDatabase) {
            passwordInput.setVisibility(VISIBLE);
//            actionButton.setEnabled(true);
        }
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
                Log.d(TAG, "Username/Connection found: " + currentUsername + "/" + currentConnection);
                if (currentConnection != null) {
                    passwordInput.setVisibility(GONE);
                    topMessage.setText(R.string.com_auth0_lock_single_sign_on_enabled);
                    topMessage.setVisibility(View.VISIBLE);
                    changePasswordBtn.setVisibility(GONE);
//                    actionButton.setEnabled(true);
                } else if (fallbackToDatabase) {
                    passwordInput.setVisibility(VISIBLE);
                    topMessage.setVisibility(View.GONE);
//                    actionButton.setEnabled(true);
                    if (changePasswordEnabled) {
                        changePasswordBtn.setVisibility(VISIBLE);
                    }
                } else {
                    resetDomain();
                }
            }
        });
    }

    private void setupSingleConnectionUI(Connection connection) {
        currentConnection = connection;
//        actionButton.setEnabled(true);
        String loginWithCorporate = String.format(getResources().getString(R.string.com_auth0_lock_action_login_with_corporate), domainParser.domainForConnection(connection));
        topMessage.setText(loginWithCorporate);
        if (connection.isActiveFlowEnabled()) {
            passwordInput.setVisibility(View.VISIBLE);
        }
        usernameInput.setVisibility(VISIBLE);
        emailInput.setVisibility(GONE);
        topMessage.setVisibility(View.VISIBLE);
    }

    private void resetDomain() {
        emailInput.setVisibility(View.VISIBLE);
        passwordInput.setVisibility(View.GONE);
        passwordInput.clearInput();
        usernameInput.setVisibility(View.GONE);
        usernameInput.clearInput();
//        actionButton.setEnabled(false);
        topMessage.setVisibility(GONE);
        corporateSSO = false;
    }

    private String getUsername() {
        return currentConnection == null && fallbackToDatabase ? emailInput.getText() : usernameInput.getText();
    }

    private String getPassword() {
        return passwordInput.getText();
    }


    @Override
    public void onClick(View v) {
        if (!hasValidData()) {
            return;
        }

        if (currentConnection != null && currentConnection.isActiveFlowEnabled() && (passwordInput.getVisibility() == VISIBLE || singleConnection)) {
            super.onClick(v);
        } else if (currentConnection == null && fallbackToDatabase) {
            super.onClick(v);
        } else {
            String loginWithCorporate = String.format(getResources().getString(R.string.com_auth0_lock_action_login_with_corporate), domainParser.domainForConnection(currentConnection));
            topMessage.setText(loginWithCorporate);
            passwordInput.setVisibility(View.VISIBLE);
            usernameInput.setVisibility(VISIBLE);
            if (currentUsername != null && !currentUsername.isEmpty()) {
                usernameInput.setText(currentUsername);
            }
            emailInput.setVisibility(GONE);
            changePasswordBtn.setVisibility(GONE);
            corporateSSO = true;
        }
    }

    @Override
    public Object getActionEvent() {
        if (currentConnection != null && currentConnection.isActiveFlowEnabled()) {
            return new EnterpriseLoginEvent(currentConnection.getName(), getUsername(), getPassword());
        } else if (currentConnection != null) {
            return new EnterpriseLoginEvent(currentConnection.getName());
        } else {
            return new DatabaseLoginEvent(getUsername(), getPassword());
        }
    }

    @Override
    public boolean hasValidData() {
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

    public boolean onBackPressed() {
        if (!singleConnection && corporateSSO) {
            resetDomain();
            topMessage.setText(R.string.com_auth0_lock_single_sign_on_enabled);
            topMessage.setVisibility(VISIBLE);
//            actionButton.setEnabled(true);
            if (changePasswordEnabled && currentConnection == null) {
                changePasswordBtn.setVisibility(VISIBLE);
            }
            return true;
        }
        return false;
    }

}
