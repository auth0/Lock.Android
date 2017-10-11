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
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.AuthButtonSize;
import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.internal.configuration.AuthMode;
import com.auth0.android.lock.views.interfaces.IdentityListener;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class FormLayout extends RelativeLayout implements ModeSelectionView.ModeSelectedListener, IdentityListener {
    private static final String TAG = FormLayout.class.getSimpleName();
    private static final int SINGLE_FORM_POSITION = 0;
    private static final int MULTIPLE_FORMS_POSITION = 2;

    private final LockWidgetForm lockWidget;
    private boolean showDatabase;
    private boolean showEnterprise;

    private SignUpFormView signUpForm;
    private LogInFormView logInForm;
    private SocialView socialLayout;
    private CustomFieldsFormView customFieldsForm;
    private TextView orSeparatorMessage;

    private LinearLayout formsHolder;
    private ModeSelectionView modeSelectionView;

    private String lastEmailInput;

    @AuthMode
    private int lastFormMode = -1;

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
        boolean showSocial = !lockWidget.getConfiguration().getSocialConnections().isEmpty();
        showDatabase = lockWidget.getConfiguration().getDatabaseConnection() != null;
        showEnterprise = !lockWidget.getConfiguration().getEnterpriseConnections().isEmpty();
        boolean showModeSelection = showDatabase && lockWidget.getConfiguration().allowLogIn() && lockWidget.getConfiguration().allowSignUp();

        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_horizontal_margin);

        if (showModeSelection) {
            Log.v(TAG, "Showing the LogIn/SignUp tabs");
            modeSelectionView = new ModeSelectionView(getContext(), this);
            modeSelectionView.setId(R.id.com_auth0_lock_form_selector);
            LayoutParams modeSelectionParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            modeSelectionParams.addRule(ALIGN_PARENT_TOP);
            addView(modeSelectionView, modeSelectionParams);
        }
        formsHolder = new LinearLayout(getContext());
        formsHolder.setOrientation(LinearLayout.VERTICAL);
        formsHolder.setGravity(Gravity.CENTER);
        formsHolder.setPadding(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            formsHolder.setPaddingRelative(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
        }
        LayoutParams holderParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holderParams.addRule(BELOW, R.id.com_auth0_lock_form_selector);
        holderParams.addRule(CENTER_VERTICAL);
        addView(formsHolder, holderParams);

        if (showSocial) {
            addSocialLayout();
            if (showDatabase || showEnterprise) {
                addSeparator();
            }
        }
        displayInitialScreen();
    }

    private void displayInitialScreen() {
        if (!showDatabase && !showEnterprise) {
            return;
        }
        int mode;
        int initialScreen = lockWidget.getConfiguration().getInitialScreen();
        if (initialScreen == InitialScreen.FORGOT_PASSWORD) {
            mode = lockWidget.getConfiguration().allowLogIn() ? AuthMode.LOG_IN : AuthMode.SIGN_UP;
        } else {
            mode = initialScreen == InitialScreen.SIGN_UP ? AuthMode.SIGN_UP : AuthMode.LOG_IN;
        }

        if (modeSelectionView != null) {
            modeSelectionView.setSelectedMode(mode);
        } else {
            changeFormMode(mode);
        }
    }

    private void addSocialLayout() {
        int style = lockWidget.getConfiguration().getSocialButtonStyle();
        boolean formContainsFields = showDatabase || showEnterprise;
        boolean singleConnection = lockWidget.getConfiguration().getSocialConnections().size() == 1;

        if (style == AuthButtonSize.UNSPECIFIED) {
            socialLayout = new SocialView(lockWidget, formContainsFields && !singleConnection);
        } else {
            socialLayout = new SocialView(lockWidget, style == AuthButtonSize.SMALL);
        }

        formsHolder.addView(socialLayout);
    }

    private void addSeparator() {
        orSeparatorMessage = new LineSpacingTextView(getContext());
        orSeparatorMessage.setText(R.string.com_auth0_lock_forms_separator);
        orSeparatorMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.com_auth0_lock_text));
        orSeparatorMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.com_auth0_lock_title_text));
        orSeparatorMessage.setGravity(Gravity.CENTER);
        int verticalPadding = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        orSeparatorMessage.setPadding(0, verticalPadding, 0, verticalPadding);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            orSeparatorMessage.setPaddingRelative(0, verticalPadding, 0, verticalPadding);
        }
        formsHolder.addView(orSeparatorMessage, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Change the current form mode
     *
     * @param mode the new DatabaseMode to change to
     */
    private void changeFormMode(@AuthMode int mode) {
        Log.d(TAG, "Mode changed to " + mode);
        if (lastFormMode == mode || !showDatabase && !showEnterprise) {
            return;
        }
        Log.d(TAG, "Mode changed to " + mode);
        lastFormMode = mode;
        lockWidget.showTopBanner(false);
        if (socialLayout != null) {
            socialLayout.setCurrentMode(mode);
        }
        switch (mode) {
            case AuthMode.LOG_IN:
                showLogInForm();
                lockWidget.showBottomBanner(false);
                lockWidget.updateButtonLabel(R.string.com_auth0_lock_action_log_in);
                break;
            case AuthMode.SIGN_UP:
                showSignUpForm();
                lockWidget.showBottomBanner(true);
                lockWidget.updateButtonLabel(R.string.com_auth0_lock_action_sign_up);
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
        if (modeSelectionView != null) {
            modeSelectionView.setVisibility(show ? GONE : VISIBLE);
        }
    }

    private void showSignUpForm() {
        removePreviousForm();

        if (signUpForm == null) {
            signUpForm = new SignUpFormView(lockWidget);
        }
        signUpForm.setLastEmail(lastEmailInput);
        signUpForm.clearEmptyFieldsError();
        formsHolder.addView(signUpForm);
    }

    private void showLogInForm() {
        removePreviousForm();

        if (logInForm == null) {
            logInForm = new LogInFormView(lockWidget);
        }
        logInForm.setLastEmail(lastEmailInput);
        logInForm.clearEmptyFieldsError();
        formsHolder.addView(logInForm);
    }

    private void showCustomFieldsForm(@NonNull DatabaseSignUpEvent event) {
        removePreviousForm();

        if (customFieldsForm == null) {
            customFieldsForm = new CustomFieldsFormView(lockWidget, event.getEmail(), event.getPassword(), event.getUsername());
        }
        formsHolder.addView(customFieldsForm);
    }

    private void removePreviousForm() {
        View existingForm = getExistingForm();
        if (existingForm != null) {
            formsHolder.removeView(existingForm);
        }
    }

    @Nullable
    private View getExistingForm() {
        return formsHolder == null ? null : formsHolder.getChildAt(formsHolder.getChildCount() == 1 ? SINGLE_FORM_POSITION : MULTIPLE_FORMS_POSITION);
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        return logInForm != null && logInForm.onBackPressed();
    }

    /**
     * ActionButton has been clicked, and validation should be run on the current
     * visible form. If this validation passes, an action event will be returned.
     *
     * @return the action event of the current visible form or null if validation failed
     */
    @Nullable
    public Object onActionPressed() {
        View existingForm = getExistingForm();
        if (existingForm == null) {
            return null;
        }

        FormView form = (FormView) existingForm;
        Object ev = form.submitForm();
        if (ev == null || lockWidget.getConfiguration().getExtraSignUpFields().size() <= SignUpFormView.MAX_FEW_CUSTOM_FIELDS) {
            return ev;
        } else if (existingForm == signUpForm) {
            //User has configured some extra SignUp custom fields.
            DatabaseSignUpEvent event = (DatabaseSignUpEvent) ev;
            showCustomFieldsForm(event);
            return null;
        }
        return ev;
    }

    @Override
    public void onModeSelected(@AuthMode int mode) {
        Log.d(TAG, "Mode changed to " + mode);
        changeFormMode(mode);
    }

    @Override
    public int getSelectedMode() {
        return lastFormMode;
    }

    @Override
    public void onEmailChanged(String currentValue) {
        lastEmailInput = currentValue;
    }

    public void refreshIdentityInput() {
        if (logInForm != null) {
            logInForm.setLastEmail(lastEmailInput);
        }
        if (signUpForm != null) {
            signUpForm.setLastEmail(lastEmailInput);
        }
    }
}
