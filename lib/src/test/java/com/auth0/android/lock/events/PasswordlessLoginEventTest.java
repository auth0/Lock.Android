/*
 * PasswordlessLoginEventTest.java
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

package com.auth0.android.lock.events;

import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.PasswordlessType;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.request.AuthenticationRequest;
import com.auth0.android.request.ParameterizableRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class PasswordlessLoginEventTest {

    private static final String CONNECTION_NAME = "connectionName";
    private static final String EMAIL = "an@email.com";
    private static final String PHONE_NUMBER_WITH_CODE = "+11234567890";
    private static final String PHONE_NUMBER_WITHOUT_CODE = "1234567890";
    private static final String CODE = "123456";
    private static final String CONNECTION_KEY = "connection";

    private Country country;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        country = new Country("US", "+1");
    }

    @Test
    public void shouldHaveNullCodeByDefault() throws Exception {
        PasswordlessLoginEvent event = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_LINK, EMAIL);

        Assert.assertThat(event.getEmailOrNumber(), is(equalTo(EMAIL)));
        Assert.assertThat(event.getMode(), is(equalTo(PasswordlessMode.EMAIL_LINK)));
        Assert.assertThat(event.getCode(), is(nullValue()));
    }

    @Test
    public void shouldSetTheCode() throws Exception {
        PasswordlessLoginEvent event = PasswordlessLoginEvent.submitCode(PasswordlessMode.EMAIL_CODE, CODE);

        Assert.assertThat(event.getMode(), is(equalTo(PasswordlessMode.EMAIL_CODE)));
        Assert.assertThat(event.getCode(), is(equalTo(CODE)));
    }

    @Test
    public void shouldGetValidCodeRequestWhenUsingEmailAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithEmail(EMAIL, PasswordlessType.CODE)).thenReturn(request);
        when(request.addParameter(CONNECTION_KEY, CONNECTION_NAME)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_CODE, EMAIL);
        ParameterizableRequest<Void, AuthenticationException> resultRequest = emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(request));
    }

    @Test
    public void shouldGetValidCodeRequestWhenUsingEmailAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithEmail(EMAIL, PasswordlessType.LINK_ANDROID)).thenReturn(request);
        when(request.addParameter(CONNECTION_KEY, CONNECTION_NAME)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_LINK, EMAIL);
        ParameterizableRequest<Void, AuthenticationException> resultRequest = emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(request));
    }

    @Test
    public void shouldGetValidCodeRequestWhenUsingSMSAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.CODE)).thenReturn(request);
        when(request.addParameter(CONNECTION_KEY, CONNECTION_NAME)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_CODE, PHONE_NUMBER_WITHOUT_CODE, country);
        ParameterizableRequest<Void, AuthenticationException> resultRequest = emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(request));
    }

    @Test
    public void shouldGetValidCodeRequestWhenUsingSMSAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.LINK_ANDROID)).thenReturn(request);
        when(request.addParameter(CONNECTION_KEY, CONNECTION_NAME)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_LINK, PHONE_NUMBER_WITHOUT_CODE, country);
        ParameterizableRequest<Void, AuthenticationException> resultRequest = emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(request));
    }

    @Test
    public void shouldCallApiClientPasswordlessStartWhenUsingSMSAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.LINK_ANDROID)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_LINK, PHONE_NUMBER_WITHOUT_CODE, country);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(client).passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.LINK_ANDROID);
    }

    @Test
    public void shouldCallApiClientPasswordlessStartWhenUsingSMSAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.CODE)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_CODE, PHONE_NUMBER_WITHOUT_CODE, country);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(client).passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.CODE);
    }

    @Test
    public void shouldCallApiClientPasswordlessStartWhenUsingEmailAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithEmail(EMAIL, PasswordlessType.LINK_ANDROID)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_LINK, EMAIL);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(client).passwordlessWithEmail(EMAIL, PasswordlessType.LINK_ANDROID);
    }

    @Test
    public void shouldCallApiClientPasswordlessStartWhenUsingEmailAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithEmail(EMAIL, PasswordlessType.CODE)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_CODE, EMAIL);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(client).passwordlessWithEmail(EMAIL, PasswordlessType.CODE);
    }

    @Test
    public void shouldSetConnectionWhenUsingSMSAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.LINK_ANDROID)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_LINK, PHONE_NUMBER_WITHOUT_CODE, country);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(request).addParameter(CONNECTION_KEY, CONNECTION_NAME);
    }

    @Test
    public void shouldSetConnectionWhenWhenUsingSMSAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithSMS(PHONE_NUMBER_WITH_CODE, PasswordlessType.CODE)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_CODE, PHONE_NUMBER_WITHOUT_CODE, country);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(request).addParameter(CONNECTION_KEY, CONNECTION_NAME);
    }

    @Test
    public void shouldSetConnectionWhenWhenUsingEmailAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithEmail(EMAIL, PasswordlessType.LINK_ANDROID)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_LINK, EMAIL);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(request).addParameter(CONNECTION_KEY, CONNECTION_NAME);
    }

    @Test
    public void shouldSetConnectionWhenWhenUsingEmailAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        ParameterizableRequest<Void, AuthenticationException> request = mock(ParameterizableRequest.class);
        when(client.passwordlessWithEmail(EMAIL, PasswordlessType.CODE)).thenReturn(request);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_CODE, EMAIL);
        emailCodeEvent.getCodeRequest(client, CONNECTION_NAME);

        verify(request).addParameter(CONNECTION_KEY, CONNECTION_NAME);
    }

    @Test
    public void shouldGetValidLoginRequestWhenUsingEmailAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithEmail(EMAIL, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.EMAIL_CODE, CODE);
        AuthenticationRequest resultRequest = emailCodeEvent.getLoginRequest(client, EMAIL);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(authRequest));
    }

    @Test
    public void shouldGetValidLoginRequestWhenUsingEmailAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithEmail(EMAIL, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.EMAIL_LINK, CODE);
        AuthenticationRequest resultRequest = emailCodeEvent.getLoginRequest(client, EMAIL);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(authRequest));
    }

    @Test
    public void shouldGetValidLoginRequestWhenUsingSMSAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithPhoneNumber(PHONE_NUMBER_WITH_CODE, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.SMS_CODE, CODE);
        AuthenticationRequest resultRequest = emailCodeEvent.getLoginRequest(client, PHONE_NUMBER_WITH_CODE);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(authRequest));
    }

    @Test
    public void shouldGetValidLoginRequestWhenUsingSMSAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithPhoneNumber(PHONE_NUMBER_WITH_CODE, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.SMS_LINK, CODE);
        AuthenticationRequest resultRequest = emailCodeEvent.getLoginRequest(client, PHONE_NUMBER_WITH_CODE);

        Assert.assertThat(resultRequest, notNullValue());
        Assert.assertThat(resultRequest, equalTo(authRequest));
    }

    @Test
    public void shouldCallApiClientPasswordlessLoginWhenUsingEmailAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithEmail(EMAIL, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.EMAIL_CODE, CODE);
        emailCodeEvent.getLoginRequest(client, EMAIL);

        verify(client).loginWithEmail(EMAIL, CODE);
        verify(client, never()).getProfileAfter(authRequest);
    }

    @Test
    public void shouldCallApiClientPasswordlessLoginWhenUsingEmailAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithEmail(EMAIL, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.EMAIL_LINK, CODE);
        emailCodeEvent.getLoginRequest(client, EMAIL);

        verify(client).loginWithEmail(EMAIL, CODE);
        verify(client, never()).getProfileAfter(authRequest);
    }

    @Test
    public void shouldCallApiClientPasswordlessLoginWhenUsingSMSAndCode() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithPhoneNumber(PHONE_NUMBER_WITH_CODE, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.SMS_CODE, CODE);
        emailCodeEvent.getLoginRequest(client, PHONE_NUMBER_WITH_CODE);

        verify(client).loginWithPhoneNumber(PHONE_NUMBER_WITH_CODE, CODE);
        verify(client, never()).getProfileAfter(authRequest);
    }

    @Test
    public void shouldCallApiClientPasswordlessLoginWhenUsingSMSAndLink() throws Exception {
        AuthenticationAPIClient client = mock(AuthenticationAPIClient.class);
        AuthenticationRequest authRequest = mock(AuthenticationRequest.class);
        when(client.loginWithPhoneNumber(PHONE_NUMBER_WITH_CODE, CODE)).thenReturn(authRequest);

        PasswordlessLoginEvent emailCodeEvent = PasswordlessLoginEvent.submitCode(PasswordlessMode.SMS_LINK, CODE);
        emailCodeEvent.getLoginRequest(client, PHONE_NUMBER_WITH_CODE);

        verify(client).loginWithPhoneNumber(PHONE_NUMBER_WITH_CODE, CODE);
        verify(client, never()).getProfileAfter(authRequest);
    }
}