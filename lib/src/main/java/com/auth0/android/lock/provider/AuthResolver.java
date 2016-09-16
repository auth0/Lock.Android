package com.auth0.android.lock.provider;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.auth0.android.provider.AuthHandler;
import com.auth0.android.provider.AuthProvider;

import java.util.ArrayList;
import java.util.List;


/**
 * Holds instances of AuthHandlers that can be used to query for AuthProviders given
 * a strategy and connection name.
 * When no AuthProvider is matched, it will return null
 */
public final class AuthResolver {
    private static List<AuthHandler> authHandlers;

    private AuthResolver() {
    }

    /**
     * Sets the AuthHandler list to use on this instance.
     *
     * @param handlers the list of AuthHandlers to use.
     */
    public static void setAuthHandlers(@NonNull List<AuthHandler> handlers) {
        authHandlers = new ArrayList<>(handlers);
    }

    /**
     * Get an AuthProvider that can handle a given strategy and connection name, or null if there are no
     * providers to handle them.
     *
     * @param strategy   to handle
     * @param connection to handle
     * @return an AuthProvider to handle the authentication or null if no providers are available.
     */
    @Nullable
    public static AuthProvider providerFor(@Nullable String strategy, @NonNull String connection) {
        if (authHandlers == null) {
            return null;
        }

        AuthProvider provider = null;
        for (AuthHandler p : authHandlers) {
            provider = p.providerFor(strategy, connection);
            if (provider != null) {
                break;
            }
        }
        return provider;
    }
}
