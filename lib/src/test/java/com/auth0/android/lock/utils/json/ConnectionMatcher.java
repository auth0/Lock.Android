/*
 * ConnectionMatcher.java
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

package com.auth0.android.lock.utils.json;


import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class ConnectionMatcher extends BaseMatcher<AuthData> {

    private final String strategy;
    private final String name;

    public ConnectionMatcher(String strategy, String name) {
        this.strategy = strategy;
        this.name = name;
    }

    @Override
    public boolean matches(Object o) {
        if (!(o instanceof AuthData)) {
            return false;
        }
        AuthData connection = (AuthData) o;
        if (name != null) {
            return name.equals(connection.getName());
        }
        if (strategy != null) {
            return strategy.equals(connection.getStrategy());
        }
        return false;
    }

    @Override
    public void describeTo(Description description) {
        if (strategy != null) {
            description.appendText("connection with strategy ").appendValue(this.strategy);
        }
        if (name != null) {
            description.appendText("connection with name ").appendValue(this.name);
        }
    }

    public static ConnectionMatcher hasName(String name) {
        return new ConnectionMatcher(null, name);
    }

    public static ConnectionMatcher hasStrategy(String name) {
        return new ConnectionMatcher(name, null);
    }
}