package com.auth0.identity.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import com.auth0.identity.AuthorizedIdentityProvider;

public class PermissionHandler implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQ_CODE = 324;

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
     * @param permissions to request to the user
     */
    public void requestPermissions(@NonNull String[] permissions) {
        boolean explanationRequired = false;
        for (String p : permissions) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(context, p)) {
                explanationRequired = true;
                callback.onPermissionRequireExplanation(p);
            }
        }
        if (explanationRequired) {
            return;
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
            callback.onPermissionsResult(new String[]{}, permissions);
            return;
        }
        int grantedIndex = 0;
        int declinedIndex = 0;
        String[] permissionsGranted = new String[]{};
        String[] permissionsDeclined = new String[]{};
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] == PermissionChecker.PERMISSION_GRANTED) {
                permissionsGranted[grantedIndex] = permissions[i];
                grantedIndex++;
            } else {
                permissionsDeclined[declinedIndex] = permissions[i];
                declinedIndex++;
            }
        }
        callback.onPermissionsResult(permissionsGranted, permissionsDeclined);
    }
}