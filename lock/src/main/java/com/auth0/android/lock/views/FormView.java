/*
 * FormView.java
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
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.auth0.android.lock.Configuration;
import com.squareup.otto.Bus;

public abstract class FormView extends RelativeLayout implements View.OnClickListener {
    private static final String TAG = FormView.class.getSimpleName();
    private Bus bus;

    public FormView(Context context) {
        super(context);
    }

    public FormView(Context context, Bus lockBus) {
        super(context);
        this.bus = lockBus;
    }

    @Nullable
    protected abstract Object getActionEvent();

    protected abstract boolean hasValidData();

    /**
     * Displays a progress bar on top of the action button. This will also
     * enable or disable the action button.
     *
     * @param show whether to show or hide the action bar.
     */
    public abstract void showProgress(boolean show);

    @Override
    public void onClick(View v) {
        if (!hasValidData()) {
            return;
        }
        Object event = getActionEvent();
        if (event != null) {
            bus.post(event);
        } else {
            Log.w(TAG, "The Action Event received from the FormView was null.");
        }
    }

}
