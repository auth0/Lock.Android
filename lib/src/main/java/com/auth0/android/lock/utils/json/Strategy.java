package com.auth0.android.lock.utils.json;

import android.support.annotation.NonNull;

import java.util.List;

import static com.auth0.android.util.CheckHelper.checkArgument;

class Strategy {

    private final String name;
    private final List<Connection> connections;

    /**
     * Creates a new strategy instance
     *
     * @param connections connections for this strategy
     */
    public Strategy(@NonNull String name, List<Connection> connections) {
        checkArgument(name != null, "Must have a non-null name");
        this.name = name;
        this.connections = connections;
    }

    public String getName() {
        return name;
    }

    public List<Connection> getConnections() {
        return connections;
    }

}
