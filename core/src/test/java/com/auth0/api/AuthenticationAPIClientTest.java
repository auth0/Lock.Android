/*
 * AuthenticationAPIClientTest.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.api;

import android.os.Handler;

import com.auth0.android.BuildConfig;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.util.AuthenticationAPI;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;
import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class AuthenticationAPIClientTest {

    private static final String CLIENT_ID = "CLIENTID";
    private static final String DOMAIN = "samples.auth0.com";
    private static final String CONNECTION = "DB";

    private AuthenticationAPIClient client;

    private AuthenticationAPI mockAPI;

    @Before
    public void setUp() throws Exception {
        mockAPI = new AuthenticationAPI();
        final String domain = mockAPI.getDomain();
        Auth0 auth0 = new Auth0(CLIENT_ID, domain, domain);
        Handler handler = mock(Handler.class);
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Runnable runnable = (Runnable) invocation.getArguments()[0];
                runnable.run();
                return null;
            }
        }).when(handler).post(any(Runnable.class));
        client = new AuthenticationAPIClient(auth0, handler);
    }

    @After
    public void tearDown() throws Exception {
        mockAPI.shutdown();
    }

    @Test
    public void shouldCreateClientWithAccountInfo() throws Exception {
        AuthenticationAPIClient client = new AuthenticationAPIClient(new Auth0(CLIENT_ID, DOMAIN));
        assertThat(client, is(notNullValue()));
        assertThat(client.getClientId(), equalTo(CLIENT_ID));
        assertThat(client.getBaseURL(), equalTo("https://samples.auth0.com"));
    }

    @Test
    public void shouldLoadApplicationInfoFromConfigurationUrl() throws Exception {
        mockAPI.willReturnValidApplicationResponse();

        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);

        assertThat(mockAPI.takeRequest().getPath(), equalTo("/client/CLIENTID.js"));
        await().until(callback.payload(), is(notNullValue()));
        await().until(callback.error(), is(nullValue()));
    }

    @Test
    public void shoulFailWithInvalidJSON() throws Exception {
        mockAPI.willReturnApplicationResponseWithBody("Auth0Client.set({ })", 200);
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);
        await().until(callback.payload(), is(nullValue()));
        await().until(callback.error(), is(notNullValue()));
    }

    @Test
    public void shoulFailWithInvalidJSONP() throws Exception {
        mockAPI.willReturnApplicationResponseWithBody("INVALID_JSONP", 200);
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);
        await().until(callback.payload(), is(nullValue()));
        await().until(callback.error(), is(notNullValue()));
    }

    @Test
    public void shoulFailWithFailedStatusCode() throws Exception {
        mockAPI.willReturnApplicationResponseWithBody("Not Found", 404);
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);
        await().until(callback.payload(), is(nullValue()));
        await().until(callback.error(), is(notNullValue()));
    }

    @Test
    public void shouldLoginWithResourceOwner() throws Exception {
        mockAPI.willReturnSuccessfulLogin();
        final MockBaseCallback<Token> callback = new MockBaseCallback<>();

        final Map<String, Object> parameters = ParameterBuilder.newBuilder()
                .setConnection("DB")
                .setGrantType(ParameterBuilder.GRANT_TYPE_PASSWORD)
                .set("username", "support@auth0.com")
                .set("password", "notapassword")
                .setScope(ParameterBuilder.SCOPE_OPENID)
                .asDictionary();
        client.loginWithResourceOwner(parameters, callback);
        await().until(callback.payload(), is(notNullValue()));
        await().until(callback.error(), is(nullValue()));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getPath(), equalTo("/oauth/ro"));

        Map<String, String> body = payloadFromRequest(request);
        assertThat(body, hasEntry("connection", "DB"));
        assertThat(body, hasEntry("grant_type", "password"));
        assertThat(body, hasEntry("username", "support@auth0.com"));
        assertThat(body, hasEntry("password", "notapassword"));
        assertThat(body, hasEntry("scope", "openid"));
    }

    @Test
    public void shouldFailLoginWithResourceOwner() throws Exception {
        mockAPI.willReturnFailedLogin();
        final MockBaseCallback<Token> callback = new MockBaseCallback<>();

        final Map<String, Object> parameters = ParameterBuilder.newBuilder()
                .setConnection(CONNECTION)
                .setGrantType(ParameterBuilder.GRANT_TYPE_PASSWORD)
                .set("username", "support@auth0.com")
                .set("password", "notapassword")
                .asDictionary();
        client.loginWithResourceOwner(parameters, callback);
        await().until(callback.payload(), is(nullValue()));
        await().until(callback.error(), is(notNullValue()));
    }

    @Test
    public void shouldLoginWithUserAndPassword() throws Exception {
        mockAPI
                .willReturnSuccessfulLogin()
                .willReturnTokenInfo();
        final MockAuthenticationCallback callback = new MockAuthenticationCallback();

        client.login("support@auth0.com", "voidpassword", null, callback);
        await().until(callback.profile(), is(notNullValue()));
        await().until(callback.token(), is(notNullValue()));
        await().until(callback.error(), is(nullValue()));
    }

    @Test
    public void shouldFetchTokenInfo() throws Exception {
        mockAPI.willReturnTokenInfo();
        final MockBaseCallback<UserProfile> callback = new MockBaseCallback<>();

        client.tokenInfo("ID_TOKEN", callback);
        await().until(callback.payload(), is(notNullValue()));
        await().until(callback.error(), is(nullValue()));
        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getPath(), equalTo("/tokeninfo"));
    }

    private Map<String, String> payloadFromRequest(RecordedRequest request) throws java.io.IOException {
        return new ObjectMapper().readValue(request.getBody().inputStream(), new TypeReference<Map<String, String>>() {});
    }

    private static class MockBaseCallback<T> implements BaseCallback<T> {

        private T payload;
        private Throwable error;

        @Override
        public void onSuccess(T payload) {
            this.payload = payload;
        }

        @Override
        public void onFailure(Throwable error) {
            this.error = error;
        }

        Callable<T> payload() {
            return new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return payload;
                }
            };
        }

        Callable<Throwable> error() {
            return new Callable<Throwable>() {
                @Override
                public Throwable call() throws Exception {
                    return error;
                }
            };
        }
    }

    private static class MockAuthenticationCallback implements AuthenticationCallback {

        private Token token;
        private UserProfile profile;
        private Throwable error;

        @Override
        public void onSuccess(UserProfile profile, Token token) {
            this.token = token;
            this.profile = profile;
        }

        @Override
        public void onFailure(Throwable error) {
            this.error = error;
        }

        Callable<Token> token() {
            return new Callable<Token>() {
                @Override
                public Token call() throws Exception {
                    return token;
                }
            };
        }

        Callable<UserProfile> profile() {
            return new Callable<UserProfile>() {
                @Override
                public UserProfile call() throws Exception {
                    return profile;
                }
            };
        }

        Callable<Throwable> error() {
            return new Callable<Throwable>() {
                @Override
                public Throwable call() throws Exception {
                    return error;
                }
            };
        }
    }

}