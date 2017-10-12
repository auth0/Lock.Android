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
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.RippleDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.Theme;

public class ActionButton extends FrameLayout {

    private static final String TAG = ActionButton.class.getSimpleName();
    private ProgressBar progress;
    private ImageView icon;
    private LinearLayout labeledLayout;
    private TextView title;
    private boolean shouldShowLabel;

    public ActionButton(Context context, Theme lockTheme) {
        super(context);
        init(lockTheme);
    }

    private void init(Theme lockTheme) {
        inflate(getContext(), R.layout.com_auth0_lock_action_button, this);
        progress = (ProgressBar) findViewById(R.id.com_auth0_lock_progress);
        progress.setVisibility(View.GONE);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        labeledLayout = (LinearLayout) findViewById(R.id.com_auth0_lock_labeled);
        title = (TextView) findViewById(R.id.com_auth0_lock_title);

        ViewUtils.setBackground(icon, generateStateBackground(lockTheme));
        ViewUtils.setBackground(labeledLayout, generateStateBackground(lockTheme));
        showLabel(false);
        icon.setFocusable(true);
        icon.setFocusableInTouchMode(false);
        labeledLayout.setFocusable(true);
        labeledLayout.setFocusableInTouchMode(false);
    }

    private Drawable generateStateBackground(Theme lockTheme) {
        int normalColor = lockTheme.getPrimaryColor(getContext());
        int pressedColor = lockTheme.getDarkPrimaryColor(getContext());
        //164 -> 64% alpha
        int focusedColor = ColorUtils.setAlphaComponent(normalColor, 164);
        int disabledColor = ContextCompat.getColor(getContext(), R.color.com_auth0_lock_submit_disabled);

        final StateListDrawable buttonDrawable = new StateListDrawable();
        buttonDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_pressed}, new ColorDrawable(pressedColor));
        buttonDrawable.addState(new int[]{android.R.attr.state_enabled, android.R.attr.state_focused}, new ColorDrawable(focusedColor));
        buttonDrawable.addState(new int[]{android.R.attr.state_enabled}, new ColorDrawable(normalColor));
        buttonDrawable.addState(new int[]{}, new ColorDrawable(disabledColor));

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            return new RippleDrawable(ColorStateList.valueOf(pressedColor), buttonDrawable, null);
        }
        return buttonDrawable;
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        icon.setOnClickListener(l);
        labeledLayout.setOnClickListener(l);
    }

    /**
     * Used to display a progress bar and disable the button.
     *
     * @param show whether to show the progress bar or not.
     */
    public void showProgress(boolean show) {
        Log.v(TAG, show ? "Disabling the button while showing progress" : "Enabling the button and hiding progress");
        setEnabled(!show);
        progress.setVisibility(show ? VISIBLE : GONE);
        if (show) {
            icon.setVisibility(INVISIBLE);
            labeledLayout.setVisibility(INVISIBLE);
            return;
        }
        icon.setVisibility(shouldShowLabel ? GONE : VISIBLE);
        labeledLayout.setVisibility(!shouldShowLabel ? GONE : VISIBLE);
    }

    /**
     * Label to display in the button.
     *
     * @param stringRes the new resource to display as the button label.
     */
    public void setLabel(@StringRes int stringRes) {
        title.setText(stringRes);
    }

    /**
     * Whether to show an icon or a label with the current selected mode.
     * By default, it will show the icon.
     *
     * @param showLabel whether to show an icon or a label.
     */
    public void showLabel(boolean showLabel) {
        shouldShowLabel = showLabel;
        labeledLayout.setVisibility(showLabel ? VISIBLE : GONE);
        icon.setVisibility(!showLabel ? VISIBLE : GONE);
    }
}
