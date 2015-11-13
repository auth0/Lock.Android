package com.auth0.identity.web;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class LinkParser {
    private static String TAG = LinkParser.class.getName();

    public String getCodeFromAppLinkIntent(Intent intent) {
        if (Intent.ACTION_VIEW.equals(intent.getAction())) {
            String dataString = intent.getDataString();
            String code = getCodeFromAppLinkUri(dataString);
            if (code != null) {
                return code;
            }
        }

        Log.d(TAG, "Invalid app link intent: " + intent);
        return null;
    }

    public String getCodeFromAppLinkUri(String uriString) {
        if (uriString == null) {
            return null;
        }

        Uri uri = Uri.parse(uriString);
        return uri.getQueryParameter("code");
    }
}
