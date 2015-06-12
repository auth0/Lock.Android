/*
 * AuthenticationReceiverTest.java
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

package com.auth0.lock.receiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.BuildConfig;
import com.auth0.lock.Lock;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class AuthenticationReceiverTest {

    private AuthenticationReceiver receiver;

    @Mock private Intent intent;
    @Mock private Context context;
    @Mock private UserProfile profile;
    @Mock private Token token;
    @Mock private LocalBroadcastManager manager;

    @Captor private ArgumentCaptor<IntentFilter> captor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        receiver = spy(new AuthenticationReceiver() {
            @Override
            public void onAuthentication(@NonNull UserProfile profile, @NonNull Token token) {
            }
        });
    }

    @Test
    public void shouldHandleAuthenticationAction() throws Exception {
        when(intent.getAction()).thenReturn(Lock.AUTHENTICATION_ACTION);
        when(intent.getParcelableExtra(eq(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER))).thenReturn(token);
        when(intent.getParcelableExtra(eq(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER))).thenReturn(profile);

        receiver.onReceive(context, intent);

        verify(receiver).onAuthentication(eq(profile), eq(token));
        verifyNoMoreInteractions(receiver);
    }

    @Test
    public void shouldHandleAuthenticationActionForSignUp() throws Exception {
        when(intent.getAction()).thenReturn(Lock.AUTHENTICATION_ACTION);
        when(intent.getParcelableExtra(eq(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER))).thenReturn(null);
        when(intent.getParcelableExtra(eq(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER))).thenReturn(null);

        receiver.onReceive(context, intent);

        verify(receiver).onSignUp();
        verifyNoMoreInteractions(receiver);
    }

    @Test
    public void shouldHandleCancelAction() throws Exception {
        when(intent.getAction()).thenReturn(Lock.CANCEL_ACTION);

        receiver.onReceive(context, intent);

        verify(receiver).onCancel();
        verifyNoMoreInteractions(receiver);
    }

    @Test
    public void shouldHandleChangePasswordAction() throws Exception {
        when(intent.getAction()).thenReturn(Lock.CHANGE_PASSWORD_ACTION);

        receiver.onReceive(context, intent);

        verify(receiver).onChangePassword();
        verifyNoMoreInteractions(receiver);
    }

    @Test
    public void shouldIgnoreInvalidAction() throws Exception {
        when(intent.getAction()).thenReturn("A STRANGE ACTION RECEIVED");

        receiver.onReceive(context, intent);

        verifyZeroInteractions(receiver);
    }

    @Test
    public void shouldRegisterInBroadcastManager() throws Exception {
        receiver.registerIn(manager);

        verify(manager).registerReceiver(eq(receiver), captor.capture());
        final IntentFilter intentFilter = captor.getValue();
        assertThat(intentFilter.hasAction(Lock.AUTHENTICATION_ACTION), is(true));
        assertThat(intentFilter.hasAction(Lock.CANCEL_ACTION), is(true));
        assertThat(intentFilter.hasAction(Lock.CHANGE_PASSWORD_ACTION), is(true));
    }

    @Test
    public void shouldUnregisterBroadcastManager() throws Exception {
        receiver.unregisterFrom(manager);

        verify(manager).unregisterReceiver(eq(receiver));
    }
}