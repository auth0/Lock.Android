/*
 * EnterpriseLayout.java
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
import android.support.annotation.IdRes;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.auth0.android.lock.Configuration;
import com.squareup.otto.Bus;

public class EnterpriseLayout extends RelativeLayout {
    private Bus bus;
    private Configuration configuration;

    private static final
    @IdRes
    int FORM_ID = 8;

    public EnterpriseLayout(Context context) {
        super(context);
    }

    public EnterpriseLayout(Context context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        init();
    }

    private void init() {
        showDomainForm();
    }

    private void showDomainForm() {
        this.removeView(findViewById(FORM_ID));

        DomainFormView domainForm = new DomainFormView(getContext(), this.bus, this.configuration);
        domainForm.setId(FORM_ID);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(ALIGN_PARENT_TOP);
        this.addView(domainForm, 0, params);
    }

}
