package com.auth0.android.lock.provider;

import android.net.Uri;
import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class CallbackHelper {

    private static final String REDIRECT_URI_FORMAT = "%s/android/%s/callback";
    private final String packageName;

    public CallbackHelper(@NonNull String packageName) {
        this.packageName = packageName;
    }

    /**
     * Generates the callback URI for the given domain.
     *
     * @return the callback URI.
     */
    public String getCallbackURI(@NonNull String domain) {
        return String.format(REDIRECT_URI_FORMAT, domain, packageName);
    }

    public Map<String, String> getValuesFromUri(@NonNull Uri uri) {
        return asMap(uri.getQuery() != null ? uri.getQuery() : uri.getFragment());
    }

    private Map<String, String> asMap(@NonNull String valueString) {
        final String[] entries = valueString.length() > 0 ? valueString.split("&") : new String[]{};
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