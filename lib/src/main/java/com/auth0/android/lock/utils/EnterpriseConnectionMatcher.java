/*
 * DomainParser.java
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

package com.auth0.android.lock.utils;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.lock.utils.json.Connection;
import com.auth0.android.lock.utils.json.Strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to find out which email domains can be valid for the current Auth0 configuration.
 */
public class EnterpriseConnectionMatcher {

    private static final String TAG = EnterpriseConnectionMatcher.class.getSimpleName();
    private static final String DOMAIN_KEY = "domain";
    private static final String DOMAIN_ALIASES_KEY = "domain_aliases";
    private static final String AT_SYMBOL = "@";

    private List<Strategy> strategies;

    public EnterpriseConnectionMatcher(List<Strategy> strategies) {
        this.strategies = new ArrayList<>();
        if (strategies == null) {
            return;
        }

        for (Strategy s : strategies) {
            if (s.getType() == Strategies.Type.ENTERPRISE) {
                this.strategies.add(s);
            }
        }
        Log.v(TAG, String.format("Creating a new instance to match %d Enterprise Strategies", this.strategies.size()));
    }

    /**
     * Tries to find a valid domain with the given input.
     *
     * @param email to search the Domain for.
     * @return a Connection if found, null otherwise.
     */
    @Nullable
    public Connection parse(String email) {
        String domain = extractDomain(email);
        if (domain == null) {
            return null;
        }

        domain = domain.toLowerCase();
        for (Strategy s : strategies) {
            for (Connection c : s.getConnections()) {
                String mainDomain = domainForConnection(c);
                if (mainDomain != null && mainDomain.equalsIgnoreCase(domain)) {
                    return c;
                }

                List<String> aliases = c.getValueForKey(DOMAIN_ALIASES_KEY);
                if (aliases != null) {
                    for (String d : aliases) {
                        if (d.equalsIgnoreCase(domain)) {
                            return c;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Extracts the username part from the email
     *
     * @param email to parse
     * @return the username String if found, an empty String otherwise
     */
    @Nullable
    public String extractUsername(String email) {
        int indexAt = email.indexOf(AT_SYMBOL);
        if (indexAt == -1) {
            return null;
        }
        return email.substring(0, indexAt);
    }

    /**
     * Extracts the domain part from the email
     *
     * @param email to parse
     * @return the domain String if found, an empty String otherwise
     */
    @Nullable
    private String extractDomain(String email) {
        int indexAt = email.indexOf(AT_SYMBOL) + 1;
        if (indexAt == 0) {
            return null;
        }
        String domain = email.substring(indexAt);
        if (domain.isEmpty()) {
            return null;
        }
        return domain;
    }

    /**
     * Extracts the Connection's main domain.
     *
     * @param connection to extract the domain from
     * @return the main domain.
     */
    public String domainForConnection(@NonNull Connection connection) {
        return connection.getValueForKey(DOMAIN_KEY);
    }
}
