package com.auth0.android.lock.internal.json;

import android.support.annotation.NonNull;

import com.auth0.android.lock.internal.AuthType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PasswordlessConnection extends Connection {

    /**
     * Creates a new PasswordlessConnection instance
     *
     * @param strategy the OAuth strategy to use.
     * @param values   Connection values
     */
    PasswordlessConnection(@NonNull String strategy, Map<String, Object> values) {
        super(strategy, values);
    }

    @Override
    public int getType() {
        return AuthType.PASSWORDLESS;
    }
}
