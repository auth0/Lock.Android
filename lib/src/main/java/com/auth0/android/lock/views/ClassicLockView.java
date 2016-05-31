/*
 * ClassicLockView.java
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
import android.support.percent.PercentRelativeLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.InitialScreen;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.FetchApplicationEvent;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.views.interfaces.LockWidget;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;
import com.squareup.otto.Bus;

public class ClassicLockView extends PercentRelativeLayout implements View.OnClickListener, LockWidget, LockWidgetForm {

    private static final String TAG = ClassicLockView.class.getSimpleName();
    private final Bus bus;
    private Configuration configuration;

    private FormLayout formLayout;
    private FormView subForm;

    private HeaderView headerView;
    private View topBanner;
    private View bottomBanner;
    private ActionButton actionButton;
    private ProgressBar loadingProgressBar;

    public ClassicLockView(Context context) {
        super(context);
        bus = null;
        configuration = null;
    }

    public ClassicLockView(Context context, Bus lockBus) {
        super(context);
        this.bus = lockBus;
        this.configuration = null;
        showWaitForConfigurationLayout();
    }

    private void init() {
        if (configuration == null) {
            Log.w(TAG, "Configuration is missing, the view won't init.");
            showConfigurationMissingLayout();
        } else {
            showContentLayout();
        }
    }

    private void showWaitForConfigurationLayout() {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT, TRUE);
        loadingProgressBar = new ProgressBar(getContext());
        loadingProgressBar.setIndeterminate(true);
        addView(loadingProgressBar, params);
    }

    private void showContentLayout() {
        TypedValue typedValue = new TypedValue();

        getResources().getValue(R.dimen.com_auth0_lock_header_view_height, typedValue, true);
        float height = typedValue.getFloat();
        LayoutParams headerViewParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        headerViewParams.addRule(ALIGN_PARENT_TOP);
        headerViewParams.getPercentLayoutInfo().heightPercent = height;

        getResources().getValue(R.dimen.com_auth0_lock_top_banner_height, typedValue, true);
        height = typedValue.getFloat();
        LayoutParams topBannerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        topBannerParams.addRule(BELOW, R.id.com_auth0_lock_header);
        topBannerParams.getPercentLayoutInfo().heightPercent = height;

        getResources().getValue(R.dimen.com_auth0_lock_bottom_banner_height, typedValue, true);
        height = typedValue.getFloat();
        LayoutParams bottomBannerParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        bottomBannerParams.addRule(ABOVE, R.id.com_auth0_lock_action_button);
        bottomBannerParams.alignWithParent = true;
        bottomBannerParams.getPercentLayoutInfo().heightPercent = height;

        getResources().getValue(R.dimen.com_auth0_lock_action_button_height, typedValue, true);
        height = typedValue.getFloat();
        LayoutParams actionButtonParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionButtonParams.addRule(ALIGN_PARENT_BOTTOM);
        actionButtonParams.getPercentLayoutInfo().heightPercent = height;

        LayoutParams formLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        formLayoutParams.alignWithParent = true;
        formLayoutParams.addRule(ABOVE, R.id.com_auth0_lock_banner_bottom);
        formLayoutParams.addRule(BELOW, R.id.com_auth0_lock_banner_top);

        headerView = new HeaderView(getContext());
        headerView.setId(R.id.com_auth0_lock_header);
        addView(headerView, headerViewParams);

        topBanner = inflate(getContext(), R.layout.com_auth0_lock_sso_layout, null);
        topBanner.setId(R.id.com_auth0_lock_banner_top);
        topBanner.setVisibility(GONE);
        addView(topBanner, topBannerParams);

        actionButton = new ActionButton(getContext());
        actionButton.setId(R.id.com_auth0_lock_action_button);
        actionButton.setOnClickListener(this);
        addView(actionButton, actionButtonParams);

        bottomBanner = inflate(getContext(), R.layout.com_auth0_lock_terms_layout, null);
        bottomBanner.setId(R.id.com_auth0_lock_banner_bottom);
        bottomBanner.setVisibility(GONE);
        addView(bottomBanner, bottomBannerParams);

        formLayout = new FormLayout(this);
        formLayout.setId(R.id.com_auth0_lock_form_layout);
        addView(formLayout, formLayoutParams);


        boolean showDatabase = configuration.getDefaultDatabaseConnection() != null;
        boolean showEnterprise = !configuration.getEnterpriseStrategies().isEmpty();
        if (!showDatabase && !showEnterprise) {
            actionButton.setVisibility(GONE);
        }

        if (configuration.allowForgotPassword() && configuration.getInitialScreen() == InitialScreen.FORGOT_PASSWORD) {
            showChangePasswordForm(true);
        }
    }

    /**
     * Setup the panel to show the correct forms by reading the Auth0 Configuration.
     *
     * @param configuration the configuration to use on this view, or null if it is missing.
     */
    public void configure(@Nullable Configuration configuration) {
        removeView(loadingProgressBar);
        loadingProgressBar = null;
        this.configuration = configuration;
        if (configuration != null) {
            init();
            return;
        }
        showConfigurationMissingLayout();
    }

    private void showConfigurationMissingLayout() {
        int horizontalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_horizontal_margin);
        final LinearLayout errorLayout = new LinearLayout(getContext());
        errorLayout.setOrientation(LinearLayout.VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(horizontalMargin, 0, horizontalMargin, 0);
        params.addRule(CENTER_IN_PARENT, TRUE);

        TextView errorText = new TextView(getContext());
        errorText.setText(R.string.com_auth0_lock_configuration_retrieving_error);
        errorText.setGravity(Gravity.CENTER);

        Button retryButton = new Button(getContext());
        retryButton.setText(R.string.com_auth0_lock_action_retry);
        retryButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(new FetchApplicationEvent());
                removeView(errorLayout);
                showWaitForConfigurationLayout();
            }
        });
        LinearLayout.LayoutParams childParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childParams.gravity = Gravity.CENTER;
        errorLayout.addView(errorText, childParams);
        errorLayout.addView(retryButton, childParams);
        addView(errorLayout, params);
    }

    @Override
    public void showChangePasswordForm(boolean show) {
        if (show) {
            addSubForm(new ChangePasswordFormView(this));
        } else {
            removeSubForm();
        }
    }

    private void addSubForm(@NonNull FormView form) {
        formLayout.setVisibility(GONE);

        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.addRule(ABOVE, R.id.com_auth0_lock_banner_bottom);
        params.addRule(BELOW, R.id.com_auth0_lock_banner_top);
        params.addRule(CENTER_IN_PARENT, TRUE);
        addView(form, params);
        this.subForm = form;
    }

    private void removeSubForm() {
        formLayout.setVisibility(VISIBLE);
        if (this.subForm != null) {
            removeView(this.subForm);
            this.subForm = null;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (configuration == null) {
            return;
        }

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int headerViewHeight = ViewUtils.measureViewHeight(headerView);
        int topBannerHeight = ViewUtils.measureViewHeight(topBanner);
        int formHeight = ViewUtils.measureViewHeight(formLayout);
        int bottomBannerHeight = ViewUtils.measureViewHeight(bottomBanner);
        int actionButtonHeight = ViewUtils.measureViewHeight(actionButton);
        int freeFormSpace = parentHeight - headerViewHeight - topBannerHeight - bottomBannerHeight - actionButtonHeight;

        Log.v(TAG, String.format("Parent is %d and free space for form: %d. The form needs %d (header %d + topBanner %d + botBanner %d + actionButton %d)", parentHeight, freeFormSpace, formHeight, headerViewHeight, topBannerHeight, bottomBannerHeight, actionButtonHeight));
        changeHeaderSize(freeFormSpace < formHeight || subForm instanceof CustomFieldsFormView);
    }

    private void changeHeaderSize(boolean collapse) {
        TypedValue typedValue = new TypedValue();
        getResources().getValue(collapse ? R.dimen.com_auth0_lock_small_header_view_height : R.dimen.com_auth0_lock_header_view_height, typedValue, true);
        float height = typedValue.getFloat();

        PercentRelativeLayout.LayoutParams headerViewParams = (PercentRelativeLayout.LayoutParams) headerView.getLayoutParams();
        headerViewParams.getPercentLayoutInfo().heightPercent = height;
        headerView.showTitle(!collapse);
    }

    /**
     * Triggers the back action on the form.
     *
     * @return true if it was handled, false otherwise
     */
    public boolean onBackPressed() {
        if (subForm != null) {
            final boolean shouldDisplayPreviousForm = configuration.allowLogIn() || configuration.allowSignUp();
            if (shouldDisplayPreviousForm) {
                showSignUpTerms(subForm instanceof CustomFieldsFormView);
                removeSubForm();
                return true;
            }
        }

        return formLayout != null && formLayout.onBackPressed();
    }

    /**
     * Displays a progress bar on top of the action button. This will also
     * enable or disable the action button.
     *
     * @param show whether to show or hide the action bar.
     */
    public void showProgress(boolean show) {
        if (actionButton != null) {
            actionButton.showProgress(show);
        }
        formLayout.setEnabled(!show);
    }

    private void showSignUpTerms(boolean show) {
        bottomBanner.setVisibility(show ? VISIBLE : GONE);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onFormSubmit() {
        actionButton.callOnClick();
    }

    @Override
    public void onClick(View v) {
        Object event = subForm != null ? subForm.submitForm() : formLayout.onActionPressed();
        if (event != null) {
            bus.post(event);
        }
    }

    @Override
    public void showCustomFieldsForm(DatabaseSignUpEvent event) {
        addSubForm(new CustomFieldsFormView(this, event.getEmail(), event.getUsername(), event.getPassword()));
        showSignUpTerms(false);
    }

    public void showMFACodeForm(DatabaseLoginEvent event) {
        addSubForm(new MFACodeFormView(this, event.getUsernameOrEmail(), event.getPassword()));
    }

    @Override
    public void onSocialLogin(SocialConnectionEvent event) {
        Log.d(TAG, "Social login triggered for connection " + event.getConnectionName());
        bus.post(event);
    }

    /**
     * Notifies this forms and its child views that the keyboard state changed, so that
     * it can change the layout in order to fit all the fields.
     *
     * @param isOpen whether the keyboard is open or close.
     */
    public void onKeyboardStateChanged(boolean isOpen) {
        if (subForm != null) {
            subForm.onKeyboardStateChanged(isOpen);
            bottomBanner.setVisibility(!isOpen && subForm instanceof SignUpFormView ? VISIBLE : GONE);
        }
        headerView.setVisibility(isOpen ? GONE : VISIBLE);
        formLayout.onKeyboardStateChanged(isOpen);
    }

    @Override
    public void showTopBanner(boolean show) {
        topBanner.setVisibility(show ? VISIBLE : GONE);
        if (formLayout != null) {
            formLayout.showOnlyEnterprise(show);
        }
    }

    @Override
    public void showBottomBanner(boolean show) {
        bottomBanner.setVisibility(show ? VISIBLE : GONE);
    }
}
