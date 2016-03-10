/*
 * AuthorizedIdentityProviderTest.java
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

package com.auth0.identity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.PermissionChecker;

import com.auth0.identity.util.PermissionHandler;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class AuthorizedIdentityProviderTest {

    private static final int REQUEST_CODE = 200;
    @Mock
    private IdentityProvider provider;
    @Mock
    private PermissionHandler handler;

    private AuthorizedIdentityProvider authorizedProvider;
    private static final String CONNECTION_NAME = "connection_name";
    public static final String[] REQUIRED_PERMISSIONS = new String[]{"Required", "Permission"};
    public static final int[] PERMISSIONS_GRANTED = new int[]{PermissionChecker.PERMISSION_GRANTED, PermissionChecker.PERMISSION_GRANTED};
    public static final int[] PERMISSIONS_DECLINED = new int[]{PermissionChecker.PERMISSION_DENIED, PermissionChecker.PERMISSION_DENIED};


    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        authorizedProvider = new AuthorizedIdentityProvider(provider, handler) {
            @Override
            public String[] getRequiredPermissions() {
                return REQUIRED_PERMISSIONS;
            }

            @Override
            public void onPermissionsRequireExplanation(List<String> permissions) {
                //Do nothing
            }
        };
    }

    @Test
    public void shouldSetCallbackOnBaseProvider() throws Exception {
        IdentityProviderCallback callback = Mockito.mock(IdentityProviderCallback.class);
        authorizedProvider.setCallback(callback);
        Mockito.verify(this.provider).setCallback(callback);
    }

    @Test
    public void shouldStopBaseProvider() throws Exception {
        authorizedProvider.stop();
        Mockito.verify(this.provider).stop();
    }

    @Test
    public void shouldClearSessionOnBaseProvider() throws Exception {
        authorizedProvider.clearSession();
        Mockito.verify(this.provider).clearSession();
    }

    @Test
    public void shouldAuthorizeOnBaseProvider() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Intent data = Mockito.mock(Intent.class);
        authorizedProvider.authorize(activity, 1, 2, data);
        Mockito.verify(this.provider).authorize(activity, 1, 2, data);
    }

    @Test
    public void shouldStartBaseProviderOnGrantedPermissions() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(handler.areAllPermissionsGranted(activity, REQUIRED_PERMISSIONS)).thenReturn(true);
        IdentityProviderRequest request = Mockito.mock(IdentityProviderRequest.class);
        Mockito.when(request.getServiceName()).thenReturn(CONNECTION_NAME);

        authorizedProvider.start(activity, CONNECTION_NAME);
        authorizedProvider.start(activity, request, null);

        Mockito.verify(this.provider, VerificationModeFactory.times(2)).start(activity, CONNECTION_NAME);
    }

    @Test
    public void shouldStartBaseProviderOnGrantedPermissionsRequestPermissionsResult() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(handler.areAllPermissionsGranted(activity, REQUIRED_PERMISSIONS)).thenReturn(false);
        Mockito.when(handler.parseRequestResult(REQUEST_CODE, REQUIRED_PERMISSIONS, PERMISSIONS_GRANTED)).thenReturn(Collections.<String>emptyList());
        authorizedProvider.start(activity, CONNECTION_NAME);
        authorizedProvider.onRequestPermissionsResult(221, REQUIRED_PERMISSIONS, PERMISSIONS_GRANTED);

        Mockito.verify(this.provider).start(activity, CONNECTION_NAME);
    }

    @Test
    public void shouldNotStartBaseProviderOnDeclinedPermissions() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(handler.areAllPermissionsGranted(activity, REQUIRED_PERMISSIONS)).thenReturn(false);
        IdentityProviderRequest request = Mockito.mock(IdentityProviderRequest.class);
        Mockito.when(request.getServiceName()).thenReturn(CONNECTION_NAME);

        authorizedProvider.start(activity, CONNECTION_NAME);
        authorizedProvider.start(activity, request, null);

        Mockito.verify(this.provider, VerificationModeFactory.times(0)).start(activity, CONNECTION_NAME);
    }

    @Test
    public void shouldNotStartBaseProviderOnDeclinedPermissionsRequestPermissionsResult() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(handler.areAllPermissionsGranted(activity, REQUIRED_PERMISSIONS)).thenReturn(false);
        Mockito.when(handler.parseRequestResult(REQUEST_CODE, REQUIRED_PERMISSIONS, PERMISSIONS_DECLINED)).thenReturn(Arrays.asList(REQUIRED_PERMISSIONS));
        authorizedProvider.start(activity, CONNECTION_NAME);
        authorizedProvider.onRequestPermissionsResult(REQUEST_CODE, REQUIRED_PERMISSIONS, PERMISSIONS_DECLINED);

        Mockito.verify(this.provider, VerificationModeFactory.times(0)).start(activity, CONNECTION_NAME);
    }

    @Test
    public void shouldStartPermissionRequestWithExplanationOnDeclinedPermissions() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(handler.areAllPermissionsGranted(activity, REQUIRED_PERMISSIONS)).thenReturn(false);
        IdentityProviderRequest request = Mockito.mock(IdentityProviderRequest.class);
        Mockito.when(request.getServiceName()).thenReturn(CONNECTION_NAME);

        authorizedProvider.start(activity, CONNECTION_NAME);
        authorizedProvider.start(activity, request, null);

        Mockito.verify(this.handler, VerificationModeFactory.times(2)).requestPermissions(activity, REQUIRED_PERMISSIONS, true);
    }

    @Test
    public void shouldRetryPermissionRequestWithoutExplanation() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Mockito.when(handler.areAllPermissionsGranted(activity, REQUIRED_PERMISSIONS)).thenReturn(false);
        authorizedProvider.start(activity, CONNECTION_NAME);
        authorizedProvider.retryLastPermissionRequest();

        Mockito.verify(this.handler).requestPermissions(activity, REQUIRED_PERMISSIONS, true);
        Mockito.verify(this.handler).requestPermissions(activity, REQUIRED_PERMISSIONS, false);
    }

}