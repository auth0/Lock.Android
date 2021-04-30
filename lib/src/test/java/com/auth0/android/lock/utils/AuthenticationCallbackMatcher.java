/*
 * AuthenticationCallbackMatcher.java
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

package com.auth0.android.lock.utils;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.result.Credentials;
import com.jayway.awaitility.Duration;
import com.jayway.awaitility.core.ConditionTimeoutException;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static com.jayway.awaitility.Awaitility.waitAtMost;
import static org.hamcrest.CoreMatchers.any;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.isA;

public class AuthenticationCallbackMatcher extends BaseMatcher<MockLockCallback> {
    private final Matcher<Credentials> authenticationMatcher;
    private final Matcher<Boolean> canceledMatcher;
    private final Matcher<Throwable> errorMatcher;

    public AuthenticationCallbackMatcher(Matcher<Credentials> authenticationMatcher, Matcher<Boolean> canceledMatcher, Matcher<Throwable> errorMatcher) {
        this.authenticationMatcher = authenticationMatcher;
        this.canceledMatcher = canceledMatcher;
        this.errorMatcher = errorMatcher;
    }

    @Override
    public boolean matches(Object item) {
        MockLockCallback callback = (MockLockCallback) item;
        try {
            waitAtMost(Duration.ONE_SECOND).await().until(callback.authentication(), authenticationMatcher);
            waitAtMost(Duration.ONE_SECOND).await().until(callback.canceled(), canceledMatcher);
            waitAtMost(Duration.ONE_SECOND).await().until(callback.error(), errorMatcher);
            return true;
        } catch (ConditionTimeoutException e) {
            return false;
        }
    }

    @Override
    public void describeTo(Description description) {
        description
                .appendText("successful method be called");
    }

    public static AuthenticationCallbackMatcher isCanceled() {
        return new AuthenticationCallbackMatcher(is(nullValue(Credentials.class)), equalTo(true), is(notNullValue(Throwable.class)));
    }

    public static AuthenticationCallbackMatcher hasAuthentication() {
        return new AuthenticationCallbackMatcher(is(notNullValue(Credentials.class)), equalTo(false), is(nullValue(Throwable.class)));
    }

    public static AuthenticationCallbackMatcher hasError() {
        return new AuthenticationCallbackMatcher(is(nullValue(Credentials.class)), any(Boolean.class), is(notNullValue(Throwable.class)));
    }

    public static AuthenticationCallbackMatcher hasNoError() {
        return new AuthenticationCallbackMatcher(anyOf(nullValue(Credentials.class), notNullValue(Credentials.class)), any(Boolean.class), is(nullValue(Throwable.class)));
    }

    public static <T> CallbackMatcher<T, AuthenticationException> hasPayloadOfType(Class<T> clazz) {
        return new CallbackMatcher<>(isA(clazz), Matchers.is(Matchers.nullValue(AuthenticationException.class)));
    }

    public static <T> CallbackMatcher<T, AuthenticationException> hasPayload(T payload) {
        return new CallbackMatcher<>(Matchers.equalTo(payload), Matchers.is(Matchers.nullValue(AuthenticationException.class)));
    }

    public static <T> CallbackMatcher<T, AuthenticationException> hasNoPayloadOfType(Class<T> clazz) {
        return new CallbackMatcher<>(Matchers.is(Matchers.nullValue(clazz)), Matchers.is(Matchers.notNullValue(AuthenticationException.class)));
    }

}
