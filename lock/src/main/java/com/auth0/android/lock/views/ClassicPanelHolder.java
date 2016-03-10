/*
 * PanelHolder.java
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
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.squareup.otto.Bus;

public class ClassicPanelHolder extends LinearLayout implements ModeSelectionView.FormModeChangedListener, FormLayout.ChangePasswordListener {

    private final Bus bus;
    private final Configuration configuration;
    private FormLayout formLayout;
    private ModeSelectionView modeSelectionView;
    private SocialView socialLayout;
    private TextView orSeparatorMessage;
    private ChangePasswordFormView changePwdForm;

    public ClassicPanelHolder(Context context) {
        super(context);
        bus = null;
        configuration = null;
    }

    public ClassicPanelHolder(Context context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        boolean showSocial = !configuration.getSocialStrategies().isEmpty();
        boolean showLoginForm = configuration.getDefaultDatabaseConnection() != null || !configuration.getEnterpriseStrategies().isEmpty();

        if (showSocial && showLoginForm) {
            socialLayout = new SocialView(getContext(), bus, configuration, SocialView.Mode.List);
            formLayout = new FormLayout(getContext(), bus, configuration, this);
        } else if (showLoginForm) {
            formLayout = new FormLayout(getContext(), bus, configuration, this);
        } else if (showSocial) {
            socialLayout = new SocialView(getContext(), bus, configuration, SocialView.Mode.List);
        }

        if (configuration.getDefaultDatabaseConnection() != null && configuration.isSignUpEnabled()) {
            modeSelectionView = new ModeSelectionView(getContext(), this);
            addView(modeSelectionView);
        }
        if (socialLayout != null) {
            addView(socialLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (socialLayout != null && formLayout != null) {
            orSeparatorMessage = new TextView(getContext());
            orSeparatorMessage.setText(R.string.com_auth0_lock_forms_separator);
            orSeparatorMessage.setGravity(Gravity.CENTER);
            int verticalPadding = (int) getResources().getDimension(R.dimen.com_auth0_lock_input_field_vertical_margin_small);
            orSeparatorMessage.setPadding(0, verticalPadding, 0, verticalPadding);
            addView(orSeparatorMessage, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (formLayout != null) {
            addView(formLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    private void showChangePasswordForm(boolean show) {
        if (formLayout != null) {
            formLayout.setVisibility(show ? GONE : VISIBLE);
        }
        if (modeSelectionView != null) {
            modeSelectionView.setVisibility(show ? GONE : VISIBLE);
        }
        if (socialLayout != null) {
            socialLayout.setVisibility(show ? GONE : VISIBLE);
        }

        if (orSeparatorMessage != null) {
            orSeparatorMessage.setVisibility(show ? GONE : VISIBLE);
        }

        if (changePwdForm == null && show) {
            changePwdForm = new ChangePasswordFormView(getContext(), this.bus, this.configuration);
            addView(changePwdForm);
        } else if (changePwdForm != null && !show) {
            removeView(changePwdForm);
            changePwdForm = null;
        }
    }

    public boolean onBackPressed() {
        if (changePwdForm != null && changePwdForm.getVisibility() == VISIBLE) {
            showChangePasswordForm(false);
            return true;
        }
        boolean handled = formLayout != null && formLayout.onBackPressed();
        if (handled) {
            modeSelectionView.setVisibility(View.VISIBLE);
            if (socialLayout != null && formLayout != null) {
                orSeparatorMessage.setVisibility(View.VISIBLE);
            }
            if (socialLayout != null) {
                socialLayout.setVisibility(View.VISIBLE);
            }
        }
        return handled;
    }

    /**
     * Displays a progress bar on top of the action button. This will also
     * enable or disable the action button.
     *
     * @param show whether to show or hide the action bar.
     */
    public void showProgress(boolean show) {
        if (formLayout != null) {
            formLayout.showProgress(show);
        }
        if (modeSelectionView != null) {
            modeSelectionView.setEnabled(!show);
        }
        if (socialLayout != null) {
            socialLayout.setEnabled(!show);
        }
    }

    @Override
    public void onFormModeChanged(FormLayout.FormMode mode) {
        if (formLayout != null) {
            formLayout.changeFormMode(mode);
        }
    }

    @Override
    public void onShowChangePassword() {
        showChangePasswordForm(true);
    }
}
