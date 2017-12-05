package com.auth0.android.lock.views.next.configuration;

import android.support.annotation.NonNull;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public interface ConnectionResolver {

    /**
     * Resolves a connection name for a given identity.
     *
     * @param identity the email or username to resolve the connection for
     * @return the name of the connection to use
     */
    @NonNull
    String resolveFor(@NonNull String identity);
}
