/*
 * AuthorizedIdentityProvider.java
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
import android.support.annotation.NonNull;

import java.util.List;

public class IdentityProviderDelegator {

    private final IdentityProvider provider;
    private final PermissionHandler handler;

    public IdentityProviderDelegator(@NonNull IdentityProvider provider) {
        this(provider, new PermissionHandler());
    }

    IdentityProviderDelegator(@NonNull IdentityProvider provider, PermissionHandler handler) {
        this.provider = provider;
        this.handler = handler;
    }

    /**
     * Starts the authentication flow on the Identity Provider for the given connection name.
     * Before calling this method, make sure you have been granted all the required Android
     * Manifest.permissions, as stated by the Identity Provider to be used.
     *
     * @param activity       a valid activity context.
     * @param connectionName the connection name to use.
     */
    public void start(Activity activity, String connectionName) {
        provider.start(activity, connectionName);
    }

    /**
     * Finishes the authentication flow by giving the AuthorizeResult to the Identity Provider
     * to parse it.
     *
     * @param activity a valid activity context.
     * @param result   the result received in the activity.
     * @return if the result is valid or not. Please not, this does not means that the
     * user is authenticated.
     */
    public boolean authorize(Activity activity, @NonNull AuthorizeResult result) {
        return provider.authorize(activity, result);
    }

    /**
     * Checks if all the Android Manifest.permissions for the IdentityProvider have already been
     * granted.
     *
     * @param activity a valid activity context. It will receive the permissions
     *                 request result on the onRequestPermissionsResult method.
     * @return true if all the requested permissions are already granted, false otherwise.
     */
    public boolean checkPermissions(Activity activity) {
        String[] permissions = provider.getRequiredAndroidPermissions();
        return handler.areAllPermissionsGranted(activity, permissions);
    }

    /**
     * Starts a Permission Request. The caller activity will be notified of the result on the
     * onRequestPermissionsResult method.
     *
     * @param activity a valid activity context. It will receive the permissions
     *                 request result on the onRequestPermissionsResult method.
     */
    public void requestPermissions(Activity activity, int requestCode) {
        String[] permissions = provider.getRequiredAndroidPermissions();
        handler.requestPermissions(activity, permissions, requestCode);
    }

    /**
     * Should be called from the activity that initiated the Android Manifest.permission request,
     * when the method #onRequestPermissionResult is called on that activity.
     *
     * @param requestCode  the request code
     * @param permissions  the requested permissions
     * @param grantResults the grant results for each permission
     * @return true if all the requested permissions are already granted, false otherwise.
     */
    public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        List<String> declinedPermissions = handler.parseRequestResult(requestCode, permissions, grantResults);
        if (!declinedPermissions.isEmpty()) {
            provider.onAndroidPermissionsRequireExplanation(declinedPermissions);
            return false;
        }
        return true;
    }
}
