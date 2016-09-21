/*
 * ResetPasswordValidator.java
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

package com.auth0.lock.validation;

import android.support.v4.app.Fragment;

import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.util.UsernameLengthParser;

public class ResetPasswordValidator implements Validator {

    private Validator emailValidator;
    private Validator passwordValidator;
    private Validator repeatPasswordValidator;
    private final int compositeErrorMessage;

    public ResetPasswordValidator(Validator emailValidator, Validator passwordValidator, Validator repeatPasswordValidator, int compositeErrorMessage) {
        this.emailValidator = emailValidator;
        this.passwordValidator = passwordValidator;
        this.repeatPasswordValidator = repeatPasswordValidator;
        this.compositeErrorMessage = compositeErrorMessage;
    }

    public ResetPasswordValidator(boolean useEmail, UsernameLengthParser lengthParser) {
        this(
            validatorThatUseEmail(useEmail, lengthParser),
            new PasswordValidator(R.id.com_auth0_db_change_password_password_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_password_message),
            new RepeatPasswordValidator(R.id.com_auth0_db_change_password_repeat_password_field, R.id.com_auth0_db_change_password_password_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_db_reset_password_invalid_repeat_password_message),
            useEmail ? R.string.com_auth0_invalid_credentials_message : R.string.com_auth0_invalid_username_credentials_message
        );
    }

    @Override
    public AuthenticationError validateFrom(Fragment fragment) {
        AuthenticationError emailError = emailValidator.validateFrom(fragment);
        AuthenticationError passwordError = passwordValidator.validateFrom(fragment);
        AuthenticationError repeatError = repeatPasswordValidator.validateFrom(fragment);
        if (emailError != null && (passwordError != null || repeatError != null)) {
            return new AuthenticationError(R.string.com_auth0_invalid_credentials_title, compositeErrorMessage);
        }
        if (repeatError != null) {
            return repeatError;
        }
        return passwordError != null ? passwordError : emailError;
    }

    public static Validator validatorThatUseEmail(boolean useEmail, UsernameLengthParser lengthParser) {
        if (useEmail) {
            return new EmailValidator(R.id.com_auth0_db_change_password_username_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_email_message);
        }
        return new UsernameValidator(R.id.com_auth0_db_change_password_username_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_password_message, lengthParser);
    }

}
