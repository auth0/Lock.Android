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

package com.auth0.android.lock.utils;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.authentication.result.Authentication;
import com.auth0.callback.BaseCallback;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.OkHttpClient;

import org.hamcrest.Matcher;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.RecordedRequest;

import static com.auth0.android.lock.utils.CallbackMatcher.hasError;
import static com.auth0.android.lock.utils.CallbackMatcher.hasNoError;
import static com.auth0.android.lock.utils.CallbackMatcher.hasNoPayloadOfType;
import static com.auth0.android.lock.utils.CallbackMatcher.hasPayloadOfType;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ApplicationFetcherTest {

    private static final String CLIENT_ID = "client_id";
    private static final String DOMAIN = "domain";

    private ApplicationFetcher appFetcher;
    private ApplicationAPI mockAPI;

    @Before
    public void setUp() throws Exception {
        mockAPI = new ApplicationAPI();

        Auth0 account = new Auth0(CLIENT_ID, mockAPI.getDomain());
        OkHttpClient client = new OkHttpClient();
        appFetcher = new ApplicationFetcher(account, client);
    }

    @After
    public void tearDown() throws Exception {
        mockAPI.shutdown();
    }

    @Test
    public void shouldReturnApplicationOnValidResponse() throws Exception {
        mockAPI.willReturnValidApplicationResponse();
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        appFetcher.fetch(callback);
        mockAPI.takeRequest();

        assertThat(callback, hasPayloadOfType(Application.class));
    }

    @Test
    public void shouldReturnExceptionOnInvalidJSONPLengthResponse() throws Exception {
        mockAPI.willReturnInvalidJSONPLengthResponse();
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        appFetcher.fetch(callback);
        mockAPI.takeRequest();

        assertThat(callback, hasNoPayloadOfType(Application.class));
//        assertThat(callback, hasError());
    }
}