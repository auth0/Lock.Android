/*
 * CallbackMatcher.java
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

import com.auth0.android.Auth0Exception;
import com.google.gson.reflect.TypeToken;
import com.jayway.awaitility.core.ConditionTimeoutException;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

public class CallbackMatcher<T, U extends Auth0Exception> extends BaseMatcher<MockCallback<T, U>> {
    private final Matcher<T> payloadMatcher;
    private final Matcher<U> errorMatcher;

    public CallbackMatcher(Matcher<T> payloadMatcher, Matcher<U> errorMatcher) {
        this.payloadMatcher = payloadMatcher;
        this.errorMatcher = errorMatcher;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean matches(Object item) {
        MockCallback<T, U> callback = (MockCallback<T, U>) item;
        try {
            await().until(callback.payload(), payloadMatcher);
            await().until(callback.error(), errorMatcher);
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

    public static <T, U extends Auth0Exception> Matcher<MockCallback<T, U>> hasPayloadOfType(Class<T> clazz, Class<U> uClazz) {
        return new CallbackMatcher<>(isA(clazz), Matchers.is(Matchers.nullValue(uClazz)));
    }

    public static <T, U extends Auth0Exception> Matcher<MockCallback<T, U>> hasPayloadOfType(TypeToken<T> tType, TypeToken<U> uType) {
        return new CallbackMatcher<>(allOf(notNullValue(), TypeTokenMatcher.isA(tType)), allOf(nullValue(), TypeTokenMatcher.isA(uType)));
    }

    public static <T, U extends Auth0Exception> Matcher<MockCallback<T, U>> hasPayload(T payload, Class<U> uClazz) {
        return new CallbackMatcher<>(Matchers.equalTo(payload), Matchers.is(Matchers.nullValue(uClazz)));
    }

    public static <T, U extends Auth0Exception> Matcher<MockCallback<T, U>> hasNoPayloadOfType(Class<T> clazz, Class<U> uClazz) {
        return new CallbackMatcher<>(Matchers.is(Matchers.nullValue(clazz)), Matchers.is(notNullValue(uClazz)));
    }

    public static <T, U extends Auth0Exception> Matcher<MockCallback<T, U>> hasNoPayloadOfType(TypeToken<T> tType, TypeToken<U> uType) {
        return new CallbackMatcher<>(allOf(nullValue(), TypeTokenMatcher.isA(tType)), allOf(notNullValue(), not(TypeTokenMatcher.isA(uType))));
    }

    public static <U extends Auth0Exception> Matcher<MockCallback<Void, U>> hasNoError(Class<U> uClazz) {
        return new CallbackMatcher<>(Matchers.is(Matchers.nullValue(Void.class)), Matchers.is(Matchers.nullValue(uClazz)));
    }

    public static <U extends Auth0Exception> Matcher<MockCallback<Void, U>> hasError(Class<U> uClazz) {
        return new CallbackMatcher<>(Matchers.is(Matchers.nullValue(Void.class)), Matchers.is(notNullValue(uClazz)));
    }
}
