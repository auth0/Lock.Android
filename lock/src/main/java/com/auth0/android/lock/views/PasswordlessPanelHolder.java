/*
 * PasswordlessPanelHolder.java
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
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.views.interfaces.LockWidgetSocial;
import com.squareup.otto.Bus;

public class PasswordlessPanelHolder extends RelativeLayout implements LockWidgetSocial, View.OnClickListener {

    private final Bus bus;
    private final Configuration configuration;
    private PasswordlessFormLayout formLayout;
    private ActionButton actionButton;

    public PasswordlessPanelHolder(Context context) {
        super(context);
        bus = null;
        configuration = null;
    }

    public PasswordlessPanelHolder(Context context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        init();
    }

    private void init() {
        formLayout = new PasswordlessFormLayout(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_IN_PARENT, TRUE);
        addView(formLayout, params);

        RelativeLayout.LayoutParams actionParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        actionParams.addRule(ALIGN_PARENT_BOTTOM, TRUE);
        actionButton = new ActionButton(getContext());
        actionButton.setOnClickListener(this);
        addView(actionButton, actionParams);
    }

    public boolean onBackPressed() {
        return formLayout.onBackPressed();
    }

    public void showProgress(boolean show) {
        actionButton.showProgress(show);
    }

    public void codeSent() {
        formLayout.codeSent();
    }

    @Override
    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void onSocialLogin(SocialConnectionEvent event) {
        bus.post(event);
    }

    @Override
    public void onClick(View v) {
        Object event = formLayout.onActionPressed();
        if (event != null) {
            bus.post(event);
            actionButton.showProgress(true);
        }
    }
}
