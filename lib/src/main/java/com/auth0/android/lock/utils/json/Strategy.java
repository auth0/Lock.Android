package com.auth0.android.lock.utils.json;

import android.support.annotation.NonNull;

import java.util.List;

import static com.auth0.android.util.CheckHelper.checkArgument;

public class Strategy {

    private final String name;
    private final List<AuthData> connections;

    /**
     * Creates a new strategy instance
     *
     * @param connections Connections for this strategy
     */
    public Strategy(@NonNull String name, List<AuthData> connections) {
        checkArgument(name != null, "Must have a non-null name");
        this.name = name;
        this.connections = connections;
    }

    public String getName() {
        return name;
    }

    public List<AuthData> getConnections() {
        return connections;
    }

}
