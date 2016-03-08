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
import com.auth0.identity.util.PermissionHandler;

import java.util.List;

/**
 * This class allows an IdentityProvider to request specific Android Manifest Permissions
 * before calling start. This provider handles the Runtime Permissions introduced by Android M.
 * <p/>
 * If the device is running Android 5.1 or lower, or your app's target SDK is 22 or lower,
 * then the user must grant all the required permissions when they install the app, so this provider
 * will fallback to work as a simple IdentityProvider.
 * If the device is running Android 6.0 or higher, and your app's target SDK is 23 or higher,
 * the app must request the user each required dangerous permission before trying to use it.
 * <p/>
 * For more information about normal and dangerous permissions, along with Android M Runtime
 * Permissions, check http://developer.android.com/training/permissions/requesting.html
 */
public abstract class AuthorizedIdentityProvider implements IdentityProvider {

    protected IdentityProvider identityProvider;
    private Activity activity;
    private String connectionName;
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
        activity = null;
        connectionName = null;
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
     * Defines which Android Manifest Permissions are required by this Identity Provider to work.
     * ex: Manifest.permission.GET_ACCOUNTS
     *
     * @return the required Android Manifest.permissions
     */
    public abstract String[] getRequiredPermissions();

    /**
     * Called when one or more Android Manifest Permissions has been declined but are needed
     * for this Identity Provider to work. You are supposed to show the user a Dialog explaining
     * why you need the permissions and how you are going to use them, and offer an option to
     * retry the recent request by calling retryLastPermissionRequest()
     *
     * @param permissions the required Android Manifest.permissions that were declined.
     */
    public abstract void onPermissionsRequireExplanation(List<String> permissions);

    /**
     * Retries the last Android Manifest Permissions request issued to the user.
     * This is useful when a Dialog is shown to explain the need for permissions to the user,
     * with an option to retry the request.
     * <p/>
     * If there is no recent request, this method will do nothing.
     */
    @SuppressWarnings("unused")
    public void retryLastPermissionRequest() {
        if (activity == null || connectionName == null) {
            return;
        }
        checkPermissions(activity, connectionName, false);
    }

    /**
     * Should be called from the activity that initiated the Android Manifest.permission request,
     * when the method #onRequestPermissionResult is called on that activity.
     *
     * @param requestCode  the request code
     * @param permissions  the requested permissions
     * @param grantResults the grant results for each permission
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (handler != null) {
            List<String> declinedPermissions = handler.parseRequestResult(requestCode, permissions, grantResults);
            if (declinedPermissions.isEmpty()) {
                identityProvider.start(activity, connectionName);
            } else {
                onPermissionsRequireExplanation(declinedPermissions);
            }
        }
    }

    private void checkPermissions(Activity activity, @NonNull String connectionName, boolean shouldExplainIfNeeded) {
        String[] permissions = getRequiredPermissions();
        if (permissions.length == 0) {
            identityProvider.start(activity, connectionName);
            return;
        }
        handler = new PermissionHandler();
        if (handler.areAllPermissionsGranted(activity, permissions)) {
            identityProvider.start(activity, connectionName);
        } else {
            this.activity = activity;
            this.connectionName = connectionName;
            handler.requestPermissions(activity, permissions, shouldExplainIfNeeded);
        }
    }
}