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
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.auth0.android.lock.R;

public class ActionButton extends FrameLayout {

    private static final String TAG = ActionButton.class.getSimpleName();
    private ProgressBar progress;
    private ImageView icon;

    public ActionButton(Context context) {
        super(context);
        init();
    }

    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_action_button, this);
        progress = (ProgressBar) findViewById(R.id.com_auth0_lock_progress);
        progress.setVisibility(View.GONE);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);

        ViewUtils.setBackground(icon, generateStateBackground());
    }

    private Drawable generateStateBackground() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getContext().getTheme();
        theme.resolveAttribute(R.attr.Auth0_PrimaryColor, typedValue, true);
        int normalColor = typedValue.data;
        theme.resolveAttribute(R.attr.Auth0_DarkPrimaryColor, typedValue, true);
        int pressedColor = typedValue.data;
        int disabledColor = ContextCompat.getColor(getContext(), R.color.com_auth0_lock_submit_disabled);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(pressedColor), new ColorDrawable(normalColor), null);
        } else {
            StateListDrawable states = new StateListDrawable();
            states.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
            states.addState(new int[]{android.R.attr.state_enabled}, new ColorDrawable(normalColor));
            states.addState(new int[]{}, new ColorDrawable(disabledColor));
            return states;
        }
    }

    /**
     * Used to display a progress bar and disable the button.
     *
     * @param show whether to show the progress bar or not.
     */
    public void showProgress(boolean show) {
        if (show) {
            Log.v(TAG, "Disabling the button while showing progress");
        } else {
            Log.v(TAG, "Enabling the button and hiding progress");
        }
        setEnabled(!show);
        progress.setVisibility(show ? VISIBLE : GONE);
        icon.setVisibility(show ? INVISIBLE : VISIBLE);
    }

}
