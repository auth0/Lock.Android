/*
 * DomainMatcher.java
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

package com.auth0.lock.util;

import com.auth0.core.Connection;
import com.auth0.core.Strategies;
import com.auth0.core.Strategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by hernan on 1/6/15.
 */
public class DomainMatcher {

    private static final String AT_SYMBOL = "@";

    Map<Connection, Set<String>> domains;

    private Connection connection;

    public DomainMatcher(List<Strategy> strategies) {
        domains = new HashMap<>();
        for (Strategy strategy: strategies) {
            if (Strategies.Type.ENTERPRISE.equals(strategy.getType())) {
                List<Connection> connections = strategy.getConnections();
                for (Connection connection: connections) {
                    final Set<String> set = connection.getDomainSet();
                    if (!set.isEmpty()) {
                        domains.put(connection, set);
                    }
                }
            }
        }
    }

    public boolean matches(String email) {
        this.connection = null;
        if (email == null || !email.contains(AT_SYMBOL)) {
            return false;
        }
        final String[] mailParts = email.split(AT_SYMBOL);
        if (mailParts.length != 2) {
            return false;
        }

        String domain = mailParts[1].toLowerCase();
        for (Map.Entry<Connection, Set<String>> entry: domains.entrySet()) {
            if (entry.getValue().contains(domain)) {
                this.connection = entry.getKey();
                return true;
            }
        }
        return false;
    }

    public Connection getConnection() {
        return connection;
    }
}
