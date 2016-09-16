/*
 * ApplicationFetcherTest.java
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

package com.auth0.android.lock.internal.configuration;

import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.lock.utils.ApplicationAPI;
import com.auth0.android.lock.utils.Auth0AuthenticationCallbackMatcher;
import com.auth0.android.lock.utils.MockAuthenticationCallback;
import com.google.gson.reflect.TypeToken;
import com.squareup.okhttp.OkHttpClient;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ApplicationFetcherTest {

    private ApplicationFetcher appFetcher;
    private ApplicationAPI mockAPI;

    @Before
    public void setUp() throws Exception {
        mockAPI = new ApplicationAPI();

        final Options options = Mockito.mock(Options.class);
        Auth0 account = new Auth0("client_id", mockAPI.getDomain());
        Mockito.when(options.getAccount()).thenReturn(account);
        OkHttpClient client = new OkHttpClient();
        appFetcher = new ApplicationFetcher(options, client);
    }

    @After
    public void tearDown() throws Exception {
        mockAPI.shutdown();
    }

    @Test
    public void shouldReturnApplicationOnValidJSONPResponse() throws Exception {
        mockAPI.willReturnValidJSONPResponse();
        final MockAuthenticationCallback<Configuration> callback = new MockAuthenticationCallback<>();
        appFetcher.fetch(callback);
        mockAPI.takeRequest();

        TypeToken<Configuration> applicationType = new TypeToken<Configuration>(){};
        assertThat(callback, Auth0AuthenticationCallbackMatcher.hasPayloadOfType(applicationType));
    }

    @Test
    public void shouldReturnExceptionOnInvalidJSONPResponse() throws Exception {
        mockAPI.willReturnInvalidJSONPLengthResponse();
        final MockAuthenticationCallback<Configuration> callback = new MockAuthenticationCallback<>();
        appFetcher.fetch(callback);
        mockAPI.takeRequest();

        TypeToken<Configuration> applicationType = new TypeToken<Configuration>(){};
        assertThat(callback, Auth0AuthenticationCallbackMatcher.hasNoPayloadOfType(applicationType));
        assertThat(callback.getError(), CoreMatchers.instanceOf(Auth0Exception.class));
        assertThat(callback.getError().getCause().getCause().getMessage(), CoreMatchers.containsString("Invalid App Info JSONP"));
    }
}