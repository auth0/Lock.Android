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
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetEnterprise;

public class FormLayout extends RelativeLayout implements ModeSelectionView.ModeSelectedListener {
    private static final String TAG = FormLayout.class.getSimpleName();
    private static final int SINGLE_FORM_POSITION = 0;
    private static final int MULTIPLE_FORMS_POSITION = 2;

    private final LockWidgetEnterprise lockWidget;
    private boolean showDatabase;
    private boolean showEnterprise;

    private LogInFormView loginForm;
    private SignUpFormView signUpForm;
    private DomainFormView domainForm;
    private SocialView socialLayout;
    private CustomFieldsFormView customFieldsForm;
    private TextView orSeparatorMessage;

    private LinearLayout formsHolder;
    private ModeSelectionView modeSelectionView;

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
        boolean showSocial = !lockWidget.getConfiguration().getSocialStrategies().isEmpty();
        showDatabase = lockWidget.getConfiguration().getDefaultDatabaseConnection() != null;
        showEnterprise = !lockWidget.getConfiguration().getEnterpriseStrategies().isEmpty();
        boolean showModeSelection = showDatabase && lockWidget.getConfiguration().isSignUpEnabled();

        int verticalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);

        if (showModeSelection) {
            Log.v(TAG, "SignUp enabled. Adding the Login/SignUp Mode Switcher");
            modeSelectionView = new ModeSelectionView(getContext(), this);
            modeSelectionView.setId(R.id.com_auth0_lock_form_selector);
            LayoutParams modeSelectionParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            modeSelectionParams.addRule(ALIGN_PARENT_TOP);
            modeSelectionParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
            addView(modeSelectionView, modeSelectionParams);
        }
        formsHolder = new LinearLayout(getContext());
        formsHolder.setOrientation(LinearLayout.VERTICAL);
        formsHolder.setGravity(Gravity.CENTER);
        LayoutParams holderParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holderParams.addRule(BELOW, R.id.com_auth0_lock_form_selector);
        holderParams.addRule(CENTER_VERTICAL);
        holderParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
        addView(formsHolder, holderParams);
        if (showSocial) {
            addSocialLayout(showDatabase || showEnterprise);
            if (showDatabase || showEnterprise) {
                addSeparator();
            }
        }
        changeFormMode(ModeSelectionView.Mode.LOG_IN);
    }

    private void addSocialLayout(boolean smallButtons) {
        socialLayout = new SocialView(lockWidget, smallButtons);
        formsHolder.addView(socialLayout);
    }

    private void addSeparator() {
        orSeparatorMessage = new TextView(getContext());
        orSeparatorMessage.setText(R.string.com_auth0_lock_forms_separator);
        orSeparatorMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.com_auth0_lock_text));
        orSeparatorMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.com_auth0_lock_title_text));
        orSeparatorMessage.setGravity(Gravity.CENTER);
        int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        orSeparatorMessage.setPadding(0, verticalPadding, 0, verticalPadding);
        formsHolder.addView(orSeparatorMessage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /**
     * Change the current form mode
     *
     * @param mode the new DatabaseMode to change to
     */
    private void changeFormMode(@ModeSelectionView.Mode int mode) {
        Log.d(TAG, "Mode changed to " + mode);
        if (!showDatabase && !showEnterprise) {
            return;
        }
        lockWidget.showTopBanner(false);
        switch (mode) {
            case ModeSelectionView.Mode.LOG_IN:
                addFormLayout();
                lockWidget.showBottomBanner(false);
                break;
            case ModeSelectionView.Mode.SIGN_UP:
                showSignUpForm();
                lockWidget.showBottomBanner(true);
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
        formsHolder.addView(signUpForm);
    }

    private void showDatabaseLoginForm() {
        removePreviousForm();

        if (loginForm == null) {
            loginForm = new LogInFormView(lockWidget);
        }
        formsHolder.addView(loginForm);
    }

    private void showEnterpriseForm() {
        removePreviousForm();

        if (domainForm == null) {
            domainForm = new DomainFormView(lockWidget);
        }
        formsHolder.addView(domainForm);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);

        int modeSelectionHeight = 0;
        if (modeSelectionView != null && modeSelectionView.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams modeSelectionParams = (MarginLayoutParams) modeSelectionView.getLayoutParams();
            modeSelectionHeight = modeSelectionView.getMeasuredHeight() + modeSelectionParams.topMargin + modeSelectionParams.bottomMargin;
        }

        int separatorHeight = 0;
        if (orSeparatorMessage != null && orSeparatorMessage.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams separatorParams = (MarginLayoutParams) orSeparatorMessage.getLayoutParams();
            separatorHeight = orSeparatorMessage.getMeasuredHeight() + separatorParams.topMargin + separatorParams.bottomMargin;
        }
        int socialHeight = 0;
        if (socialLayout != null && socialLayout.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams socialParams = (MarginLayoutParams) socialLayout.getLayoutParams();
            socialHeight = socialLayout.getMeasuredHeight() + socialParams.topMargin + socialParams.bottomMargin;
        }
        int fieldsHeight = 0;
        View existingForm = getExistingForm();
        if (existingForm != null && existingForm.getVisibility() == View.VISIBLE) {
            ViewGroup.MarginLayoutParams formParams = (MarginLayoutParams) existingForm.getLayoutParams();
            fieldsHeight = existingForm.getMeasuredHeight() + formParams.topMargin + formParams.bottomMargin;
        }

        int sumHeight = modeSelectionHeight + separatorHeight + socialHeight + fieldsHeight;
        Log.e(TAG, String.format("Parent height %d, FormReal height %d", parentHeight, sumHeight));
        setMeasuredDimension(getMeasuredWidth(), sumHeight);
    }

    private void showCustomFieldsForm(@NonNull DatabaseSignUpEvent event) {
        removePreviousForm();

        if (customFieldsForm == null) {
            customFieldsForm = new CustomFieldsFormView(lockWidget, event.getEmail(), event.getUsername(), event.getPassword());
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
        return formsHolder.getChildAt(formsHolder.getChildCount() == 1 ? SINGLE_FORM_POSITION : MULTIPLE_FORMS_POSITION);
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
        View existingForm = getExistingForm();
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

    @Override
    public void onModeSelected(@ModeSelectionView.Mode int mode) {
        Log.d(TAG, "Mode changed to " + mode);
        changeFormMode(mode);
    }
}
