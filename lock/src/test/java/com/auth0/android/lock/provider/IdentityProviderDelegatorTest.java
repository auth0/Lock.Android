/*
 * IdentityProviderDelegatorTest.java
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

package com.auth0.android.lock.provider;

import android.app.Activity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class IdentityProviderDelegatorTest {

    @Mock
    private IdentityProvider provider;
    @Mock
    private PermissionHandler handler;
    @Mock
    private Activity activity;

    private IdentityProviderDelegator delegator;
    private static final String CONNECTION_NAME = "connectionName";
    private static final String[] PROVIDER_PERMISSIONS = new String[]{"PermissionX", "PermissionY"};

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        delegator = new IdentityProviderDelegator(provider, handler);
    }

    @Test
    public void shouldCallStartOnTheProvider() throws Exception {
        delegator.start(activity, CONNECTION_NAME);

        Mockito.verify(provider).start(activity, CONNECTION_NAME);
    }

    @Test
    public void shouldCallAuthorizeOnTheProvider() throws Exception {
        AuthorizeResult result = new AuthorizeResult(null);
        delegator.authorize(activity, result);

        Mockito.verify(provider).authorize(activity, result);
    }

    @Test
    public void shouldCallCheckPermissionsOnHandler() throws Exception {
        Mockito.when(provider.getRequiredAndroidPermissions()).thenReturn(PROVIDER_PERMISSIONS);
        delegator.checkPermissions(activity);

        Mockito.verify(handler).areAllPermissionsGranted(activity, PROVIDER_PERMISSIONS);
    }

    @Test
    public void shouldCallRequestPermissionsOnHandler() throws Exception {
        Mockito.when(provider.getRequiredAndroidPermissions()).thenReturn(PROVIDER_PERMISSIONS);
        delegator.requestPermissions(activity, 1);

        Mockito.verify(handler).requestPermissions(activity, PROVIDER_PERMISSIONS, 1);
    }

    @Test
    public void shouldDeliverOnRequestPermissionsResultToHandler() throws Exception {
        Mockito.when(provider.getRequiredAndroidPermissions()).thenReturn(PROVIDER_PERMISSIONS);
        delegator.onRequestPermissionsResult(1, PROVIDER_PERMISSIONS, new int[2]);

        Mockito.verify(handler).parseRequestResult(1, PROVIDER_PERMISSIONS, new int[2]);
    }
}