package com.auth0.android.lock.net;

import android.net.Uri;

import java.util.HashMap;
import java.util.Map;

public class CallbackParser {

    public Map<String, String> getValuesFromUri(Uri uri) {
        return asMap(uri.getQuery() != null ? uri.getQuery() : uri.getFragment());
    }

    private Map<String, String> asMap(String valueString) {
        final String[] entries = valueString != null && valueString.length() > 0 ? valueString.split("&") : new String[]{};
        Map<String, String> values = new HashMap<>(entries.length);
        for (String entry : entries) {
            final String[] value = entry.split("=");
            if (value.length == 2) {
                values.put(value[0], value[1]);
            }
        }
        return values;
    }
}