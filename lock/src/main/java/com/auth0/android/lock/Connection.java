/*
 * Connection.java
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

package com.auth0.android.lock;

import com.auth0.authentication.api.util.CheckHelper;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Class with a Auth0 connection info
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Connection {

    protected String name;
    protected Map<String, Object> values;

    protected Connection(Connection connection) {
        name = connection.name;
        values = connection.values;
    }

    protected Connection() {

    }

    /**
     * Creates a new connection instance
     * @param values Connection values
     */
    @JsonCreator
    public Connection(Map<String, Object> values) {
        CheckHelper.checkArgument(values != null && values.size() > 0, "Must have at least one value");
        final String name = (String) values.remove("name");
        CheckHelper.checkArgument(name != null, "Must have a non-null name");
        this.name = name;
        this.values = values;
    }

    /**
     * Returns all the connection values
     * @return connection values
     */
    public Map<String, Object> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    /**
     * Returns a value using its key
     * @param key a key
     * @param <T> type of value to return
     * @return a value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueForKey(String key) {
        return (T) this.values.get(key);
    }

    /**
     * Returns a boolean value using its key
     * @param key a key
     * @return the value of the flag
     */
    public boolean booleanForKey(String key) {
        Boolean value = getValueForKey(key);
        if (value == null) {
            return false;
        }
        return value;
    }

    /**
     * Get set of domain if the connection is Enterprise
     * @return a set with all domains configured
     */
    public Set<String> getDomainSet() {
        Set<String> domains = new HashSet<>();
        String domain = getValueForKey("domain");
        if (domain != null) {
            domains.add(domain.toLowerCase());
            List<String> aliases = getValueForKey("domain_aliases");
            if (aliases != null) {
                for (String alias: aliases) {
                    domains.add(alias.toLowerCase());
                }
            }
        }
        return domains;
    }
}
