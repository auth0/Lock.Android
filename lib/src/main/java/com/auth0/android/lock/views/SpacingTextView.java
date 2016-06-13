/*
 * SpacingTextView.java
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

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.TextView;

public class SpacingTextView extends TextView {

    private int marginTop;
    private int marginBottom;
    private int marginLeft;
    private int marginRight;
    private float lineSpacingAdd;

    public SpacingTextView(Context context) {
        super(context);
    }

    public SpacingTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        parseAttrs(context, attrs);
    }

    public SpacingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        parseAttrs(context, attrs);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SpacingTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        parseAttrs(context, attrs);
    }

    @Override
    public void setLineSpacing(float add, float mult) {
        super.setLineSpacing(add, mult);
        lineSpacingAdd = add;
        requestLayout();
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        params = updateLayoutParams(params);
        super.setLayoutParams(params);
    }

    @Override
    public void setPadding(int left, int top, int right, int bottom) {
        super.setPadding(left, top, right, bottom + calculateOffset());
    }

    @SuppressWarnings("ResourceType")
    private void parseAttrs(Context context, AttributeSet attrs) {
        int[] attributes = new int[]{android.R.attr.layout_margin, android.R.attr.layout_marginTop,
                android.R.attr.layout_marginBottom, android.R.attr.layout_marginLeft,
                android.R.attr.layout_marginRight, android.R.attr.lineSpacingExtra};

        TypedArray arr = context.obtainStyledAttributes(attrs, attributes);

        int allMargins = arr.getDimensionPixelOffset(0, 0);
        if (allMargins != 0) {
            marginTop = allMargins;
            marginBottom = allMargins;
            marginLeft = allMargins;
            marginRight = allMargins;
        }
        marginTop = arr.getDimensionPixelOffset(1, 0);
        marginBottom = arr.getDimensionPixelOffset(2, 0);
        marginLeft = arr.getDimensionPixelOffset(3, 0);
        marginRight = arr.getDimensionPixelOffset(4, 0);
        lineSpacingAdd = arr.getDimensionPixelOffset(5, 1);
        arr.recycle();
    }

    private void parseMargins(ViewGroup.LayoutParams params) {
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
        marginTop = marginParams.topMargin;
        marginBottom = marginParams.bottomMargin;
        marginLeft = marginParams.leftMargin;
        marginRight = marginParams.rightMargin;
    }

    private ViewGroup.LayoutParams updateLayoutParams(ViewGroup.LayoutParams params) {
        parseMargins(params);
        ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams) params;
        marginParams.setMargins(marginLeft, marginTop, marginRight, marginBottom + calculateOffset());
        return marginParams;
    }

    private int calculateOffset() {
        if (lineSpacingAdd <= 1 || Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
            return 0;
        }
        return (int) (lineSpacingAdd / -2);
    }
}
