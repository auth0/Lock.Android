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
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.auth0.android.lock.R;

public class ValidatedInputView extends RelativeLayout implements View.OnFocusChangeListener {

    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_USERNAME_LENGTH = 6;

    private ImageView icon;
    private EditText input;
    private int inputIcon;
    private int inputErrorIcon;

    enum DataType {USERNAME, EMAIL, USERNAME_OR_EMAIL, PASSWORD}

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
        if (attrs == null || isInEditMode()) {
            return;
        }

        input.setOnFocusChangeListener(this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock_ValidatedInput);
        dataType = DataType.values()[a.getInt(R.styleable.Lock_ValidatedInput_Auth0_InputDataType, 0)];

        setupInputValidation();
        a.recycle();
    }

    private void setupInputValidation() {
        String hint;
        input.setTransformationMethod(null);
        switch (dataType) {
            case EMAIL:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputIcon = R.drawable.com_auth0_lock_ic_input_email;
                inputErrorIcon = R.drawable.com_auth0_lock_ic_input_email_error;
                hint = getResources().getString(R.string.com_auth0_lock_hint_email);
                break;
            case PASSWORD:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                inputIcon = R.drawable.com_auth0_lock_ic_input_password;
                inputErrorIcon = R.drawable.com_auth0_lock_ic_input_password_error;
                hint = getResources().getString(R.string.com_auth0_lock_hint_password);
                break;
            case USERNAME_OR_EMAIL:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                inputIcon = R.drawable.com_auth0_lock_ic_input_username;
                inputErrorIcon = R.drawable.com_auth0_lock_ic_input_username_error;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username_or_email);
                break;
            case USERNAME:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                inputIcon = R.drawable.com_auth0_lock_ic_input_username;
                inputErrorIcon = R.drawable.com_auth0_lock_ic_input_username_error;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username);
                break;
            default:
                input.setInputType(InputType.TYPE_TEXT_VARIATION_NORMAL);
                inputIcon = R.drawable.com_auth0_lock_social_icon_auth0;
                inputErrorIcon = R.drawable.com_auth0_lock_social_icon_auth0;
                hint = "";
                break;
        }
        TextInputLayout inputLayout = (TextInputLayout) input.getParent();
        inputLayout.setHint(hint);
        icon.setImageResource(inputIcon);
    }

    /**
     * Changes the type of input this view will validate.
     *
     * @param type a valid DataType
     */
    public void setDataType(DataType type) {
        dataType = type;
        setupInputValidation();
    }

    /**
     * Validates the input data and updates the icon. DataType must be set.
     *
     * @return whether the data is valid or not.
     */
    public boolean validate() {
        //also called on EditText focus change
        String value = getText();
        boolean valid;
        int errMsg = 0;
        switch (dataType) {
            case EMAIL:
                valid = !value.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(value).matches();
                errMsg = R.string.com_auth0_lock_input_error_email;
                break;
            case PASSWORD:
                valid = !value.isEmpty() && value.length() >= MIN_PASSWORD_LENGTH;
                errMsg = R.string.com_auth0_lock_input_error_password;
                break;
            case USERNAME:
                String withoutSpaces = value.replace(" ", "");
                valid = !withoutSpaces.isEmpty() && withoutSpaces.length() >= MIN_USERNAME_LENGTH;
                errMsg = R.string.com_auth0_lock_input_error_username;
                break;
            default:
            case USERNAME_OR_EMAIL:
                valid = !value.isEmpty() && (Patterns.EMAIL_ADDRESS.matcher(value).matches() || value.length() >= MIN_USERNAME_LENGTH);
                errMsg = R.string.com_auth0_lock_input_error_username_email;
                break;
        }

        TextInputLayout inputLayout = (TextInputLayout) input.getParent();
        inputLayout.setError(valid ? null : getResources().getString(errMsg));
        icon.setImageResource(valid ? inputIcon : inputErrorIcon);
        return valid;
    }

    public String getText() {
        return input.getText().toString().trim();
    }

    public void clearInput() {
        input.setText("");
        input.setError(null);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            validate();
        }
    }
}
