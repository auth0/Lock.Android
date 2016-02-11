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
import android.widget.Button;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.utils.Strategies;
import com.squareup.otto.Bus;

public class DomainFormView extends FormView {

    private static final String TAG = DomainFormView.class.getSimpleName();
    private ValidatedInputView emailInput;
    private String currentDomain;

    public DomainFormView(Context context) {
        super(context);
    }

    public DomainFormView(Context context, Bus lockBus, Configuration configuration) {
        super(context, lockBus, configuration);
    }

    @Override
    protected void init(Configuration configuration) {
        inflate(getContext(), R.layout.com_auth0_lock_domain_form_view, this);

        final Button actionButton = (Button) findViewById(R.id.com_auth0_lock_action_btn);
        actionButton.setText(R.string.com_auth0_lock_action_login);
        actionButton.setOnClickListener(this);

        emailInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username_email);
        emailInput.setDataType(ValidatedInputView.DataType.EMAIL);
        emailInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                int indexAt = text.indexOf("@") + 1;
                if (indexAt == 0) {
                    return;
                }
                int indexDot = text.indexOf(".", indexAt);
                String domain;
                if (indexDot == -1) {
                    domain = text.substring(indexAt);
                } else {
                    domain = text.substring(indexAt, indexDot);
                }
                if (domain.isEmpty()) {
                    return;
                }

                currentDomain = searchDomain(domain);
                Log.d(TAG, "Domain found: " + currentDomain);
                if (currentDomain != null) {
                    actionButton.setText(String.format(getResources().getString(R.string.com_auth0_lock_action_login_with), currentDomain));
                } else {
                    actionButton.setText(R.string.com_auth0_lock_action_login);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    protected Object getActionEvent() {
        return null;
    }

    @Override
    protected boolean hasValidData() {
        boolean valid = false;
        if (emailInput.getVisibility() == VISIBLE) {
            valid = emailInput.validate();
        }
        return valid;
    }


    @Nullable
    private String searchDomain(String domain) {
        for (Strategies s : Strategies.values()) {
            if (s.getType() == Strategies.Type.ENTERPRISE && s.getName().startsWith(domain.toLowerCase())) {
                return s.getName();
            }
        }
        return null;
    }

}
