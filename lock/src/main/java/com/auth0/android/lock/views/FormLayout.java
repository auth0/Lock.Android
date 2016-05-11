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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetEnterprise;

public class FormLayout extends LinearLayout {
    private static final int SINGLE_FORM_POSITION = 0;
    private static final int MULTIPLE_FORMS_POSITION = 2;
    private static final String TAG = FormLayout.class.getSimpleName();

    private final LockWidgetEnterprise lockWidget;
    private boolean showDatabase;
    private boolean showEnterprise;

    private LogInFormView loginForm;
    private SignUpFormView signUpForm;
    private DomainFormView domainForm;
    private SocialView socialLayout;
    private CustomFieldsFormView customFieldsForm;
    private TextView orSeparatorMessage;

    public FormLayout(Context context) {
        super(context);
        lockWidget = null;
    }

    public FormLayout(LockWidgetEnterprise lockWidget) {
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
            addSocialLayout(showDatabase || showEnterprise);
        }
        if (showDatabase || showEnterprise) {
            if (showSocial) {
                addSeparator();
            }
            addFormLayout();
        }
    }

    private void addSocialLayout(boolean smallButtons) {
        socialLayout = new SocialView(lockWidget, smallButtons);
        addView(socialLayout);
    }

    private void addSeparator() {
        orSeparatorMessage = new TextView(getContext());
        orSeparatorMessage.setText(R.string.com_auth0_lock_forms_separator);
        orSeparatorMessage.setTextColor(ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_text));
        orSeparatorMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.com_auth0_lock_title_text));
        orSeparatorMessage.setGravity(Gravity.CENTER);
        int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        orSeparatorMessage.setPadding(0, verticalPadding, 0, verticalPadding);
        addView(orSeparatorMessage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Change the current form mode
     *
     * @param mode the new DatabaseMode to change to
     */
    public void changeFormMode(@ModeSelectionView.Mode int mode) {
        Log.d(TAG, "Mode changed to " + mode);
        if (!showDatabase && !showEnterprise) {
            return;
        }
        switch (mode) {
            case ModeSelectionView.Mode.LOG_IN:
                addFormLayout();
                break;
            case ModeSelectionView.Mode.SIGN_UP:
                showSignUpForm();
                break;
        }
    }

    public void showOnlyEnterprise(boolean show) {
        if (socialLayout != null) {
            socialLayout.setVisibility(show ? GONE : VISIBLE);
        }
        if (orSeparatorMessage != null) {
            orSeparatorMessage.setVisibility(show ? GONE : VISIBLE);
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
            loginForm = new LogInFormView(lockWidget);
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

    private void showCustomFieldsForm(@NonNull DatabaseSignUpEvent event) {
        removePreviousForm();

        if (customFieldsForm == null) {
            customFieldsForm = new CustomFieldsFormView(lockWidget, event.getEmail(), event.getUsername(), event.getPassword());
        }
        addView(customFieldsForm);
    }

    private void removePreviousForm() {
        View existingForm = getChildAt(getChildCount() == 1 ? SINGLE_FORM_POSITION : MULTIPLE_FORMS_POSITION);
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
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        if (loginForm != null) {
            loginForm.onKeyboardStateChanged(isOpen);
        }
        if (domainForm != null) {
            domainForm.onKeyboardStateChanged(isOpen);
            if (domainForm.isEnterpriseDomainMatch()) {
                isOpen = true;
            }
        }
        if (orSeparatorMessage != null) {
            orSeparatorMessage.setVisibility(isOpen ? GONE : VISIBLE);
        }
        if (socialLayout != null) {
            socialLayout.setVisibility(isOpen ? GONE : VISIBLE);
        }
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        return domainForm != null && domainForm.onBackPressed();
    }

    /**
     * ActionButton has been clicked, and validation should be run on the current
     * visible form. If this validation passes, an action event will be returned.
     *
     * @return the action event of the current visible form or null if validation failed
     */
    @Nullable
    public Object onActionPressed() {
        View existingForm = getChildAt(getChildCount() == 1 ? SINGLE_FORM_POSITION : MULTIPLE_FORMS_POSITION);
        if (existingForm == null) {
            return null;
        }

        FormView form = (FormView) existingForm;
        Object ev = form.submitForm();
        if (ev == null || !lockWidget.getConfiguration().hasExtraFields()) {
            return ev;
        } else if (existingForm == signUpForm) {
            //User has configured some extra SignUp custom fields.
            DatabaseSignUpEvent event = (DatabaseSignUpEvent) ev;
            showCustomFieldsForm(event);
            return null;
        }
        return ev;
    }
}
