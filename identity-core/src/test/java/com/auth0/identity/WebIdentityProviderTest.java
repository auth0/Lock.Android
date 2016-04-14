/*
 * WebIdentityProviderTest.java
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

package com.auth0.identity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.auth0.core.Token;
import com.auth0.identity.web.CallbackParser;
import com.auth0.identity.web.WebViewActivity;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class WebIdentityProviderTest {

    private static final String SERVICE_NAME = "I.O.U. a service name";
    private static final String ID_TOKEN = "ID TOKEN";
    private static final String ACCESS_TOKEN = "ACCESS TOKEN";
    public static final String TOKEN_TYPE = "TOKEN TYPE";
    public static final String REFRESH_TOKEN = "REFRESH TOKEN";
    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String AUTHORIZE_URL = "https://samples.auth0.com/authorize";

    private WebIdentityProvider provider;

    @Mock
    private CallbackParser parser;
    @Mock
    private IdentityProviderCallback callback;
    @Mock
    private IdentityProviderRequest request;
    @Mock
    private Activity activity;
    @Mock
    private Application application;
    @Mock
    private Uri uri;
    @Mock
    private Intent data;
    @Mock
    private Auth0 auth0;
    @Mock
    private AuthenticationAPIClient client;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        provider = new WebIdentityProvider(parser, CLIENT_ID, AUTHORIZE_URL);
        provider.setCallback(callback);
        when(request.getServiceName()).thenReturn(SERVICE_NAME);
        when(auth0.getClientId()).thenReturn(CLIENT_ID);
        when(auth0.getAuthorizeUrl()).thenReturn(AUTHORIZE_URL);
        when(auth0.newAuthenticationAPIClient()).thenReturn(client);
    }

    @Test
    public void shouldSendViewActionIntent() throws Exception {
        provider.setUseWebView(false);
        provider.start(activity, request, application);

        final ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(captor.capture());
        final Intent intent = captor.getValue();
        assertThat(intent.getAction(), equalTo(Intent.ACTION_VIEW));
        assertThat(intent.getData(), isAuthorizeUri("samples.auth0.com", CLIENT_ID, "token"));
    }

    @Test
    public void shouldStartWebViewActivity() throws Exception {
        provider.setUseWebView(true);
        provider.start(activity, request, application);

        final ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivityForResult(captor.capture(), eq(IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE));
        final Intent intent = captor.getValue();
        assertThat(intent.getComponent().getClassName(), equalTo(WebViewActivity.class.getName()));
        assertThat(intent.getData(), isAuthorizeUri("samples.auth0.com", CLIENT_ID, "token"));
        assertThat(intent.getStringExtra(WebViewActivity.SERVICE_NAME_EXTRA), equalTo(SERVICE_NAME));
    }

    @Test
    public void shouldStartWebViewActivityWithNoConnection() throws Exception {
        provider.setUseWebView(true);
        provider.start(activity);

        final ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivityForResult(captor.capture(), eq(IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE));
        final Intent intent = captor.getValue();
        assertThat(intent.getComponent().getClassName(), equalTo(WebViewActivity.class.getName()));
        assertThat(intent.getData(), equalTo(Uri.parse("https://samples.auth0.com/authorize?response_type=token&scope=openid&redirect_uri=a0client_id%3A%2F%2Fsamples.auth0.com%2Fcallback&client_id=CLIENT_ID")));
        assertThat(intent.getStringExtra(WebViewActivity.SERVICE_NAME_EXTRA), is(nullValue()));
    }

    @Test
    public void shouldUsePKCE() throws Exception {
        provider = new WebIdentityProvider(auth0, true);
        provider.setUseWebView(true);
        provider.start(activity);

        final ArgumentCaptor<Intent> captor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivityForResult(captor.capture(), eq(IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE));
        final Intent intent = captor.getValue();
        assertThat(intent.getComponent().getClassName(), equalTo(WebViewActivity.class.getName()));
        final Uri uri = intent.getData();
        assertThat(uri, isAuthorizeUri("samples.auth0.com", CLIENT_ID, "code"));
        assertThat(uri.getQueryParameter("code_challenge"), is(notNullValue()));
        assertThat(uri.getQueryParameter("code_challenge"), is(not(equalTo("null"))));
        assertThat(intent.getStringExtra(WebViewActivity.SERVICE_NAME_EXTRA), is(nullValue()));
    }

    @Test
    public void shouldSuccessfulAuthentication() throws Exception {
        when(parser.getValuesFromUri(eq(uri))).thenReturn(validToken());
        when(data.getData()).thenReturn(uri);
        boolean valid = provider.authorize(activity, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, Activity.RESULT_OK, data);
        assertThat(valid, is(true));

        final ArgumentCaptor<Token> captor = ArgumentCaptor.forClass(Token.class);
        verify(callback).onSuccess(captor.capture());
        final Token token = captor.getValue();
        assertThat(token.getIdToken(), equalTo(ID_TOKEN));
        assertThat(token.getAccessToken(), equalTo(ACCESS_TOKEN));
        assertThat(token.getType(), equalTo(TOKEN_TYPE));
        assertThat(token.getRefreshToken(), equalTo(REFRESH_TOKEN));
    }

    @Test
    public void shouldDoNothingWithEmptyQueryString() throws Exception {
        when(parser.getValuesFromUri(eq(uri))).thenReturn(new HashMap<String, String>());
        when(data.getData()).thenReturn(uri);
        boolean valid = provider.authorize(activity, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, Activity.RESULT_OK, data);
        assertThat(valid, is(true));

        verifyZeroInteractions(callback);
    }

    @Test
    public void shouldDoNothingWithNonOKResult() throws Exception {
        when(parser.getValuesFromUri(eq(uri))).thenReturn(new HashMap<String, String>());
        when(data.getData()).thenReturn(uri);
        boolean valid = provider.authorize(activity, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, Activity.RESULT_CANCELED, data);
        assertThat(valid, is(false));

        verifyZeroInteractions(callback);
    }

    @Test
    public void shouldDoNothingWithInvalidRequestCode() throws Exception {
        when(parser.getValuesFromUri(eq(uri))).thenReturn(new HashMap<String, String>());
        when(data.getData()).thenReturn(uri);
        boolean valid = provider.authorize(activity, 12345677, Activity.RESULT_OK, data);
        assertThat(valid, is(false));

        verifyZeroInteractions(callback);
    }

    @Test
    public void shouldDoNothingWithNullDataUri() throws Exception {
        when(data.getData()).thenReturn(null);
        boolean valid = provider.authorize(activity, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, Activity.RESULT_OK, data);
        assertThat(valid, is(false));

        verifyZeroInteractions(callback);
    }

    @Test
    public void shouldDoNothingWithNullData() throws Exception {
        boolean valid = provider.authorize(activity, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, Activity.RESULT_OK, null);
        assertThat(valid, is(false));

        verifyZeroInteractions(callback);
    }

    @Test
    public void shouldHandleAccessDeniedError() throws Exception {
        when(parser.getValuesFromUri(eq(uri))).thenReturn(errorValue("access_denied"));
        when(data.getData()).thenReturn(uri);
        boolean valid = provider.authorize(activity, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, Activity.RESULT_OK, data);
        assertThat(valid, is(true));

        verify(callback).onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_access_denied_message, null);
    }

    @Test
    public void shouldHandleRandomError() throws Exception {
        when(parser.getValuesFromUri(eq(uri))).thenReturn(errorValue("something_went_wrong"));
        when(data.getData()).thenReturn(uri);
        boolean valid = provider.authorize(activity, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, Activity.RESULT_OK, data);
        assertThat(valid, is(true));

        verify(callback).onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_error_message, null);
    }

    private Map<String, String> errorValue(String message) {
        Map<String, String> values = new HashMap<>();
        values.put("error", message);
        return values;
    }

    private Map<String, String> validToken() {
        Map<String, String> tokenValues = new HashMap<>();
        tokenValues.put("id_token", ID_TOKEN);
        tokenValues.put("access_token", ACCESS_TOKEN);
        tokenValues.put("token_type", TOKEN_TYPE);
        tokenValues.put("refresh_token", REFRESH_TOKEN);
        return tokenValues;
    }

    private static class AuthorizeMatcher extends BaseMatcher<Uri> {

        private final String responseType;
        private final String clientId;
        private final String domain;

        public AuthorizeMatcher(String domain, String clientId, String responseType) {
            this.responseType = responseType;
            this.clientId = clientId;
            this.domain = domain;
        }

        @Override
        public boolean matches(Object item) {
            if (!(item instanceof Uri)) {
                return false;
            }
            Uri uri = (Uri) item;
            final String responseType = uri.getQueryParameter("response_type");
            final String clientId = uri.getQueryParameter("client_id");
            final String redirectUri = uri.getQueryParameter("redirect_uri");
            final String expectedRedirectUri = String.format("a0%s://%s/callback", clientId.toLowerCase(), domain);
            return this.domain.equalsIgnoreCase(uri.getHost())
                    && "/authorize".equalsIgnoreCase(uri.getPath())
                    && this.responseType.equalsIgnoreCase(responseType)
                    && this.clientId.equalsIgnoreCase(clientId)
                    && expectedRedirectUri.equalsIgnoreCase(redirectUri);
        }

        @Override
        public void describeTo(Description description) {
            description.appendText("A valid authorize uri with the following values").appendText("\n")
            .appendText("domain: ").appendValue(domain).appendText(" ")
            .appendText("client_id: ").appendValue(clientId).appendText(" ")
            .appendText("response_type: ").appendValue(responseType);
        }
    }

    private static AuthorizeMatcher isAuthorizeUri(String domain, String clientId, String responseType) {
        return new AuthorizeMatcher(domain, clientId, responseType);
    }
}