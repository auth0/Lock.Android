package com.auth0.android.lock.events;

/**
 * Created by lbalmaceda on 1/22/16.
 */
public class SocialConnectionEvent {
    private final String connectionName;

    public SocialConnectionEvent(String connectionName) {
        this.connectionName = connectionName;
    }
}
