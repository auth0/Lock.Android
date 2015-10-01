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
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.util.AuthenticationAPI;
import com.auth0.util.MockAuthenticationCallback;
import com.auth0.util.MockBaseCallback;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.okhttp.mockwebserver.RecordedRequest;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static com.auth0.util.AuthenticationCallbackMatcher.hasTokenAndProfile;
import static com.auth0.util.CallbackMatcher.hasNoPayloadOfType;
import static com.auth0.util.CallbackMatcher.hasPayloadOfType;
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
        client.fetchApplicationInfo()
                .start(callback);

        assertThat(mockAPI.takeRequest().getPath(), equalTo("/client/CLIENTID.js"));
        assertThat(callback, hasPayloadOfType(Application.class));
    }

    @Test
    public void shoulFailWithInvalidJSON() throws Exception {
        mockAPI.willReturnApplicationResponseWithBody("Auth0Client.set({ })", 200);
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo()
                .start(callback);
        assertThat(callback, hasNoPayloadOfType(Application.class));
    }

    @Test
    public void shoulFailWithInvalidJSONP() throws Exception {
        mockAPI.willReturnApplicationResponseWithBody("INVALID_JSONP", 200);
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo()
                .start(callback);
        assertThat(callback, hasNoPayloadOfType(Application.class));
    }

    @Test
    public void shouldFailWithFailedStatusCode() throws Exception {
        mockAPI.willReturnApplicationResponseWithBody("Not Found", 404);
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();

        client.fetchApplicationInfo()
                .start(callback);

        assertThat(callback, hasNoPayloadOfType(Application.class));
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
        client.loginWithResourceOwner()
            .setParameters(parameters)
            .start(callback);

        assertThat(callback, hasPayloadOfType(Token.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getPath(), equalTo("/oauth/ro"));

        Map<String, String> body = bodyFromRequest(request);
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
        client.loginWithResourceOwner()
                .setParameters(parameters)
                .start(callback);

        assertThat(callback, hasNoPayloadOfType(Token.class));
    }

    @Test
    public void shouldLoginWithUserAndPassword() throws Exception {
        mockAPI
            .willReturnSuccessfulLogin()
            .willReturnTokenInfo();
        final MockAuthenticationCallback callback = new MockAuthenticationCallback();

        client.login("support@auth0.com", "voidpassword")
            .start(callback);

        assertThat(callback, hasTokenAndProfile());
    }

    @Test
    public void shouldFetchTokenInfo() throws Exception {
        mockAPI.willReturnTokenInfo();
        final MockBaseCallback<UserProfile> callback = new MockBaseCallback<>();

        client.tokenInfo("ID_TOKEN")
            .start(callback);

        assertThat(callback, hasPayloadOfType(UserProfile.class));

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getPath(), equalTo("/tokeninfo"));
    }

    @Test
    public void shouldLoginWithOAuthAccessToken() throws Exception {
        mockAPI
                .willReturnSuccessfulLogin()
                .willReturnTokenInfo();

        final MockAuthenticationCallback callback = new MockAuthenticationCallback();
        client.loginWithOAuthAccessToken("fbtoken", "facebook")
                .start(callback);

        final RecordedRequest request = mockAPI.takeRequest();
        assertThat(request.getPath(), equalTo("/oauth/access_token"));

        Map<String, String> body = bodyFromRequest(request);
        assertThat(body, hasEntry("connection", "facebook"));
        assertThat(body, hasEntry("access_token", "fbtoken"));
        assertThat(body, hasEntry("scope", "openid offline_access"));

        assertThat(callback, hasTokenAndProfile());
    }

    private Map<String, String> bodyFromRequest(RecordedRequest request) throws java.io.IOException {
        return new ObjectMapper().readValue(request.getBody().inputStream(), new TypeReference<Map<String, String>>() {});
    }
}