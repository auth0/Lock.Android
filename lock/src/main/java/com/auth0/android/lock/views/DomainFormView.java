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
import android.widget.Button;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.EnterpriseROLoginEvent;
import com.auth0.android.lock.events.EnterpriseWebLoginEvent;
import com.auth0.android.lock.utils.Connection;
import com.auth0.android.lock.utils.EmailParser;
import com.auth0.android.lock.utils.Strategy;
import com.squareup.otto.Bus;

public class DomainFormView extends FormView {

    private static final String TAG = DomainFormView.class.getSimpleName();
    private ValidatedInputView emailInput;
    private ValidatedInputView usernameInput;
    private ValidatedInputView passwordInput;
    private Connection currentConnection;
    private String currentUsername;
    private ValidatedInputView.DataType usernameEmailValidation;
    private EmailParser domainParser;
    private Button actionButton;
    private Button goBackBtn;

    public DomainFormView(Context context) {
        super(context);
    }

    public DomainFormView(Context context, Bus lockBus, Configuration configuration) {
        super(context, lockBus, configuration);
    }

    @Override
    protected void init(Configuration configuration) {
        inflate(getContext(), R.layout.com_auth0_lock_domain_form_view, this);
        domainParser = new EmailParser(configuration.getEnterpriseStrategies());
        actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setText(R.string.com_auth0_lock_action_login);
        actionButton.setOnClickListener(this);
        actionButton.setEnabled(false);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setVisibility(View.GONE);
        usernameInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.setVisibility(View.GONE);
        goBackBtn = (Button) findViewById(R.id.com_auth0_lock_back_btn);
        goBackBtn.setText(R.string.com_auth0_lock_action_go_back);
        goBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                emailInput.clearInput();
                resetDomain();
            }
        });
        goBackBtn.setVisibility(GONE);

        emailInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username_email);
        emailInput.setDataType(ValidatedInputView.DataType.EMAIL);
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
                    actionButton.setEnabled(true);
                    actionButton.setText(String.format(getResources().getString(R.string.com_auth0_lock_action_login_with), currentConnection.getName()));
                } else {
                    resetDomain();
                }
            }
        });

        switch (configuration.getUsernameStyle()) {
            case EMAIL:
                usernameEmailValidation = ValidatedInputView.DataType.EMAIL;
                break;
            case USERNAME:
                usernameEmailValidation = ValidatedInputView.DataType.USERNAME;
                break;
            case DEFAULT:
                if (configuration.isUsernameRequired()) {
                    usernameEmailValidation = ValidatedInputView.DataType.USERNAME_OR_EMAIL;
                } else {
                    usernameEmailValidation = ValidatedInputView.DataType.EMAIL;
                }
                break;
        }
        usernameInput.setDataType(usernameEmailValidation);
    }

    private void resetDomain() {
        goBackBtn.setVisibility(GONE);
        emailInput.setVisibility(View.VISIBLE);
        passwordInput.setVisibility(View.GONE);
        passwordInput.clearInput();
        usernameInput.setVisibility(View.GONE);
        usernameInput.clearInput();
        actionButton.setEnabled(false);
        actionButton.setText(R.string.com_auth0_lock_action_login);
    }

    public String getUsernameOrEmail() {
        return usernameInput.getText();
    }

    public String getPassword() {
        return passwordInput.getText();
    }


    @Override
    public void onClick(View v) {
        if (!hasValidData()) {
            return;
        }

        Strategy strategy = domainParser.strategyForConnection(currentConnection);
        if (passwordInput.getVisibility() == VISIBLE || (strategy != null && strategy.isResourceOwnerEnabled())) {
            super.onClick(v);
        } else {
            goBackBtn.setVisibility(VISIBLE);
            passwordInput.setVisibility(View.VISIBLE);
            usernameInput.setVisibility(VISIBLE);
            if (usernameEmailValidation == ValidatedInputView.DataType.USERNAME) {
                usernameInput.setText(currentUsername);
            } else {
                usernameInput.setText(emailInput.getText());
            }
            emailInput.setVisibility(GONE);
        }
    }

    @Override
    protected Object getActionEvent() {
        Strategy strategy = domainParser.strategyForConnection(currentConnection);
        if (strategy != null && strategy.isResourceOwnerEnabled()) {
            return new EnterpriseROLoginEvent(currentConnection.getName(), getUsernameOrEmail(), getPassword());
        } else {
            return new EnterpriseWebLoginEvent(currentConnection.getName());
        }
    }

    @Override
    protected boolean hasValidData() {
        boolean valid = true;
        if (emailInput.getVisibility() == VISIBLE) {
            valid = emailInput.validate();
        }
        if (usernameInput.getVisibility() == VISIBLE) {
            valid = valid && usernameInput.validate();
        }
        if (passwordInput.getVisibility() == VISIBLE) {
            valid = valid && passwordInput.validate();
        }
        return valid;
    }

}
