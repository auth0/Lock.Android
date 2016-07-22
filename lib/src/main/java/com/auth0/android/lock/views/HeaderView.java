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
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.Theme;

public class HeaderView extends RelativeLayout {
    private View header;
    private ImageView logo;
    private TextView text;

    public HeaderView(Context context, Theme lockTheme) {
        super(context);
        init(lockTheme);
    }

    private void init(Theme lockTheme) {
        inflate(getContext(), R.layout.com_auth0_lock_header, this);
        header = findViewById(R.id.com_auth0_lock_header_background);
        logo = (ImageView) findViewById(R.id.com_auth0_lock_header_logo);
        text = (TextView) findViewById(R.id.com_auth0_lock_header_text);
        header.setBackgroundColor(lockTheme.getHeaderColor(getContext()));
        logo.setImageDrawable(lockTheme.getHeaderLogo(getContext()));
        text.setText(lockTheme.getHeaderTitle(getContext()));
        text.setTextColor(lockTheme.getHeaderTitleColor(getContext()));
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

    public void showTitle(boolean show) {
        text.setVisibility(show ? VISIBLE : GONE);
    }

    /**
     * Setter for the Header's logo.
     *
     * @param logo the logo to use
     */
    public void setLogo(@DrawableRes int logo) {
        this.logo.setImageResource(logo);
    }

    public void setPaddingTop(int padding) {
        header.setPadding(0, padding, 0, 0);
    }
}
