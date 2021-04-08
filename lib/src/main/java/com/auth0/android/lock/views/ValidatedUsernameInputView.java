/*
 * ValidatedUserInputView.java
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

import android.annotation.SuppressLint;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.AttributeSet;

import com.auth0.android.lock.R;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.lock.internal.configuration.DatabaseConnection;

import static com.auth0.android.lock.UsernameStyle.DEFAULT;
import static com.auth0.android.lock.UsernameStyle.EMAIL;
import static com.auth0.android.lock.UsernameStyle.USERNAME;
import static com.auth0.android.lock.internal.configuration.DatabaseConnection.MAX_USERNAME_LENGTH;
import static com.auth0.android.lock.internal.configuration.DatabaseConnection.MIN_USERNAME_LENGTH;

public class ValidatedUsernameInputView extends ValidatedInputView {

    private int minUsernameLength = MIN_USERNAME_LENGTH;
    private int maxUsernameLength = MAX_USERNAME_LENGTH;

    private boolean usernameRequired;
    private boolean isCustomDatabase;

    public ValidatedUsernameInputView(@NonNull Context context) {
        super(context);
        init();
    }

    public ValidatedUsernameInputView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ValidatedUsernameInputView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setUsernameStyle(DEFAULT);
    }

    /**
     * Extract the required values from this Database connection to use later in this widget.
     *
     * @param connection used with this widget
     */
    public void configureFrom(@Nullable DatabaseConnection connection) {
        if (connection == null) {
            return;
        }
        minUsernameLength = connection.getMinUsernameLength();
        maxUsernameLength = connection.getMaxUsernameLength();
        usernameRequired = connection.requiresUsername();
        isCustomDatabase = connection.isCustomDatabase();
    }

    /**
     * Choose username or email DataType, according to the Username Style.
     *
     * @param style to use in this username field
     */
    @SuppressLint("StringFormatInvalid")
    public void setUsernameStyle(@UsernameStyle int style) {
        if (style == EMAIL || !usernameRequired) {
            setDataType(DataType.EMAIL);
        } else if (style == USERNAME) {
            setDataType(DataType.USERNAME);
            String errorDescription = isCustomDatabase ? getResources().getString(R.string.com_auth0_lock_input_error_username_empty) :
                    getResources().getString(R.string.com_auth0_lock_input_error_username, minUsernameLength, maxUsernameLength);
            setErrorDescription(errorDescription);
        } else if (style == DEFAULT) {
            setDataType(DataType.USERNAME_OR_EMAIL);
        }
    }

    @Override
    protected boolean validate(boolean validateEmptyFields) {
        final String value = getText().trim();
        if (!validateEmptyFields && value.isEmpty()) {
            return true;
        }
        boolean validUsernameLength = value.length() >= minUsernameLength && value.length() <= maxUsernameLength;

        if (getDataType() == DataType.USERNAME) {
            return validUsernameLength && !isCustomDatabase ? value.matches(USERNAME_REGEX) : validUsernameLength;
        }
        if (getDataType() == DataType.USERNAME_OR_EMAIL) {
            //This case is only used in the LogInFormView, avoid validating against username regex
            final boolean validEmail = value.matches(EMAIL_REGEX);
            return validEmail || validUsernameLength;
        }
        return super.validate(validateEmptyFields);
    }
}
