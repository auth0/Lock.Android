package com.auth0.android.lock.events;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.auth0.android.lock.internal.configuration.OAuthConnection;

public class OAuthLoginEvent {

    private final OAuthConnection connection;
    private String username;
    private String password;

    /**
     * Creates a new OAuthLoginEvent to authenticate using the Active Flow.
     *
     * @param connection the connection instance.
     * @param username   the username to use.
     * @param password   the password to use.
     */
    public OAuthLoginEvent(@NonNull OAuthConnection connection, @NonNull String username, @Nullable String password) {
        this(connection);
        this.username = username;
        this.password = password;
    }

    /**
     * Creates a new OAuthLoginEvent to authenticate using the WebAuth flow.
     *
     * @param connection the connection instance.
     */
    public OAuthLoginEvent(@NonNull OAuthConnection connection) {
        this.connection = connection;
    }

    /**
     * Returns if this login event should use Resource Owner to authenticate, instead of the WebAuth flow.
     *
     * @return if the Active Flow (Resource Owner) is enabled.
     */
    public boolean useActiveFlow() {
        return username != null && password != null;
    }

    /**
     * Returns the connection name to use.
     *
     * @return the connection name.
     */
    @NonNull
    public String getConnection() {
        return connection.getName();
    }

    /**
     * Returns the strategy name to use.
     *
     * @return the strategy name.
     */
    @Nullable
    public String getStrategy() {
        return connection.getStrategy();
    }

    /**
     * Getter for the username. Will only be available if this login event uses Active Flow.
     *
     * @return the username to use, or null if this connection doesn't use Active Flow.
     */
    @Nullable
    public String getUsername() {
        return username;
    }

    /**
     * Getter for the password. Will only be available if this login request uses Active Flow.
     *
     * @return the password to use, or null if this connection doesn't use Active Flow.
     */
    @Nullable
    public String getPassword() {
        return password;
    }
}
