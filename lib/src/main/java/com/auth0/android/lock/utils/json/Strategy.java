/*
 * Strategy.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.utils.json;


import android.support.annotation.NonNull;

import com.auth0.android.lock.utils.Strategies;

import java.util.ArrayList;
import java.util.List;

/**
 * Class with Auth0 authentication strategy info
 */
public class Strategy {

    private String name;
    private List<Connection> connections;
    private Strategies strategyMetadata;

    public Strategy() {
        this.connections = new ArrayList<>();
    }

    public Strategy(String name, List<Connection> connections) {
        this.name = name;
        this.strategyMetadata = Strategies.fromName(name);
        boolean isActiveFlowEnabled = isActiveFlowEnabled();
        for (Connection c : connections) {
            c.setActiveFlowEnabled(isActiveFlowEnabled);
        }
        this.connections = connections;
    }

    public String getName() {
        return name;
    }

    public List<Connection> getConnections() {
        return connections;
    }

    @Strategies.Type
    public int getType() {
        return this.strategyMetadata.getType();
    }

    public boolean isActiveFlowEnabled() {
        return Strategies.ActiveDirectory.getName().equals(name)
                || Strategies.ADFS.getName().equals(name)
                || Strategies.Waad.getName().equals(name);
    }

    /**
     * Returns the name of the first connection found in this strategy. When no connections available,
     * it will default to the strategy name.
     *
     * @return the first connection found or the strategy name if no connections are available.
     */
    @NonNull
    public String getDefaultConnectionName() {
        return !connections.isEmpty() ? connections.get(0).getName() : this.name;
    }
}