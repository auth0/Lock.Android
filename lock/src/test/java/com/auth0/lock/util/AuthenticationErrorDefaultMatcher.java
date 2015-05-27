/*
 * AuthenticationErrorDefaultMatcher.java
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

package com.auth0.lock.util;

import android.app.Application;

import com.auth0.lock.R;
import com.auth0.lock.event.AuthenticationError;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import static org.hamcrest.Matchers.equalTo;

public class AuthenticationErrorDefaultMatcher extends BaseMatcher<AuthenticationError> {
    private final Matcher<String> messageMatcher;
    private final Matcher<String> titleMatcher;

    private AuthenticationErrorDefaultMatcher() {
        this(R.string.com_auth0_db_login_error_title, R.string.com_auth0_db_login_error_message);
    }

    private AuthenticationErrorDefaultMatcher(int messageResource) {
        this(R.string.com_auth0_db_login_error_title, messageResource);
    }

    public AuthenticationErrorDefaultMatcher(int titleResource, int messageResource) {
        messageMatcher = equalTo(RuntimeEnvironment.application.getString(messageResource));
        titleMatcher = equalTo(RuntimeEnvironment.application.getString(titleResource));
    }

    @Override
    public boolean matches(Object o) {
        AuthenticationError error = (AuthenticationError) o;
        Application application = RuntimeEnvironment.application;
        return messageMatcher.matches(error.getMessage(application))
                && titleMatcher.matches(error.getTitle(application));
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("String resources: ")
                .appendDescriptionOf(messageMatcher)
                .appendText(" & ")
                .appendDescriptionOf(titleMatcher);
    }

    public static Matcher<AuthenticationError> hasDefaultTitleAndMessage() {
        return new AuthenticationErrorDefaultMatcher();
    }

    public static Matcher<AuthenticationError> hasMessage(int messageResource) {
        return new AuthenticationErrorDefaultMatcher(messageResource);
    }

    public static Matcher<AuthenticationError> hasError(int titleResource, int messageResource) {
        return new AuthenticationErrorDefaultMatcher(titleResource, messageResource);
    }

}
