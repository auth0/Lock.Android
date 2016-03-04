package com.auth0.identity.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.auth0.identity.AuthorizedIdentityProvider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PermissionHandler implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQ_CODE = 211;

    private final Activity context;
    private final AuthorizedIdentityProvider callback;

    public PermissionHandler(@NonNull Activity context, @NonNull AuthorizedIdentityProvider callback) {
        this.context = context;
        this.callback = callback;
    }

    /**
     * Checks if the given permission has been granted by the user to this application before.
     *
     * @param permission to check availability for
     * @return true if the permission is currently granted, false otherwise.
     */
    public boolean isPermissionGranted(@NonNull String permission) {
        int result = ContextCompat.checkSelfPermission(context, permission);
        return result == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * Checks if the given permissions have been granted by the user to this application before.
     *
     * @param permissions to check availability for
     * @return true if all the permissions are currently granted, false otherwise.
     */
    public boolean areAllPermissionsGranted(@NonNull String[] permissions) {
        for (String p : permissions) {
            if (!isPermissionGranted(p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starts the async request of the given permission.
     *
     * @param permissions     to request to the user
     * @param explainIfNeeded the reason why we need the permission
     */
    public void requestPermissions(@NonNull String[] permissions, boolean explainIfNeeded) {
        if (explainIfNeeded) {
            for (String p : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(context, p)) {
                    callback.onPermissionRequireExplanation(p);
                    return;
                }
            }
        }

        ActivityCompat.requestPermissions(context,
                permissions,
                PERMISSION_REQ_CODE);
    }

    /**
     * Called when there is a new response for a Permission request
     *
     * @param requestCode  received
     * @param permissions  the permissions that were requested
     * @param grantResults the grant result for each permission
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQ_CODE) {
            return;
        } else if (permissions.length == 0 && grantResults.length == 0) {
            callback.onPermissionsResult(Collections.<String>emptyList(), Arrays.asList(permissions));
            return;
        }
        List<String> grantedPermissions = new ArrayList<>();
        List<String> declinedPermissions = new ArrayList<>();

        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PermissionChecker.PERMISSION_GRANTED) {
                grantedPermissions.add(permissions[i]);
            } else {
                declinedPermissions.add(permissions[i]);
            }
        }
        callback.onPermissionsResult(grantedPermissions, declinedPermissions);
    }
}