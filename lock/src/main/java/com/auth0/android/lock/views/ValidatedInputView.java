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
import android.support.design.widget.TextInputLayout;
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

    private ImageView icon;
    private EditText input;
    private int inputIcon;
    private int inputErrorIcon;

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
        inflate(getContext(), R.layout.com_auth0_lock_validated_input_view, this);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        input = (EditText) findViewById(R.id.com_auth0_lock_input);
        TextInputLayout inputLayout = (TextInputLayout) input.getParent();

        if (attrs == null || isInEditMode()) {
            return;
        }

        input.setOnFocusChangeListener(this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock_ValidatedInput);
        dataType = DataType.values()[a.getInt(R.styleable.Lock_ValidatedInput_Auth0_InputDataType, 0)];

        String hint;

        switch (dataType) {
            case email:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputIcon = R.drawable.com_auth0_lock_ic_input_email;
                inputErrorIcon = R.drawable.com_auth0_lock_ic_input_email_error;
                hint = getResources().getString(R.string.com_auth0_lock_hint_email);
                break;
            case password:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                inputIcon = R.drawable.com_auth0_lock_ic_input_password;
                inputErrorIcon = R.drawable.com_auth0_lock_ic_input_password_error;
                hint = getResources().getString(R.string.com_auth0_lock_hint_password);
                break;
            case username:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                inputIcon = R.drawable.com_auth0_lock_ic_input_username;
                inputErrorIcon = R.drawable.com_auth0_lock_ic_input_username_error;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username);
                break;
            default:
            case none:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                inputIcon = R.drawable.com_auth0_lock_social_icon_auth0;
                inputErrorIcon = R.drawable.com_auth0_lock_social_icon_auth0;
                hint = "";
                break;
        }
        a.recycle();

        inputLayout.setHint(hint);
    }

    /**
     * Validates the input data and updates the icon. DataType must be set.
     *
     * @return whether the data is valid or not.
     */
    public boolean validate() {
        //also called on EditText focus change
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

        icon.setImageResource(valid ? inputIcon : inputErrorIcon);
        return valid;
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
