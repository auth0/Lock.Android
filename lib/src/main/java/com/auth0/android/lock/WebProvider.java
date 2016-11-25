package com.auth0.android.lock;

import android.content.Intent;
import android.support.annotation.NonNull;

import com.auth0.android.Auth0;
import com.auth0.android.provider.WebAuthProvider;

/**
 * The WebProvider class is a wrapper for calls to the WebAuthProvider static methods.
 * This is only for testing purposes.
 */
class WebProvider {

    private final Auth0 account;

    /**
     * Creates a new instance with the given account.
     *
     * @param account to use in the WebAuthProvider.Builder instances.
     */
    WebProvider(@NonNull Auth0 account) {
        this.account = account;
    }

    /**
     * Creates a new instance of the WebAuthProvider.Builder with a valid account.
     *
     * @return a WebAuthProvider.Builder ready to customize.
     */
    public WebAuthProvider.Builder init() {
        return WebAuthProvider.init(account);
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
