/*
 * StrategyMatcher.java
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

import com.auth0.android.lock.utils.Strategies;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class StrategyMatcher extends BaseMatcher<Strategy> {

    private final String name;

    public StrategyMatcher(String name) {
        this.name = name;
    }

    @Override
    public boolean matches(Object o) {
        if (!(o instanceof Strategy)) {
            return false;
        }
        Strategy strategy = (Strategy) o;
        return name.equals(strategy.getName());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("a Strategy with name ").appendValue(name);
    }

    public static StrategyMatcher isStrategy(Strategies strategy) {
        return new StrategyMatcher(strategy.getName());
    }
}
