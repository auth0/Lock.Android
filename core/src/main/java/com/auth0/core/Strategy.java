package com.auth0.core;

import com.squareup.moshi.Json;

import java.util.List;


import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class with Auth0 authentication strategy info
 */
public class Strategy extends StrategyJson {
    private transient Strategies strategyMetadata;

    @SuppressWarnings("unused") // Moshi uses this!
    private Strategy() {
        strategyMetadata=null;
    }
    public Strategy(String name,
                    List<Connection> connections) {
        this.name = name;
        this.connections = connections;
        init();
    }

    private void init() {
        checkArgument(name != null, "name must be non-null");
        checkArgument(connections != null, "connections must be non-null");
        this.strategyMetadata = Strategies.fromName(name);
    }

    public String getName() {
        return name;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public Strategies.Type getType() {
        return this.strategyMetadata.getType();
    }

    public boolean isResourceOwnerEnabled() {
        return Strategies.ActiveDirectory.getName().equals(name)
                || Strategies.ADFS.getName().equals(name)
                || Strategies.Waad.getName().equals(name);
    }
}
