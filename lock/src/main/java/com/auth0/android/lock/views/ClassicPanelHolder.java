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
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.views.interfaces.LockWidget;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;
import com.squareup.otto.Bus;

public class ClassicPanelHolder extends RelativeLayout implements ModeSelectionView.FormModeChangedListener, LockWidgetForm, View.OnClickListener, LockWidget {

    private final Bus bus;
    private final Configuration configuration;
    private FormLayout formLayout;
    private ModeSelectionView modeSelectionView;
    private ChangePasswordFormView changePwdForm;
    private ActionButton actionButton;

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
        if (configuration.getDefaultDatabaseConnection() != null && configuration.isSignUpEnabled()) {
            RelativeLayout.LayoutParams swicherParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            swicherParams.addRule(ALIGN_PARENT_TOP, TRUE);
            modeSelectionView = new ModeSelectionView(getContext(), this);
            addView(modeSelectionView, swicherParams);
        }

        formLayout = new FormLayout(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT, TRUE);
        addView(formLayout, params);

        RelativeLayout.LayoutParams actionParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        actionButton = new ActionButton(getContext());
        actionButton.setOnClickListener(this);
        addView(actionButton, actionParams);
    }

    private void showChangePasswordForm(boolean show) {
        if (formLayout != null) {
            formLayout.setVisibility(show ? GONE : VISIBLE);
        }
        if (modeSelectionView != null) {
            modeSelectionView.setVisibility(show ? GONE : VISIBLE);
        }

        if (changePwdForm == null && show) {
            changePwdForm = new ChangePasswordFormView(this);
            LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(CENTER_IN_PARENT, TRUE);
            addView(changePwdForm, params);
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
        if (actionButton != null) {
            actionButton.showProgress(show);
        }
        if (modeSelectionView != null) {
            modeSelectionView.setEnabled(!show);
        }
    }

    @Override
    public void onFormModeChanged(FormLayout.FormMode mode) {
        formLayout.changeFormMode(mode);
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onClick(View v) {
        Object event;
        if (changePwdForm != null) {
            event = changePwdForm.submitForm();
        } else {
            event = formLayout.onActionPressed();
        }
        if (event != null) {
            bus.post(event);
        }
    }

    @Override
    public void showChangePasswordForm() {
        showChangePasswordForm(true);
    }

    @Override
    public void onSocialLogin(SocialConnectionEvent event) {
        bus.post(event);
    }
}
