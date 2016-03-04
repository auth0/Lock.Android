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

package com.auth0.identity;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.auth0.identity.util.PermissionCallback;
import com.auth0.identity.util.PermissionHandler;

/**
 * Interface for allowing an IdentityProvider to request specific permissions
 * before calling start.
 */
public abstract class AuthorizedIdentityProvider implements IdentityProvider, PermissionCallback {

    protected IdentityProvider identityProvider;
    private Activity activity;
    private String serviceName;

    public AuthorizedIdentityProvider(@NonNull IdentityProvider provider) {
        this.identityProvider = provider;
    }

    @Override
    public void setCallback(IdentityProviderCallback callback) {
        identityProvider.setCallback(callback);
    }

    @Override
    public void start(Activity activity, @NonNull String connectionName) {
        checkPermissions(activity, connectionName);
    }

    @Override
    public void stop() {
        identityProvider.stop();
    }

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        return identityProvider.authorize(activity, requestCode, resultCode, data);
    }

    @Override
    public void clearSession() {
        identityProvider.clearSession();
    }

    /**
     * Defines which permissions are required by this Identity Provider to work
     *
     * @return the required permissions
     */
    abstract String[] getRequiredPermissions();

    /**
     * Checks if the required permissions have been granted by the user and starts the authentication process
     *
     * @param activity    activity that starts the process (and will receive its response)
     * @param serviceName of the IdentityProvider to authenticate with
     */
    private void checkPermissions(Activity activity, @NonNull String serviceName) {
        String[] permissions = getRequiredPermissions();
        if (permissions.length == 0) {
            identityProvider.start(activity, serviceName);
            return;
        }
        PermissionHandler handler = new PermissionHandler(activity, this);
        if (handler.areAllPermissionsGranted(permissions)) {
            identityProvider.start(activity, serviceName);
        } else {
            this.activity = activity;
            this.serviceName = serviceName;
            handler.requestPermissions(permissions);
        }
    }

    @Override
    public void onPermissionsResult(String[] permissionsGranted, String[] permissionsDeclined) {
        if (permissionsDeclined.length == 0 && activity != null && serviceName != null) {
            //continue with the Authentication flow
            identityProvider.start(activity, serviceName);
        }
    }
}