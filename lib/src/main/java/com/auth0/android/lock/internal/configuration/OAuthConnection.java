package com.auth0.android.lock.internal.configuration;

import android.support.annotation.NonNull;

import java.util.Set;

public interface OAuthConnection extends BaseConnection {

    /**
     * Returns if this Connection can use Resource Owner to authenticate
     *
     * @return if the Active Flow (Resource Owner) is enabled.
     */
    boolean isActiveFlowEnabled();

    /**
     * If this connection is of type Enterprise it will return the configured Domains.
     *
     * @return a set with all domains configured
     */
    @NonNull
    Set<String> getDomainSet();

}
