package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.provider.CustomTabsOptions;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import java.util.HashMap;
import java.util.Map;

/**
 * The WebProvider class is an internal wrapper for calls to the WebAuthProvider static methods.
 * This is only for testing purposes.
 */
class WebProvider {

    private final Options options;

    /**
     * Creates a new instance with the given account.
     *
     * @param options to use in the WebAuthProvider.Builder instances.
     */
    WebProvider(@NonNull Options options) {
        this.options = options;
    }

    /**
     * Configures a new instance of the WebAuthProvider.Builder and starts it.
     *
     * @param activity            a valid Activity context
     * @param connection          to use in the authentication
     * @param extraAuthParameters extra authentication parameters to use along with the provided in the Options instance
     * @param callback            to deliver the authentication result to
     */
    public void start(@NonNull Activity activity, @NonNull String connection, @Nullable Map<String, String> extraAuthParameters, @NonNull Callback<Credentials, AuthenticationException> callback) {
        HashMap<String, String> parameters;
        if (extraAuthParameters == null) {
            parameters = options.getAuthenticationParameters();
        } else {
            parameters = new HashMap<>(options.getAuthenticationParameters());
            parameters.putAll(extraAuthParameters);
        }

        WebAuthProvider.Builder builder = WebAuthProvider.login(options.getAccount())
                .withParameters(parameters)
                .withConnection(connection);

        final String connectionScope = options.getConnectionsScope().get(connection);
        if (connectionScope != null) {
            builder.withConnectionScope(connectionScope);
        }
        final String scope = options.getScope();
        if (scope != null) {
            builder.withScope(scope);
        }
        final String audience = options.getAudience();
        if (audience != null) {
            builder.withAudience(audience);
        }
        final String scheme = options.getScheme();
        if (scheme != null) {
            builder.withScheme(scheme);
        }
        final CustomTabsOptions customTabsOptions = options.getCustomTabsOptions();
        if (customTabsOptions != null) {
            builder.withCustomTabsOptions(customTabsOptions);
        }
        builder.start(activity, callback);
    }

    /**
     * Finishes the authentication flow in the WebAuthProvider
     *
     * @param intent the intent received in the onNewIntent method.
     * @return true if a result was expected and has a valid format, or false if not
     */
    public boolean resume(Intent intent) {
        return WebAuthProvider.resume(intent);
    }

}
