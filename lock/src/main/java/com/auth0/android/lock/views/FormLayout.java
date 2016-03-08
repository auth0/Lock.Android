/*
 * DbLayout.java
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
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.squareup.otto.Bus;

public class FormLayout extends RelativeLayout implements View.OnClickListener {
    private Bus bus;
    private Configuration configuration;
    private ChangePasswordListener callback;

    private Button changePasswordBtn;
    private FrameLayout formContainer;
    private boolean showDatabase;
    private boolean showEnterprise;

    private LoginFormView loginForm;
    private SignUpFormView signUpForm;
    private ChangePasswordFormView changePwdForm;
    private DomainFormView domainForm;
    private FormMode currentFormMode;

    public enum FormMode {LOG_IN, SIGN_UP}

    public FormLayout(Context context) {
        super(context);
    }

    public FormLayout(Context context, Bus lockBus, Configuration configuration, ChangePasswordListener callback) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        this.currentFormMode = FormMode.LOG_IN;
        this.callback = callback;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_database_layout, this);
        formContainer = (FrameLayout) findViewById(R.id.com_auth0_lock_form_layout);
        changePasswordBtn = (Button) findViewById(R.id.com_auth0_lock_change_password_btn);
        changePasswordBtn.setOnClickListener(this);

        showDatabase = configuration.getDefaultDatabaseConnection() != null;
        showEnterprise = !configuration.getEnterpriseStrategies().isEmpty();

        moveToFirstForm();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.com_auth0_lock_change_password_btn) {
            showChangePasswordForm();
        }
    }

    /**
     * Change the current form mode
     *
     * @param mode the new FormMode to change to
     */
    public void changeFormMode(FormMode mode) {
        switch (mode) {
            case LOG_IN:
                moveToFirstForm();
                break;
            case SIGN_UP:
                showSignUpForm();
                break;
        }
        currentFormMode = mode;
    }

    private void showSignUpForm() {
        removePreviousForm();

        if (signUpForm == null) {
            signUpForm = new SignUpFormView(getContext(), this.bus, this.configuration);
        }
        formContainer.addView(signUpForm);

        changePasswordBtn.setVisibility(View.GONE);
    }

    private void showChangePasswordForm() {
        removePreviousForm();

        if (changePwdForm == null) {
            changePwdForm = new ChangePasswordFormView(getContext(), this.bus, this.configuration);
        }
        formContainer.addView(changePwdForm);

        changePasswordBtn.setVisibility(View.GONE);
        callback.onShowChangePassword();
    }

    private void showLoginForm() {
        removePreviousForm();

        if (loginForm == null) {
            loginForm = new LoginFormView(getContext(), this.bus, this.configuration);
        }
        formContainer.addView(loginForm);

        changePasswordBtn.setVisibility(configuration.isChangePasswordEnabled() ? View.VISIBLE : View.GONE);
    }

    private void showDomainForm(boolean fallbackToDatabase) {
        removePreviousForm();

        if (domainForm == null) {
            domainForm = new DomainFormView(getContext(), this.bus, this.configuration, fallbackToDatabase);
        }
        formContainer.addView(domainForm);

        changePasswordBtn.setVisibility(fallbackToDatabase && configuration.isChangePasswordEnabled() ? View.VISIBLE : View.GONE);
    }

    private void removePreviousForm() {
        View existingForm = formContainer.getChildAt(0);
        if (existingForm != null) {
            formContainer.removeView(existingForm);
        }
    }

    private void moveToFirstForm() {
        if (showDatabase && !showEnterprise) {
            showLoginForm();
        } else {
            showDomainForm(showDatabase);
        }
    }

    /**
     * Should be called to update the form layout.
     *
     * @return true if it was consumed, false otherwise.
     */
    public boolean onBackPressed() {
        if (changePwdForm != null && currentFormMode == FormMode.LOG_IN) {
            changePwdForm = null;
            moveToFirstForm();
            return true;
        }
        return false;
    }

    public interface ChangePasswordListener {
        void onShowChangePassword();
    }

}
