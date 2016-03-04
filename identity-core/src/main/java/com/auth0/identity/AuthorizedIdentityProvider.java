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

import com.auth0.core.Application;
import com.auth0.identity.util.PermissionCallback;
import com.auth0.identity.util.PermissionHandler;

import java.util.List;

/**
 * Interface for allowing an IdentityProvider to request specific permissions
 * before calling start.
 */
public abstract class AuthorizedIdentityProvider implements IdentityProvider, PermissionCallback {

    protected IdentityProvider identityProvider;
    private Activity activity;
    private String serviceName;
    private PermissionHandler handler;

    public AuthorizedIdentityProvider(@NonNull IdentityProvider provider) {
        this.identityProvider = provider;
    }

    @Override
    public void setCallback(IdentityProviderCallback callback) {
        identityProvider.setCallback(callback);
    }

    @Override
    public void start(Activity activity, IdentityProviderRequest request, Application application) {
        checkPermissions(activity, request.getServiceName(), true);
    }

    @Override
    public void start(Activity activity, @NonNull String connectionName) {
        checkPermissions(activity, connectionName, true);
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
    public abstract String[] getRequiredPermissions();

    /**
     * Retry the last permission request issued to the user. If there is no recent
     * request, this method will do nothing.
     */
    public void retryLastPermissionRequest() {
        if (activity == null || serviceName == null) {
            return;
        }
        checkPermissions(activity, serviceName, false);
    }

    /**
     * Checks if the required permissions have been granted by the user and starts the authentication process
     *
     * @param activity        activity that starts the process (and will receive its response)
     * @param serviceName     of the IdentityProvider to authenticate with
     * @param explainIfNeeded the reason why we need the permission
     */
    private void checkPermissions(Activity activity, @NonNull String serviceName, boolean explainIfNeeded) {
        String[] permissions = getRequiredPermissions();
        if (permissions.length == 0) {
            identityProvider.start(activity, serviceName);
            return;
        }
        handler = new PermissionHandler(activity, this);
        if (handler.areAllPermissionsGranted(permissions)) {
            identityProvider.start(activity, serviceName);
        } else {
            this.activity = activity;
            this.serviceName = serviceName;
            handler.requestPermissions(permissions, explainIfNeeded);
        }
    }

    /**
     * Should be called from the activity that initiated the permission request,
     * when the method #onRequestPermissionResult is called on that activity.
     *
     * @param requestCode  the permission request code
     * @param permissions  the permissions requested
     * @param grantResults the grant results for each permission
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (handler != null) {
            handler.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onPermissionsResult(List<String> permissionsGranted, List<String> permissionsDeclined) {
        if (permissionsDeclined.isEmpty() && activity != null && serviceName != null) {
            //continue with the Authentication flow
            identityProvider.start(activity, serviceName);
        } else {
            onPermissionRequireExplanation(permissionsDeclined.get(0));
        }
    }
}