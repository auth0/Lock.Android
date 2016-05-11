/*
 * HeaderView.java
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
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.views.HeaderView.HeaderSize.NORMAL;
import static com.auth0.android.lock.views.HeaderView.HeaderSize.SMALL;

public class HeaderView extends RelativeLayout {
    private View header;
    private ImageView logo;
    private TextView text;

    @HeaderSize
    private int currentSize = NORMAL;
    private boolean keyboardIsOpen;

    @IntDef({NORMAL, SMALL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface HeaderSize {
        int NORMAL = 0;
        int SMALL = 1;
    }

    public HeaderView(Context context) {
        super(context);
        init();
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_header, this);
        header = findViewById(R.id.com_auth0_lock_header_background);
        logo = (ImageView) findViewById(R.id.com_auth0_lock_header_logo);
        text = (TextView) findViewById(R.id.com_auth0_lock_header_text);
    }

    /**
     * Setter for the Header's background color.
     *
     * @param color the color to use
     */
    public void setColor(@ColorRes int color) {
        this.header.setBackgroundColor(ContextCompat.getColor(getContext(), color));
    }

    /**
     * Setter for the Header's title.
     *
     * @param title the title to use
     */
    public void setTitle(@StringRes int title) {
        this.text.setText(getResources().getString(title));
    }

    /**
     * Changes the header height. If the keyboard is currently open,
     * the change wont be visible until its closed.
     *
     * @param size the new size.
     */
    public void changeHeaderSize(@HeaderSize int size) {
        this.currentSize = size;
        updateHeaderHeight();
    }

    private void updateHeaderHeight() {
        text.setVisibility(currentSize == NORMAL ? VISIBLE : GONE);
        int headerHeightSmall = (int) getResources().getDimension(R.dimen.com_auth0_lock_header_height_small);
        int headerHeightNormal = ViewGroup.LayoutParams.WRAP_CONTENT;
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, currentSize == SMALL ? headerHeightSmall : headerHeightNormal);
        setLayoutParams(params);
        setVisibility(keyboardIsOpen ? GONE : VISIBLE);
    }

    /**
     * Setter for the Header's logo.
     *
     * @param logo the logo to use
     */
    public void setLogo(@DrawableRes int logo) {
        this.logo.setImageResource(logo);
    }

    public void onKeyboardStateChanged(boolean isOpen) {
        keyboardIsOpen = isOpen;
        updateHeaderHeight();
    }

    /**
     * Just sets the top padding to the given one.
     *
     * @param paddingTop to set to this view.
     */
    public void setPaddingTop(int paddingTop) {
        this.header.setPadding(0, paddingTop, 0, 0);
    }
}
