package com.auth0.android.lock;

/**
 * Created by nikolaseu on 1/21/16.
 */
public interface AuthenticationCallback {
    void onAuthentication(Authentication authentication);
    void onCancelled();
    void onError(Auth0Exception error);
}
