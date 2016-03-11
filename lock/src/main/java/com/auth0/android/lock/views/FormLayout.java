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
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.RelativeLayout;

import com.auth0.android.lock.R;
import com.auth0.android.lock.views.interfaces.LockWidgetDatabase;

public class FormLayout extends RelativeLayout {
    private final LockWidgetDatabase lockWidget;

    private RelativeLayout formContainer;
    private boolean showDatabase;
    private boolean showEnterprise;

    private LoginFormView loginForm;
    private SignUpFormView signUpForm;
    private DomainFormView domainForm;

    public enum FormMode {LOG_IN, SIGN_UP}

    public FormLayout(Context context) {
        super(context);
        lockWidget = null;
    }

    public FormLayout(LockWidgetDatabase lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_database_layout, this);
        formContainer = (RelativeLayout) findViewById(R.id.com_auth0_lock_form_layout);

        showDatabase = lockWidget.getConfiguration().getDefaultDatabaseConnection() != null;
        showEnterprise = !lockWidget.getConfiguration().getEnterpriseStrategies().isEmpty();

        moveToFirstForm();
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
    }

    private void showSignUpForm() {
        removePreviousForm();

        if (signUpForm == null) {
            signUpForm = new SignUpFormView(lockWidget);
        }
        formContainer.addView(signUpForm);
    }

    private void showLoginForm() {
        removePreviousForm();

        if (loginForm == null) {
            loginForm = new LoginFormView(lockWidget);
        }
        formContainer.addView(loginForm);
    }

    private void showDomainForm() {
        removePreviousForm();

        if (domainForm == null) {
            domainForm = new DomainFormView(lockWidget);
        }
        formContainer.addView(domainForm);
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
            showDomainForm();
        }
    }

    /**
     * Should be called to update the form layout.
     *
     * @return true if it was consumed, false otherwise.
     */
    public boolean onBackPressed() {
        if (domainForm != null && domainForm.onBackPressed()) {
            return true;
        }
        return false;
    }

    /**
     * ActionButton has been clicked, and validation should be run on the current
     * visible form. If this validation passes, an action event will be returned.
     *
     * @return the action event of the current visible form or null if validation failed
     */
    @Nullable
    public Object onActionPressed() {
        View existingForm = formContainer.getChildAt(0);
        if (existingForm != null) {
            FormView form = (FormView) existingForm;
            if (form.hasValidData()) {
                return form.getActionEvent();
            }
        }
        return null;
    }
}
