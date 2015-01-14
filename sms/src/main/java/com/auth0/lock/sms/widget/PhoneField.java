/*
 * PhoneField.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.lock.sms.widget;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.auth0.lock.sms.R;
import com.auth0.lock.validation.ValidationEnabled;

/**
 * Created by hernan on 1/13/15.
 */
public class PhoneField extends LinearLayout implements ValidationEnabled {

    private int iconResource;
    private int errorIconResource;
    private int errorColorResource;
    private ColorStateList colorResource;
    private ColorStateList hintColorResource;
    private int codeColorResource;

    private ImageView iconView;
    private Button codeButton;
    private EditText phoneEditText;

    public PhoneField(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, android.R.attr.textViewStyle);
    }

    public PhoneField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.view_phone_fied, this, true);
        iconView = (ImageView) getChildAt(0);
        codeButton = (Button) getChildAt(1);
        phoneEditText = (EditText) getChildAt(2);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.PhoneField, defStyleAttr, 0);
        try {
            iconResource = a.getResourceId(R.styleable.PhoneField_normalPhoneIconDrawable, -1);
            errorIconResource = a.getResourceId(R.styleable.PhoneField_errorPhoneIconDrawable, -1);
            errorColorResource = a.getResourceId(R.styleable.PhoneField_errorPhoneColor, R.color.credential_field_error);
            colorResource = phoneEditText.getTextColors();
            hintColorResource = phoneEditText.getHintTextColors();
            codeColorResource = codeButton.getTextColors().getDefaultColor();
            iconView.setImageResource(iconResource);
        } finally {
            a.recycle();
        }
    }

    @Override
    public void markAsInvalid(boolean invalid) {
        if (invalid) {
            phoneEditText.setTextColor(getResources().getColor(errorColorResource));
            phoneEditText.setHintTextColor(getResources().getColor(errorColorResource));
            iconView.setImageResource(errorIconResource);
            codeButton.setTextColor(getResources().getColor(errorColorResource));
        } else {
            final Editable text = phoneEditText.getText();
            phoneEditText.setText(null);
            phoneEditText.setTextColor(colorResource);
            phoneEditText.setHintTextColor(hintColorResource);
            phoneEditText.setText(text);
            phoneEditText.setSelection(text.length());
            iconView.setImageResource(iconResource);
            codeButton.setTextColor(codeColorResource);
        }
    }

    @Override
    public Editable getText() {
        return phoneEditText.getText();
    }

    public void setOnClickListener(OnClickListener listener) {
        codeButton.setOnClickListener(listener);
    }

    public String getCompletePhoneNumber() {
        final String number = codeButton.getText().toString() + phoneEditText.getText().toString();
        return number.replace(" ", "");
    }

    public String getPhoneNumber() {
        return phoneEditText.getText().toString();
    }

    public String getDialCode() {
        return codeButton.getText().toString();
    }

    public void setDialCode(String dialCode) {
        codeButton.setText(dialCode);
    }
}
