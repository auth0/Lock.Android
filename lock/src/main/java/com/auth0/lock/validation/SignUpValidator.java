/*
 * SignUpValidator.java
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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class SignUpValidator implements Validator {

    private final Validator emailValidator;
    private final Validator usernameValidator;
    private final Validator passwordValidator;
    private final int compositeErrorMessage;

    public SignUpValidator(Validator emailValidator, Validator usernameValidator, Validator passwordValidator, int compositeErrorMessage) {
        this.emailValidator = emailValidator;
        this.usernameValidator = usernameValidator;
        this.passwordValidator = passwordValidator;
        this.compositeErrorMessage = compositeErrorMessage;
    }

    public SignUpValidator(boolean useEmail, boolean requiresUsername, UsernameLengthParser lengthParser) {
        this(
                emailValidator(useEmail, requiresUsername),
                usernameValidator(useEmail, requiresUsername, lengthParser),
                new PasswordValidator(R.id.com_auth0_db_signup_password_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_password_message),
                useEmail ? R.string.com_auth0_invalid_credentials_message : R.string.com_auth0_invalid_username_credentials_message
        );
    }

    @Override
    public AuthenticationError validateFrom(Fragment fragment) {
        Set<AuthenticationError> errors = new HashSet<>();
        errors.add(emailValidator.validateFrom(fragment));
        errors.add(usernameValidator.validateFrom(fragment));
        errors.add(passwordValidator.validateFrom(fragment));
        errors.removeAll(Collections.singleton(null));
        if (errors.size() > 1) {
            return new AuthenticationError(R.string.com_auth0_invalid_credentials_title, compositeErrorMessage);
        }
        if (errors.isEmpty()) {
            return null;
        }
        return errors.iterator().next();
    }

    public static Validator emailValidator(boolean useEmail, boolean requiresUsername) {
        if (useEmail || requiresUsername) {
            return new EmailValidator(R.id.com_auth0_db_signup_email_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_email_message);
        }
        return new NullValidator();
    }

    public static Validator usernameValidator(boolean useEmail, boolean requiresUsername, UsernameLengthParser lengthParser) {
        if (!useEmail || requiresUsername) {
            return new UsernameValidator(R.id.com_auth0_db_signup_username_field, R.string.com_auth0_invalid_credentials_title, R.string.com_auth0_invalid_username_message, lengthParser);
        }
        return new NullValidator();
    }
}
