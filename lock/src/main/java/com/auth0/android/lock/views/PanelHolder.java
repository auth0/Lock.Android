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
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.LockActivity;
import com.auth0.android.lock.PasswordlessLockActivity;
import com.auth0.android.lock.R;
import com.squareup.otto.Bus;

public class PanelHolder extends LinearLayout {

    private final Bus bus;
    private final Configuration configuration;
    private FormLayout formLayout;
    private PasswordlessFormView passwordlessLayout;
    private boolean usingPasswordless;

    public PanelHolder(Context context) {
        super(context);
        bus = null;
        configuration = null;
    }

    public PanelHolder(PasswordlessLockActivity context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        this.usingPasswordless = true;
        init();
    }

    public PanelHolder(LockActivity context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        this.usingPasswordless = false;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        boolean showSocial = !configuration.getSocialStrategies().isEmpty();
        boolean showLoginForm = configuration.getDefaultDatabaseConnection() != null || !configuration.getEnterpriseStrategies().isEmpty();
        boolean showPasswordless = configuration.getPasswordlessStrategy() != null;

        SocialView socialLayout = null;
        if (showSocial && showLoginForm && !usingPasswordless) {
            socialLayout = new SocialView(getContext(), bus, configuration, SocialView.Mode.List);
            formLayout = new FormLayout(getContext(), bus, configuration);
        } else if (showLoginForm && !usingPasswordless) {
            formLayout = new FormLayout(getContext(), bus, configuration);
        } else if (showSocial && showPasswordless && usingPasswordless) {
            socialLayout = new SocialView(getContext(), bus, configuration, SocialView.Mode.List);
            passwordlessLayout = new PasswordlessFormView(getContext(), bus, configuration.getPasswordlessMode());
        } else if (showPasswordless && usingPasswordless) {
            passwordlessLayout = new PasswordlessFormView(getContext(), bus, configuration.getPasswordlessMode());
        } else if (showSocial) {
            socialLayout = new SocialView(getContext(), bus, configuration, SocialView.Mode.List);
        }

        if (socialLayout != null) {
            addView(socialLayout, ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.com_auth0_lock_social_container_height));
        }
        if (formLayout != null) {
            addView(formLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        if (passwordlessLayout != null) {
            addView(passwordlessLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public boolean onBackPressed() {
        if (usingPasswordless) {
            return passwordlessLayout != null && passwordlessLayout.onBackPressed();
        } else {
            return formLayout != null && formLayout.onBackPressed();
        }
    }
}
