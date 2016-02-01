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
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

public class HeaderView extends RelativeLayout {
    private View header;
    private ImageView logo;
    private TextView text;

    public HeaderView(Context context) {
        super(context);
        init(null);
    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.com_auth0_lock_header, this);
        header = findViewById(R.id.com_auth0_lock_header_background);
        logo = (ImageView) findViewById(R.id.com_auth0_lock_header_logo);
        text = (TextView) findViewById(R.id.com_auth0_lock_header_text);
        if (attrs == null) {
            return;
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock_Theme);
        String headerTitle = a.getString(R.styleable.Lock_Theme_Auth0_HeaderText);
        //TODO: Add default logo
        Drawable headerLogo = a.getDrawable(R.styleable.Lock_Theme_Auth0_HeaderLogo);
        @SuppressWarnings("deprecation")
        int defHeaderColor = getResources().getColor(R.color.com_auth0_lock_header_background);
        int headerColor = a.getColor(R.styleable.Lock_Theme_Auth0_HeaderBackground, defHeaderColor);
        if (headerTitle == null) {
            headerTitle = getResources().getString(R.string.com_auth0_lock_header_title);
        }
        text.setText(headerTitle);
        header.setBackgroundColor(headerColor);
        if (Build.VERSION.SDK_INT >= 16) {
            header.setBackground(headerLogo);
        } else {
            header.setBackgroundDrawable(headerLogo);
        }
        a.recycle();
    }

    @SuppressWarnings("deprecation")
    public void setColor(@ColorRes int color) {
        this.header.setBackgroundColor(getResources().getColor(color));
    }

    public void setTitle(@StringRes int title) {
        this.text.setText(getResources().getString(title));
    }

    public void setLogo(@DrawableRes int logo) {
        this.logo.setImageResource(logo);
    }
}
