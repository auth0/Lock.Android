package com.auth0.android.lock;

import com.auth0.Auth0Exception;
import com.auth0.authentication.result.Authentication;

/**
 * Created by nikolaseu on 1/21/16.
 */
public interface AuthenticationCallback {
    void onAuthentication(Authentication authentication);

    void onCanceled();

    void onError(Auth0Exception error);
}
