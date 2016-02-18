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
import com.auth0.android.lock.R;
import com.squareup.otto.Bus;

public class PanelHolder extends LinearLayout {

    private final Bus bus;
    private final Configuration configuration;
    private FormLayout databaseLayout;

    public PanelHolder(Context context) {
        super(context);
        bus = null;
        configuration = null;
    }

    public PanelHolder(Context context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        boolean showSocial = !configuration.getSocialStrategies().isEmpty();
        boolean showLoginForm = configuration.getDefaultDatabaseConnection() != null || !configuration.getEnterpriseStrategies().isEmpty();

        SocialView socialLayout = null;
        if (showSocial && showLoginForm) {
            //TODO: merge db and enterprise form, see trello
            socialLayout = new SocialView(getContext(), bus, configuration, SocialView.Mode.List);
            databaseLayout = new FormLayout(getContext(), bus, configuration);
        } else if (showLoginForm) {
            databaseLayout = new FormLayout(getContext(), bus, configuration);
        } else if (showSocial) {
            socialLayout = new SocialView(getContext(), bus, configuration, SocialView.Mode.List);
        }

        if (socialLayout != null) {
            addView(socialLayout, ViewGroup.LayoutParams.MATCH_PARENT, (int) getResources().getDimension(R.dimen.com_auth0_lock_social_container_height));
        }
        if (databaseLayout != null) {
            addView(databaseLayout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public boolean onBackPressed() {
        return databaseLayout != null && databaseLayout.onBackPressed();
    }
}
