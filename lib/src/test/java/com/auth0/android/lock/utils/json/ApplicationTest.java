/*
 * ApplicationTest.java
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

import com.auth0.android.lock.BuildConfig;
import com.auth0.android.lock.utils.Strategies;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.auth0.android.lock.utils.Strategies.Auth0;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ApplicationTest {

    public static final String ID = "ID";
    public static final String TENANT = "TENANT";
    public static final String AUTHORIZE_URL = "AUTHORIZE";
    public static final String CALLBACK_URL = "CALLBACK";
    public static final String SUBSCRIPTION = "SUBSCRIPTION";
    public static final boolean HAS_ALLOWED_ORIGINS = true;

    @Test
    public void shouldInstantiateApplication() throws Exception {
        Application application = newApplicationWithStrategies(Auth0);
        assertThat(application, is(notNullValue()));
    }

    @Test
    public void shouldHaveApplicationInfo() throws Exception {
        Application application = newApplicationWithStrategies(Auth0);
        assertThat(application.getId(), equalTo(ID));
        assertThat(application.getTenant(), equalTo(TENANT));
        assertThat(application.getAuthorizeURL(), equalTo(AUTHORIZE_URL));
        assertThat(application.getCallbackURL(), equalTo(CALLBACK_URL));
        assertThat(application.getSubscription(), equalTo(SUBSCRIPTION));
        assertThat(application.hasAllowedOrigins(), equalTo(HAS_ALLOWED_ORIGINS));
    }

    private static AuthData newStrategyFor(Strategies strategyMetadata) {
        return new AuthData(strategyMetadata.getName(), new HashMap<String, Object>());
    }

    private static Application newApplicationWithStrategies(Strategies... list) {
        List<AuthData> strategies = new ArrayList<>();
        for (Strategies str : list) {
            strategies.add(newStrategyFor(str));
        }
        Application application = new Application(ID, TENANT, AUTHORIZE_URL, CALLBACK_URL, SUBSCRIPTION, HAS_ALLOWED_ORIGINS, strategies);
        return application;
    }
}