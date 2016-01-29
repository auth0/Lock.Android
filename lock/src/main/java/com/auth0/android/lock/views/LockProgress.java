/*
 * LockProgress.java
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
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LockProgress extends RelativeLayout {

    private ProgressBar progress;
    private TextView message;

    public LockProgress(Context context) {
        super(context);
        init();
    }

    public LockProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LockProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        progress = new ProgressBar(getContext());
        message = new TextView(getContext());
        progress.setIndeterminate(true);
        progress.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        addView(progress, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutParams messageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.addRule(BELOW, progress.getId());
        addView(message, messageParams);
        if (isInEditMode()) {
            return;
        }

    }

    public void showResult(@Nullable String error) {
        if (error == null || error.isEmpty()) {
            message.setText("");
            message.setVisibility(View.GONE);
        } else {
            message.setText(error);
            message.setVisibility(View.VISIBLE);
        }
        progress.setVisibility(View.GONE);
    }
}
