/*
 * CheckableOptionView.java
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
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

public class CheckableOptionView extends LinearLayout {

    private ImageView icon;
    private TextView description;

    private boolean mandatory;
    private boolean checked;

    public CheckableOptionView(Context context) {
        super(context);
        init(null);
    }

    public CheckableOptionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public CheckableOptionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        final View v = inflate(getContext(), R.layout.com_auth0_lock_checkable_option, this);
        icon = v.findViewById(R.id.com_auth0_lock_checkable_text_icon);
        description = v.findViewById(R.id.com_auth0_lock_checkable_text_description);

        if (attrs == null) {
            return;
        }

        int[] set = {android.R.attr.text};
        TypedArray a = getContext().obtainStyledAttributes(attrs, set);
        CharSequence text = a.getText(0);
        a.recycle();
        description.setText(text);
        updateStatus();
    }

    private void updateStatus() {
        if (checked) {
            icon.setImageResource(R.drawable.com_auth0_lock_ic_check_success);
            description.setTextColor(ContextCompat.getColor(getContext(), R.color.com_auth0_lock_checkable_option_success));
            return;
        }
        icon.setImageResource(mandatory ? R.drawable.com_auth0_lock_ic_check_error : R.drawable.com_auth0_lock_ic_check_unset);
        description.setTextColor(ContextCompat.getColor(getContext(), mandatory ? R.color.com_auth0_lock_checkable_option_error : R.color.com_auth0_lock_normal_text));
    }

    /**
     * Sets the current text/description for this Option.
     *
     * @param text to set.
     */
    public void setText(String text) {
        this.description.setText(text);
    }

    /**
     * Updates the current checked state of this Option widget.
     *
     * @param checked whether to check or uncheck the Option.
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
        updateStatus();
    }

    /**
     * If this Option is a requirement that must be met, set it to mandatory.
     * Used for displaying a different image on the side when unchecked.
     *
     * @param mandatory whether this Option is required or not.
     */
    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
        updateStatus();
    }
}
