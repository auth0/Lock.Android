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

import android.support.annotation.Nullable;

import com.auth0.Auth0Exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class to find out which email domains can be valid for the current Auth0 configuration.
 */
public class EmailParser {

    private static final String DOMAIN_KEY = "domain";
    private static final String DOMAIN_ALIASES_KEY = "domain_aliases";

    private List<Strategy> strategies;

    public EmailParser(List<Strategy> strategies) {
        if (strategies == null || strategies.isEmpty()) {
            throw new Auth0Exception("You must provide a valid list of Strategies.");
        }
        this.strategies = strategies;
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
        if (domain.isEmpty()) {
            return null;
        }

        domain = domain.toLowerCase();
        for (Strategy s : strategies) {
            if (s.getType() != Strategies.Type.ENTERPRISE) {
                continue;
            }
            for (Connection c : s.getConnections()) {
                String mainDomain = c.getValueForKey(DOMAIN_KEY);
                String[] aliases = c.getValueForKey(DOMAIN_ALIASES_KEY);
                List<String> strings;
                strings = aliases == null ? new ArrayList<String>() : Arrays.asList(aliases);

                if (strings.contains(domain) || domain.equals(mainDomain)) {
                    return c;
                }
            }
        }
        return null;
    }


    /**
     * Tries to find the Strategy holder of the given connection.
     *
     * @param connection to search for.
     * @return a Strategy if found, null otherwise.
     */
    @Nullable
    public Strategy strategyForConnection(Connection connection) {
        if (connection == null) {
            return null;
        }

        for (Strategy s : strategies) {
            if (s.getType() != Strategies.Type.ENTERPRISE) {
                continue;
            }
            for (Connection c : s.getConnections()) {
                if (c.equals(connection)) {
                    return s;
                }
            }
        }
        return null;
    }


    /**
     * Extracts the domain part from the email
     *
     * @param email to parse
     * @return the domain String if found, an empty String otherwise
     */
    public String extractDomain(String email) {
        int indexAt = email.indexOf("@") + 1;
        if (indexAt == 0) {
            return "";
        }
        int indexDot = email.indexOf(".", indexAt);
        String domain;
        if (indexDot == -1) {
            domain = email.substring(indexAt);
        } else {
            domain = email.substring(indexAt, indexDot);
        }
        if (domain.isEmpty()) {
            return "";
        }
        return domain;
    }

    /**
     * Extracts the username part from the email
     *
     * @param email to parse
     * @return the username String if found, an empty String otherwise
     */
    public String extractUsername(String email) {
        int indexAt = email.indexOf("@");
        if (indexAt == -1) {
            return "";
        }
        return email.substring(0, indexAt);
    }


}
