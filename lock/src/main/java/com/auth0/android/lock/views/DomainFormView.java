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
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.EnterpriseROLoginEvent;
import com.auth0.android.lock.events.EnterpriseWebLoginEvent;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.Strategy;
import com.squareup.otto.Bus;

public class DomainFormView extends FormView {

    private static final String TAG = DomainFormView.class.getSimpleName();
    private ValidatedInputView usernameEmailInput;
    private ValidatedInputView passwordInput;
    private Configuration configuration;
    private Strategy currentStrategy;

    public DomainFormView(Context context) {
        super(context);
    }

    public DomainFormView(Context context, Bus lockBus, Configuration configuration) {
        super(context, lockBus, configuration);
    }

    @Override
    protected void init(Configuration configuration) {
        this.configuration = configuration;
        inflate(getContext(), R.layout.com_auth0_lock_domain_form_view, this);

        final Button actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setText(R.string.com_auth0_lock_action_login);
        actionButton.setOnClickListener(this);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setVisibility(View.GONE);

        usernameEmailInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username_email);
        usernameEmailInput.setDataType(ValidatedInputView.DataType.EMAIL);
        usernameEmailInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                String domain = extractDomain(text);
                String currentUsername = extractUsername(text);
                currentStrategy = searchDomain(domain);
                Log.d(TAG, "Username/Domain found: " + currentUsername + "/" + currentStrategy);
                if (currentStrategy != null) {
                    actionButton.setEnabled(true);
                    actionButton.setText(String.format(getResources().getString(R.string.com_auth0_lock_action_login_with), currentStrategy.getName()));
                } else {
                    passwordInput.setVisibility(View.GONE);
                    actionButton.setEnabled(false);
                    actionButton.setText(R.string.com_auth0_lock_action_login);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private String extractDomain(String email) {
        int indexAt = email.indexOf("@") + 1;
        if (indexAt == 0) {
            return "";
        }
        int indexDot = email.indexOf(".", indexAt);
        String domain;
        if (indexDot == -1) {
            domain = email.substring(indexAt);
        } else {
            domain = email.substring(indexAt, indexDot);
        }
        if (domain.isEmpty()) {
            return "";
        }
        return domain;
    }

    private String extractUsername(String email) {
        int indexAt = email.indexOf("@");
        if (indexAt == -1) {
            return "";
        }
        return email.substring(0, indexAt);
    }

    public String getUsernameOrEmail() {
        return usernameEmailInput.getText();
    }

    public String getPassword() {
        return passwordInput.getText();
    }


    @Override
    public void onClick(View v) {
        if (passwordInput.getVisibility() == VISIBLE || !currentStrategy.isResourceOwnerEnabled()) {
            super.onClick(v);
        } else {
            passwordInput.clearInput();
            passwordInput.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected Object getActionEvent() {
        if (currentStrategy.isResourceOwnerEnabled()) {
            return new EnterpriseROLoginEvent(currentStrategy.getConnections().get(0).getName(), getUsernameOrEmail(), getPassword());
        } else {
            return new EnterpriseWebLoginEvent(currentStrategy.getConnections().get(0).getName());
        }
    }

    @Override
    protected boolean hasValidData() {
        boolean valid = false;
        if (usernameEmailInput.getVisibility() == VISIBLE) {
            valid = usernameEmailInput.validate();
        }
        if (passwordInput.getVisibility() == VISIBLE) {
            valid = passwordInput.validate();
        }
        return valid;
    }

    @Nullable
    private Strategy searchDomain(String domain) {
        if (domain.isEmpty()) {
            return null;
        }
        for (Strategy s : configuration.getEnterpriseStrategies()) {
            if (s.getType() == Strategies.Type.ENTERPRISE && domain.toLowerCase().contains(s.getName())) {
                return s;
            }
        }
        return null;
    }

    private boolean isResourceOwnerEnabled(String name) {
        Strategy strategy = configuration.getApplication().strategyForName(name);
        return strategy != null && strategy.isResourceOwnerEnabled();
    }

}
