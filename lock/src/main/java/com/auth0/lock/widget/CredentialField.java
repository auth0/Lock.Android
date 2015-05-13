/*
 * CredentialField.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.lock.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.util.AttributeSet;
import android.widget.EditText;

import com.auth0.lock.R;
import com.auth0.lock.validation.ValidationEnabled;

public class CredentialField extends EditText implements ValidationEnabled {

    private int iconResource;
    private int errorIconResource;
    private final int colorResource;
    private final int hintColorResource;
    private final int errorColorResource;
    private boolean invalid;

    public CredentialField(Context context) {
        this(context, null);
    }

    public CredentialField(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.editTextStyle);
    }

    public CredentialField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CredentialField, defStyleAttr, 0);
        try {
            iconResource = a.getResourceId(R.styleable.CredentialField_com_auth0_normalIconDrawable, -1);
            errorIconResource = a.getResourceId(R.styleable.CredentialField_com_auth0_errorIconDrawable, -1);
            errorColorResource = a.getResourceId(R.styleable.CredentialField_com_auth0_errorColor, R.color.com_auth0_credential_field_error);
            colorResource = getTextColors().getDefaultColor();
            hintColorResource = getHintTextColors().getDefaultColor();
            setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
        } finally {
            a.recycle();
        }
    }

    public void markAsInvalid(boolean invalid) {
        this.invalid = invalid;
        if (invalid) {
            setCompoundDrawablesWithIntrinsicBounds(errorIconResource, 0, 0, 0);
            setTextColor(getResources().getColor(errorColorResource));
            setHintTextColor(getResources().getColor(errorColorResource));
        } else {
            setCompoundDrawablesWithIntrinsicBounds(iconResource, 0, 0, 0);
            setTextColor(colorResource);
            setHintTextColor(hintColorResource);
        }
    }

    public void setErrorIconResource(int errorIconResource) {
        this.errorIconResource = errorIconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }

    public void refresh() {
        markAsInvalid(this.invalid);
    }

    @Override
    public String getInputText() {
        final Editable text = getText();
        return text != null ? text.toString() : null;
    }
}
