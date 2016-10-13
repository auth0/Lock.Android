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

import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.DatabaseConnection;

import static com.auth0.android.lock.UsernameStyle.DEFAULT;
import static com.auth0.android.lock.UsernameStyle.EMAIL;
import static com.auth0.android.lock.UsernameStyle.USERNAME;
import static com.auth0.android.lock.internal.configuration.DatabaseConnection.MAX_USERNAME_LENGTH;
import static com.auth0.android.lock.internal.configuration.DatabaseConnection.MIN_USERNAME_LENGTH;
import static com.auth0.android.lock.internal.configuration.DatabaseConnection.UNUSED_USERNAME_LENGTH;

public class ValidatedUsernameInputView extends ValidatedInputView {

    private int minUsernameLength = MIN_USERNAME_LENGTH;
    private int maxUsernameLength = MAX_USERNAME_LENGTH;

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
        final DatabaseConnection dbConnection = configuration.getDatabaseConnection();
        if (dbConnection != null) {
            minUsernameLength = dbConnection.getMinUsernameLength();
            maxUsernameLength = dbConnection.getMaxUsernameLength();
        }
        if (configuration.getUsernameStyle() == EMAIL || !configuration.isUsernameRequired()) {
            setDataType(DataType.EMAIL);
        } else if (configuration.getUsernameStyle() == USERNAME) {
            setDataType(DataType.USERNAME);
            String error = getResources().getString(R.string.com_auth0_lock_input_error_username, minUsernameLength, maxUsernameLength);
            if (minUsernameLength == UNUSED_USERNAME_LENGTH || maxUsernameLength == UNUSED_USERNAME_LENGTH) {
                error = getResources().getString(R.string.com_auth0_lock_input_error_username_empty);
            }
            setErrorDescription(error);
        } else if (configuration.getUsernameStyle() == DEFAULT) {
            setDataType(DataType.USERNAME_OR_EMAIL);
        }
    }

    @Override
    protected boolean validate(boolean validateEmptyFields) {
        final String value = getText().trim();
        if (!validateEmptyFields && value.isEmpty()) {
            return true;
        }
        boolean validUsername;
        if (minUsernameLength == UNUSED_USERNAME_LENGTH || maxUsernameLength == UNUSED_USERNAME_LENGTH) {
            validUsername = !value.isEmpty();
        } else {
            validUsername = value.matches(USERNAME_REGEX) && value.length() >= minUsernameLength && value.length() <= maxUsernameLength;
        }

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
