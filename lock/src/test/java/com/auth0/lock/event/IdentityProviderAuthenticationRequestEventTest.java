/*
 * IdentityProviderAuthenticationRequestEventTest.java
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

package com.auth0.lock.event;

import android.net.Uri;

import com.auth0.android.BuildConfig;
import com.auth0.api.ParameterBuilder;
import com.auth0.core.Application;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class IdentityProviderAuthenticationRequestEventTest {

    private static final String CLIENT_ID = "TTg50aFpvVFC6vlNgAgq";
    private static final String TENANT = "samples";
    private static final String SERVICE_NAME = "facebook";
    private IdentityProviderAuthenticationRequestEvent event;

    @Mock private Application application;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        event = new IdentityProviderAuthenticationRequestEvent(SERVICE_NAME);
        when(application.getId()).thenReturn(CLIENT_ID);
        when(application.getTenant()).thenReturn(TENANT);
        when(application.getAuthorizeURL()).thenReturn("https://samples.auth0.com/authorize");
    }

    @Test
    public void shouldReturnServiceName() throws Exception {
        assertThat(event.getServiceName(), equalTo(SERVICE_NAME));
    }

    @Test
    public void shouldBuildAuthenticationUri() throws Exception {
        final Uri uri = event.getAuthenticationUri(application, null);
        assertThat(uri.getScheme(), equalTo("https"));
        assertThat(uri.getHost(), equalTo("samples.auth0.com"));
        assertThat(uri.getPath(), equalTo("/authorize"));
        assertThat(uri.getQueryParameter("scope"), equalTo("openid"));
        assertThat(uri.getQueryParameter("response_type"), equalTo("token"));
        assertThat(uri.getQueryParameter("connection"), equalTo(SERVICE_NAME));
        assertThat(uri.getQueryParameter("client_id"), equalTo(CLIENT_ID));
    }

    @Test
    public void shouldSetRedirectUriAsQueryParameter() throws Exception {
        final Uri uri = event.getAuthenticationUri(application, null);
        assertThat(uri.getQueryParameter("redirect_uri"), equalTo("a0" + CLIENT_ID.toLowerCase() + "://samples.auth0.com/authorize"));
    }

    @Test
    public void shouldSetRedirectUriForEU() throws Exception {
        when(application.getAuthorizeURL()).thenReturn("https://samples.eu.auth0.com/authorize");
        final Uri uri = event.getAuthenticationUri(application, null);
        assertThat(uri.getQueryParameter("redirect_uri"), equalTo("a0" + CLIENT_ID.toLowerCase() + "://samples.eu.auth0.com/authorize"));
    }

    @Test
    public void shouldOverrideDefaultScope() throws Exception {
        final String scope = "openid email";
        Map<String, Object> parameters = new ParameterBuilder()
                .setScope(scope)
                .asDictionary();
        final Uri uri = event.getAuthenticationUri(application, parameters);
        assertThat(uri.getQueryParameter("scope"), equalTo(scope));
    }

    @Test
    public void shouldSendExtraQueryParameters() throws Exception {
        final String state = "ragnarok";
        Map<String, Object> parameters = new ParameterBuilder()
                .set("state", state)
                .asDictionary();
        final Uri uri = event.getAuthenticationUri(application, parameters);
        assertThat(uri.getQueryParameter("state"), equalTo(state));
    }

    @Test
    public void shouldHaveLoginHintQueryParameter() throws Exception {
        final String username = "username-value";
        IdentityProviderAuthenticationRequestEvent event = new IdentityProviderAuthenticationRequestEvent(SERVICE_NAME, username);
        final Uri uri = event.getAuthenticationUri(application, null);
        assertThat(uri.getQueryParameter("login_hint"), equalTo(username));
    }

    @Test
    public void shouldHaveLoginHintOnlyUsernameQueryParameter() throws Exception {
        final String username = "username@example.com";
        IdentityProviderAuthenticationRequestEvent event = new IdentityProviderAuthenticationRequestEvent(SERVICE_NAME, username);
        final Uri uri = event.getAuthenticationUri(application, null);
        assertThat(uri.getQueryParameter("login_hint"), equalTo("username"));
    }
}
