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
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class FormLayout extends LinearLayout {
    private final LockWidgetForm lockWidget;

    private boolean showDatabase;
    private boolean showEnterprise;

    private LoginFormView loginForm;
    private SignUpFormView signUpForm;
    private DomainFormView domainForm;

    public enum DatabaseForm {LOG_IN, SIGN_UP}

    public FormLayout(Context context) {
        super(context);
        lockWidget = null;
    }

    public FormLayout(LockWidgetForm lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        boolean showSocial = !lockWidget.getConfiguration().getSocialStrategies().isEmpty();
        showDatabase = lockWidget.getConfiguration().getDefaultDatabaseConnection() != null;
        showEnterprise = !lockWidget.getConfiguration().getEnterpriseStrategies().isEmpty();
        if (showSocial) {
            addSocialLayout();
        }
        if (showDatabase || showEnterprise) {
            if (showSocial) {
                addSeparator();
            }
            addFormLayout();
        }
    }

    private void addSocialLayout() {
        SocialView socialLayout = new SocialView(lockWidget, SocialView.Mode.List);
        addView(socialLayout);
    }

    private void addSeparator() {
        TextView orSeparatorMessage = new TextView(getContext());
        orSeparatorMessage.setText(R.string.com_auth0_lock_forms_separator);
        orSeparatorMessage.setGravity(Gravity.CENTER);
        int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_input_field_vertical_margin_small);
        orSeparatorMessage.setPadding(0, verticalPadding, 0, verticalPadding);
        addView(orSeparatorMessage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Change the current form mode
     *
     * @param mode the new DatabaseForm to change to
     */
    public void changeFormMode(DatabaseForm mode) {
        switch (mode) {
            case LOG_IN:
                addFormLayout();
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
        addView(signUpForm);
    }

    private void showDatabaseLoginForm() {
        removePreviousForm();

        if (loginForm == null) {
            loginForm = new LoginFormView(lockWidget);
        }
        addView(loginForm);
    }

    private void showEnterpriseForm() {
        removePreviousForm();

        if (domainForm == null) {
            domainForm = new DomainFormView(lockWidget);
        }
        addView(domainForm);
    }

    private void removePreviousForm() {
        View existingForm = getChildAt(getChildCount() == 1 ? 0 : 2);
        if (existingForm != null) {
            removeView(existingForm);
        }
    }

    private void addFormLayout() {
        if (showDatabase && !showEnterprise) {
            showDatabaseLoginForm();
        } else {
            showEnterpriseForm();
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
        View existingForm = getChildAt(getChildCount() == 1 ? 0 : 2);
        if (existingForm != null) {
            FormView form = (FormView) existingForm;
            return form.submitForm();
        }
        return null;
    }
}
