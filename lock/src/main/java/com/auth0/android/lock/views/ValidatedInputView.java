/*
 * EmailAndPasswordView.java
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
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.auth0.android.lock.R;

public class ValidatedInputView extends RelativeLayout implements View.OnFocusChangeListener {

    private static final int MIN_PASSWORD_LENGTH = 10;
    private static final int MIN_USERNAME_LENGTH = 8;

    private Drawable inputIcon;
    private Drawable inputErrorIcon;
    private ImageView icon;
    private EditText input;

    private enum DataType {none, email, username, password}

    private DataType dataType;


    public ValidatedInputView(Context context) {
        super(context);
        init(null);
    }

    public ValidatedInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ValidatedInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.com_auth0_lock_validated_input_view, null);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        input = (EditText) findViewById(R.id.com_auth0_lock_input);

        input.setOnFocusChangeListener(this);
        if (attrs == null) {
            return;
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock_Theme);
        String hint = a.getString(R.styleable.Lock_Theme_Auth0_InputHint);
        if (hint != null) {
            input.setHint(hint);
        }
        inputIcon = a.getDrawable(R.styleable.Lock_Theme_Auth0_InputIcon);
        if (inputIcon == null) {
            inputIcon = getResources().getDrawable(R.drawable.com_auth0_lock_ic_username);
        }
        icon.setImageDrawable(inputIcon);
        inputErrorIcon = a.getDrawable(R.styleable.Lock_Theme_Auth0_InputIcon);
        if (inputErrorIcon == null) {
            inputErrorIcon = getResources().getDrawable(R.drawable.com_auth0_lock_ic_username_error);
        }
        dataType = DataType.values()[a.getInt(R.styleable.Lock_Theme_Auth0_InputDataType, 0)];
        switch (dataType) {
            case email:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case password:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                break;
            default:
            case none:
            case username:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                break;
        }
        a.recycle();
    }

    private void validate() {
        //call on EditText focus change
        boolean valid = true;
        String value = getText();
        switch (dataType) {
            case email:
                valid = !value.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(value).matches();
                break;
            case password:
                valid = !value.isEmpty() && value.length() >= MIN_PASSWORD_LENGTH;
                break;
            case username:
                valid = !value.isEmpty() && value.length() >= MIN_USERNAME_LENGTH;
                break;
            default:
            case none:
                break;
        }

        icon.setImageDrawable(valid ? inputIcon : inputErrorIcon);
    }

    public String getText() {
        return input.getText().toString().trim();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            validate();
        }
    }
}
