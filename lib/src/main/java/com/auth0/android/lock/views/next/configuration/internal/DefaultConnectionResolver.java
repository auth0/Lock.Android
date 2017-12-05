package com.auth0.android.lock.views.next.configuration.internal;

import android.support.annotation.NonNull;

import com.auth0.android.lock.views.next.configuration.ConnectionResolver;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public class DefaultConnectionResolver implements ConnectionResolver {
    @NonNull
    @Override
    public String resolveFor(@NonNull String identity) {
        return "Username-Password-Authentication";
    }
}
