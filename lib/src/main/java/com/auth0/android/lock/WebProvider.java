package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;

import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthProvider;

/**
 * The WebProvider class is a wrapper for calls to the WebAuthProvider static methods.
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
     * @param activity    a valid Activity context
     * @param connection  to use in the authentication
     * @param callback    to deliver the authentication result to
     * @param requestCode to use in the startActivityForResult request
     */
    public void start(Activity activity, String connection, AuthCallback callback, int requestCode) {
        WebAuthProvider.Builder builder = WebAuthProvider.init(options.getAccount())
                .useBrowser(options.useBrowser())
                .withParameters(options.getAuthenticationParameters())
                .withConnection(connection);

        final String connectionScope = options.getConnectionsScope().get(connection);
        if (connectionScope != null) {
            builder.withConnectionScope(connectionScope);
        }
        final String scope = options.getScope();
        if (scope != null) {
            builder.withScope(scope);
        }
        if (options.isLoggingEnabled()) {
            builder.enableLogging();
        }
        builder.start(activity, callback, requestCode);
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

    /**
     * Finishes the authentication flow in the WebAuthProvider
     *
     * @param requestCode the request code received on the onActivityResult method
     * @param resultCode  the result code received on the onActivityResult method
     * @param intent      the intent received in the onActivityResult method.
     * @return true if a result was expected and has a valid format, or false if not
     */
    public boolean resume(int requestCode, int resultCode, Intent intent) {
        return WebAuthProvider.resume(requestCode, resultCode, intent);
    }

}
