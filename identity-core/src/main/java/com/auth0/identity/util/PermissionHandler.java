package com.auth0.identity.util;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PermissionHandler {

    private static final int PERMISSION_REQ_CODE = 211;

    public PermissionHandler() {
    }

    /**
     * Checks if the given Android Manifest Permission has been granted by the user to this application before.
     *
     * @param activity   the caller activity
     * @param permission to check availability for
     * @return true if the Android Manifest Permission is currently granted, false otherwise.
     */
    public boolean isPermissionGranted(@NonNull Activity activity, @NonNull String permission) {
        int result = ContextCompat.checkSelfPermission(activity, permission);
        return result == PermissionChecker.PERMISSION_GRANTED;
    }

    /**
     * Checks if the given Android Manifest Permissions have been granted by the user to this application before.
     *
     * @param activity    the caller activity
     * @param permissions to check availability for
     * @return true if all the Android Manifest Permissions are currently granted, false otherwise.
     */
    public boolean areAllPermissionsGranted(@NonNull Activity activity, @NonNull String[] permissions) {
        for (String p : permissions) {
            if (!isPermissionGranted(activity, p)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starts the request of the given Android Manifest Permissions.
     *
     * @param activity              the caller activity
     * @param permissions           to request to the user
     * @param shouldExplainIfNeeded the reason why we need the Android Manifest Permissions
     * @return the Android Manifest Permissions that were previously declined by the user and may
     * now require an usage explanation
     */
    public List<String> requestPermissions(@NonNull Activity activity, @NonNull String[] permissions, boolean shouldExplainIfNeeded) {
        List<String> permissionsToExplain = new ArrayList<>();
        if (shouldExplainIfNeeded) {
            for (String p : permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, p)) {
                    permissionsToExplain.add(p);
                }
            }
            if (!permissionsToExplain.isEmpty()) {
                return permissionsToExplain;
            }
        }


        ActivityCompat.requestPermissions(activity,
                permissions,
                PERMISSION_REQ_CODE);
        return permissionsToExplain;
    }

    /**
     * Called when there is a new response for a Android Manifest Permission request
     *
     * @param requestCode  received.
     * @param permissions  the Android Manifest Permissions that were requested
     * @param grantResults the grant result for each permission
     * @return the list of Android Manifest Permissions that were declined by the user.
     */
    public List<String> parseRequestResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != PERMISSION_REQ_CODE) {
            return Arrays.asList(permissions);
        } else if (permissions.length == 0 && grantResults.length == 0) {
            return Arrays.asList(permissions);
        }

        List<String> declinedPermissions = new ArrayList<>();
        for (int i = 0; i < permissions.length; i++) {
            if (grantResults[i] != PermissionChecker.PERMISSION_GRANTED) {
                declinedPermissions.add(permissions[i]);
            }
        }
        return declinedPermissions;
    }
}