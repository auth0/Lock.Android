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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.auth0.android.lock.events.LockMessageEvent;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.internal.configuration.AuthMode;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.OAuthConnection;
import com.auth0.android.lock.utils.EnterpriseConnectionMatcher;
import com.auth0.android.lock.views.interfaces.IdentityListener;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

import static com.auth0.android.lock.views.ValidatedInputView.DataType;

public class LogInFormView extends FormView implements TextView.OnEditorActionListener, IdentityListener {

    private static final String TAG = LogInFormView.class.getSimpleName();
    private final LockWidgetForm lockWidget;
    private ValidatedUsernameInputView emailInput;
    private ValidatedUsernameInputView usernameInput;
    private ValidatedInputView passwordInput;
    private SocialButton enterpriseBtn;
    private View changePasswordBtn;
    private TextView topMessage;
    private OAuthConnection currentConnection;
    private String currentUsername;
    private EnterpriseConnectionMatcher domainParser;
    private boolean fallbackToDatabase;
    private boolean corporateSSO;
    private boolean changePasswordEnabled;

    public LogInFormView(@NonNull Context context) {
        super(context);
        lockWidget = null;
    }

    public LogInFormView(@NonNull LockWidgetForm lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_login_form_view, this);
        changePasswordBtn = findViewById(R.id.com_auth0_lock_change_password_btn);
        enterpriseBtn = findViewById(R.id.com_auth0_lock_enterprise_button);
        topMessage = findViewById(R.id.com_auth0_lock_text);
        Configuration configuration = lockWidget.getConfiguration();
        domainParser = new EnterpriseConnectionMatcher(configuration.getEnterpriseConnections());
        usernameInput = findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.setDataType(DataType.NON_EMPTY_USERNAME);
        passwordInput = findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setDataType(DataType.PASSWORD);
        passwordInput.setAllowShowPassword(configuration.allowShowPassword());
        passwordInput.setOnEditorActionListener(this);

        emailInput = findViewById(R.id.com_auth0_lock_input_username_email);
        emailInput.configureFrom(configuration.getDatabaseConnection());
        emailInput.setUsernameStyle(configuration.getUsernameStyle());
        emailInput.setIdentityListener(this);

        fallbackToDatabase = configuration.getDatabaseConnection() != null;
        changePasswordEnabled = fallbackToDatabase && configuration.allowForgotPassword();
        changePasswordBtn.setVisibility(changePasswordEnabled ? VISIBLE : GONE);
        changePasswordBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lockWidget.showChangePasswordForm(true);
            }
        });
        boolean socialAvailable = !configuration.getSocialConnections().isEmpty();
        boolean singleEnterprise = configuration.getEnterpriseConnections().size() == 1;
        if (!fallbackToDatabase && !socialAvailable && singleEnterprise) {
            Log.v(TAG, "Only one enterprise connection was found.");
            setupSingleConnectionUI(configuration.getEnterpriseConnections().get(0));
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

    private void setupSingleConnectionUI(final OAuthConnection connection) {
        final int strategyStyle = AuthConfig.styleForStrategy(connection.getStrategy());
        final AuthConfig authConfig = new AuthConfig(connection, strategyStyle);
        enterpriseBtn.setStyle(authConfig, AuthMode.LOG_IN);
        enterpriseBtn.setVisibility(View.VISIBLE);
        enterpriseBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                lockWidget.onOAuthLoginRequest(new OAuthLoginEvent(connection));
            }
        });
        topMessage.setText(R.string.com_auth0_lock_action_single_login_with_corporate);
        topMessage.setVisibility(View.VISIBLE);
        emailInput.setVisibility(GONE);
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
        boolean usingDatabase = currentConnection == null && fallbackToDatabase;
        boolean usingOAuthEnterprise = currentConnection != null && !currentConnection.isActiveFlowEnabled();
        if (usingDatabase || usingOAuthEnterprise) {
            return emailInput.getText();
        } else {
            //Using RO: we get the Username from the "second screen".
            return usernameInput.getText();
        }
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

        if (currentConnection == null || !currentConnection.isActiveFlowEnabled() || passwordInput.getVisibility() == VISIBLE) {
            return getActionEvent();
        }

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
        return null;
    }

    @NonNull
    @Override
    public Object getActionEvent() {
        if (currentConnection != null && currentConnection.isActiveFlowEnabled()) {
            Log.d(TAG, String.format("Form submitted. Logging in with enterprise connection %s using active flow", currentConnection.getName()));
            return new OAuthLoginEvent(currentConnection, getUsername(), getPassword());
        }
        if (currentConnection != null) {
            Log.d(TAG, String.format("Form submitted. Logging in with enterprise connection %s using authorize screen", currentConnection.getName()));
            return new OAuthLoginEvent(currentConnection, getUsername(), null);
        }
        if (fallbackToDatabase) {
            Log.d(TAG, "Logging in with database connection using active flow");
            return new DatabaseLoginEvent(getUsername(), getPassword());
        }
        return new LockMessageEvent(R.string.com_auth0_lock_enterprise_no_connection_message);
    }

    @Override
    public boolean validateForm() {
        boolean valid = true;
        if (emailInput.getVisibility() == VISIBLE) {
            valid = emailInput.validate();
        }
        if (usernameInput.getVisibility() == VISIBLE) {
            valid = usernameInput.validate() && valid;
        }
        if (passwordInput.getVisibility() == VISIBLE) {
            valid = passwordInput.validate() && valid;
        }
        return valid;
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (corporateSSO) {
            Log.d(TAG, "Removing the SSO Login Form, going back to the Username/Password Form.");
            resetDomain();
            showSSOMessage(true);
            return true;
        }
        return false;
    }

    private void showSSOMessage(boolean show) {
        lockWidget.showTopBanner(show);
        if (changePasswordEnabled) {
            changePasswordBtn.setVisibility(show ? GONE : VISIBLE);
        }
    }

    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT && currentConnection != null) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    public void setLastEmail(@Nullable String email) {
        emailInput.setText(email);
        passwordInput.clearInput();
    }

    @Override
    public void onEmailChanged(@NonNull String email) {
        lockWidget.onEmailChanged(email);
    }

    public void clearEmptyFieldsError() {
        if (usernameInput.getText().isEmpty()) {
            usernameInput.clearInput();
        }
        if (emailInput.getText().isEmpty()) {
            emailInput.clearInput();
        }
    }
}
