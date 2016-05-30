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
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

public class FormLayout extends RelativeLayout implements ModeSelectionView.ModeSelectedListener {
    private static final String TAG = FormLayout.class.getSimpleName();
    private static final int SINGLE_FORM_POSITION = 0;
    private static final int MULTIPLE_FORMS_POSITION = 2;

    private final LockWidgetForm lockWidget;
    private boolean showSocial;
    private boolean showDatabase;
    private boolean showEnterprise;
    private boolean showModeSelection;
    private boolean showSocialBigButtons;

    private boolean keyboardIsOpen;
    private boolean showingOtherProviderForm;

    private SignUpFormView signUpForm;
    private LogInFormView logInForm;
    private SocialView socialLayout;
    private CustomFieldsFormView customFieldsForm;
    private TextView orSeparatorMessage;
    private TextView continueWithOtherProvider;

    private LinearLayout formsHolder;
    private ModeSelectionView modeSelectionView;

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
        showSocial = !lockWidget.getConfiguration().getSocialStrategies().isEmpty();
        showDatabase = lockWidget.getConfiguration().getDefaultDatabaseConnection() != null;
        showEnterprise = !lockWidget.getConfiguration().getEnterpriseStrategies().isEmpty();
        showModeSelection = showDatabase && lockWidget.getConfiguration().isSignUpEnabled();
        showSocialBigButtons = true; //TODO: Make it configurable from the Builder

        formsHolder = new LinearLayout(getContext());
        formsHolder.setOrientation(LinearLayout.VERTICAL);
        formsHolder.setGravity(Gravity.CENTER);

        int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);
        LayoutParams holderParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        holderParams.addRule(BELOW, R.id.com_auth0_lock_form_selector);
        holderParams.addRule(CENTER_VERTICAL);
        holderParams.setMargins(horizontalMargin, 0, horizontalMargin, 0);
        addView(formsHolder, holderParams);

        if (showSocial) {
            addSocialLayout();
            if (showSocialBigButtons) {
                addContinueWithOtherProviderLink();
                lockWidget.showActionButton(false);
            } else {
                addSeparator();
                changeFormMode(ModeSelectionView.Mode.LOG_IN);
            }
        } else {
            changeFormMode(ModeSelectionView.Mode.LOG_IN);
        }
    }


    private void addModeSelector() {
        if (showModeSelection) {
            int verticalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
            int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);
            Log.v(TAG, "SignUp enabled. Adding the Login/SignUp Mode Switcher");

            modeSelectionView = new ModeSelectionView(getContext(), this);
            modeSelectionView.setId(R.id.com_auth0_lock_form_selector);
            LayoutParams modeSelectionParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            modeSelectionParams.addRule(ALIGN_PARENT_TOP);
            modeSelectionParams.setMargins(horizontalMargin, verticalMargin, horizontalMargin, verticalMargin);
            addView(modeSelectionView, modeSelectionParams);
        }
    }

    private void removeModeSelector() {
        removeView(modeSelectionView);
        modeSelectionView = null;
    }

    private void addSocialLayout() {
        boolean smallButtons = !showSocialBigButtons && (showDatabase || showEnterprise);
        socialLayout = new SocialView(lockWidget, smallButtons);
        formsHolder.addView(socialLayout);
    }

    private void addSeparator() {
        if (!showDatabase && !showEnterprise) {
            return;
        }
        orSeparatorMessage = new TextView(getContext());
        orSeparatorMessage.setText(R.string.com_auth0_lock_forms_separator);
        orSeparatorMessage.setTextColor(ContextCompat.getColor(getContext(), R.color.com_auth0_lock_text));
        orSeparatorMessage.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.com_auth0_lock_title_text));
        orSeparatorMessage.setGravity(Gravity.CENTER);
        int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        orSeparatorMessage.setPadding(0, verticalPadding, 0, verticalPadding);
        formsHolder.addView(orSeparatorMessage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void addContinueWithOtherProviderLink() {
        if (!showDatabase && !showEnterprise) {
            return;
        }
        continueWithOtherProvider = new TextView(getContext());
        continueWithOtherProvider.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showingOtherProviderForm = true;
                lockWidget.showActionButton(true);
                formsHolder.removeView(socialLayout);
                removeView(continueWithOtherProvider);
                socialLayout = null;
                addModeSelector();
                changeFormMode(ModeSelectionView.Mode.LOG_IN);
            }
        });
        continueWithOtherProvider.setText("Sign in or Sign up with email");
        continueWithOtherProvider.setTextColor(ContextCompat.getColor(getContext(), R.color.com_auth0_lock_text));
        continueWithOtherProvider.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.com_auth0_lock_title_text));
        continueWithOtherProvider.setGravity(Gravity.CENTER_HORIZONTAL);
        int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        LayoutParams linkParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        linkParams.addRule(ALIGN_PARENT_BOTTOM);
        linkParams.setMargins(0, verticalPadding, 0, verticalPadding);
        addView(continueWithOtherProvider, linkParams);
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
                showLogInForm();
                lockWidget.showBottomBanner(false);
                break;
            case ModeSelectionView.Mode.SIGN_UP:
                showSignUpForm();
                lockWidget.showBottomBanner(true);
                break;
        }
    }

    public void showOnlyEnterprise(boolean show) {
        if (keyboardIsOpen) {
            return;
        }
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

    private void showLogInForm() {
        removePreviousForm();

        if (logInForm == null) {
            logInForm = new LogInFormView(lockWidget);
        }
        formsHolder.addView(logInForm);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int modeSelectionHeight = ViewUtils.measureViewHeight(modeSelectionView);
        int separatorHeight = ViewUtils.measureViewHeight(orSeparatorMessage);
        int continueWithOtherProviderHeight = ViewUtils.measureViewHeight(continueWithOtherProvider);
        int socialHeight = ViewUtils.measureViewHeight(socialLayout);
        int fieldsHeight = ViewUtils.measureViewHeight(getExistingForm());
        int sumHeight = modeSelectionHeight + separatorHeight + continueWithOtherProviderHeight + socialHeight + fieldsHeight;

        Log.v(TAG, String.format("Parent height %d, FormReal height %d", parentHeight, sumHeight));
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

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        keyboardIsOpen = isOpen;
        if (logInForm != null) {
            logInForm.onKeyboardStateChanged(isOpen);
            if (logInForm.isEnterpriseDomainMatch()) {
                isOpen = true;
            }
        }
        if (modeSelectionView != null) {
            modeSelectionView.setVisibility(isOpen ? GONE : VISIBLE);
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
        if (logInForm != null && logInForm.onBackPressed()) {
            return true;
        } else if (showSocial && showingOtherProviderForm) {
            showingOtherProviderForm = false;
            lockWidget.showTopBanner(false);
            lockWidget.showBottomBanner(false);
            lockWidget.showActionButton(false);
            removePreviousForm();
            removeModeSelector();
            addSocialLayout();
            addContinueWithOtherProviderLink();
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
