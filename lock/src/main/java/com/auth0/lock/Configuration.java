/*
 * Configuration.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.lock;

import com.auth0.core.Application;
import com.auth0.core.Connection;
import com.auth0.core.Strategies;
import com.auth0.core.Strategy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configuration {

    private static final String DEFAULT_DB_CONNECTION = "Username-Password-Authentication";

    private Connection defaultDatabaseConnection;

    private Connection defaultActiveDirectoryConnection;

    private Strategy activeDirectoryStrategy;

    private List<Strategy> socialStrategies;

    private List<Strategy> enterpriseStrategies;

    public Configuration(Application application, List<String> connections, String defaultDatabaseName) {
        Set<String> connectionSet = connections != null ? new HashSet<>(connections) : new HashSet<String>();
        this.defaultDatabaseConnection = filterDatabaseConnections(application.getDatabaseStrategy(), connectionSet, defaultDatabaseName);
        this.activeDirectoryStrategy = filterADStrategy(application.strategyForName(Strategies.ActiveDirectory.getName()), connectionSet);
        this.defaultActiveDirectoryConnection = filteredDefaultADConnection();
    }

    public Connection getDefaultDatabaseConnection() {
        return defaultDatabaseConnection;
    }

    public Connection getDefaultActiveDirectoryConnection() {
        return defaultActiveDirectoryConnection;
    }

    public Strategy getActiveDirectoryStrategy() {
        return activeDirectoryStrategy;
    }

    public List<Strategy> getSocialStrategies() {
        return socialStrategies;
    }

    public List<Strategy> getEnterpriseStrategies() {
        return enterpriseStrategies;
    }

    private Connection filterDatabaseConnections(Strategy databaseStrategy, Set<String> connections, String defaultDatabaseName) {
        List<Connection> dbs = databaseStrategy != null ? databaseStrategy.getConnections() : null;
        if (dbs == null) {
            return null;
        }
        Set<String> set = new HashSet<>(connections);
        if (defaultDatabaseName != null) {
            set.add(defaultDatabaseName);
        }
        Connection connection = null;
        for (Connection db: dbs) {
            if (db.getName().equals(defaultDatabaseName) || shouldSelect(db, set)) {
                connection = db;
                break;
            }
        }
        return connection;
    }

    private Strategy filterADStrategy(Strategy strategy, Set<String> connections) {
        if (strategy == null || connections.isEmpty()) {
            return strategy;
        }
        List<Connection> filtered = new ArrayList<>(strategy.getConnections().size());
        for (Connection connection : strategy.getConnections()) {
            if (connections.contains(connection.getName())) {
                filtered.add(connection);
            }
        }
        if (filtered.isEmpty()) {
            return null;
        }
        return new Strategy(strategy.getName(), filtered);
    }

    private boolean shouldSelect(Connection connection, Set<String> connections) {
        return connections.isEmpty() || connections.contains(connection.getName());
    }

    private Connection filteredDefaultADConnection() {
        if (this.activeDirectoryStrategy == null) {
            return null;
        }
        final List<Connection> connections = this.activeDirectoryStrategy.getConnections();
        return !connections.isEmpty() ? connections.get(0) : null;
    }
}
