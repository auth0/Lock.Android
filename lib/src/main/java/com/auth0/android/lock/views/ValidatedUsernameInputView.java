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

import android.content.Context;
import android.util.AttributeSet;

import com.auth0.android.lock.internal.Configuration;
import com.auth0.android.lock.internal.json.DatabaseConnection;

import static com.auth0.android.lock.UsernameStyle.DEFAULT;
import static com.auth0.android.lock.UsernameStyle.EMAIL;
import static com.auth0.android.lock.UsernameStyle.USERNAME;

public class ValidatedUsernameInputView extends ValidatedInputView {

    private int minUsernameLength;
    private int maxUsernameLength;

    public ValidatedUsernameInputView(Context context) {
        super(context);
    }

    public ValidatedUsernameInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ValidatedUsernameInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Choose username or email DataType, according to the Configuration
     *
     * @param configuration of the instance
     */
    public void chooseDataType(Configuration configuration) {
        @ValidatedInputView.DataType
        int type = 0;
        switch (configuration.getUsernameStyle()) {
            case EMAIL:
                type = DataType.EMAIL;
                break;
            case USERNAME:
                type = configuration.isUsernameRequired() ? DataType.USERNAME : DataType.EMAIL;
                break;
            case DEFAULT:
                type = configuration.isUsernameRequired() ? DataType.USERNAME_OR_EMAIL : DataType.EMAIL;
                break;
        }
        final DatabaseConnection dbConnection = configuration.getDatabaseConnection();
        if (dbConnection != null) {
            minUsernameLength = dbConnection.getMinUsernameLength();
            maxUsernameLength = dbConnection.getMaxUsernameLength();
        }
        setDataType(type);
    }

    @Override
    protected boolean validate(boolean validateEmptyFields) {
        final String value = getText().trim();
        if (!validateEmptyFields && value.isEmpty()) {
            return true;
        }
        final boolean validUsername = value.matches(USERNAME_REGEX) && value.length() >= minUsernameLength && value.length() <= maxUsernameLength;
        if (getDataType() == DataType.USERNAME) {
            return validUsername;
        }
        if (getDataType() == DataType.USERNAME_OR_EMAIL) {
            final boolean validEmail = value.matches(EMAIL_REGEX);
            return validEmail || validUsername;
        }
        return super.validate(validateEmptyFields);
    }
}
