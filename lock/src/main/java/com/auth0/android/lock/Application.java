/*
 * Application.java
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


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

import static com.auth0.authentication.api.util.CheckHelper.checkArgument;

/**
 * Class with your Auth0's application information and the list of enabled connections (DB, Social, Enterprise, Passwordless).
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Application {

    private String id;
    private String tenant;
    private String authorizeURL;
    private String callbackURL;
    private String subscription;
    private boolean hasAllowedOrigins;
    private List<Strategy> strategies;
    private List<Strategy> socialStrategies;
    private List<Strategy> enterpriseStrategies;
    private Strategy databaseStrategy;

    public Application(Application application) {
        id = application.id;
        tenant = application.tenant;
        authorizeURL = application.authorizeURL;
        callbackURL = application.callbackURL;
        subscription = application.subscription;
        hasAllowedOrigins = application.hasAllowedOrigins;
        strategies = application.strategies;
        socialStrategies = application.socialStrategies;
        enterpriseStrategies = application.enterpriseStrategies;
        databaseStrategy = application.databaseStrategy;
    }

    /**
     * Creates a new application instance
     * @param id app id.
     * @param tenant name of the tenant who owns the app.
     * @param authorizeURL url used to authorize during oauth flow.
     * @param callbackURL url used after a oauth flow.
     * @param subscription type of subscription.
     * @param hasAllowedOrigins if the app allows other origins
     * @param strategies list of the strategies enabled for the app (Social, DB, etc).
     */
    @JsonCreator
    public Application(@JsonProperty(value = "id") String id,
                       @JsonProperty(value = "tenant") String tenant,
                       @JsonProperty(value = "authorize") String authorizeURL,
                       @JsonProperty(value = "callback") String callbackURL,
                       @JsonProperty(value = "subscription") String subscription,
                       @JsonProperty(value = "hasAllowedOrigins") boolean hasAllowedOrigins,
                       @JsonProperty(value = "strategies") List<Strategy> strategies) {
        checkArgument(id != null, "id must be non-null");
        checkArgument(tenant != null, "tenant must be non-null");
        checkArgument(authorizeURL != null, "authorize must be non-null");
        checkArgument(strategies != null && strategies.size() > 0, "Must have at least 1 strategy");
        this.id = id;
        this.tenant = tenant;
        this.authorizeURL = authorizeURL;
        this.callbackURL = callbackURL;
        this.subscription = subscription;
        this.hasAllowedOrigins = hasAllowedOrigins;
        this.strategies = strategies;
        this.socialStrategies = new ArrayList<>();
        this.enterpriseStrategies = new ArrayList<>();
        for(Strategy strategy: strategies) {
            if (Strategies.Auth0.getName().equals(strategy.getName())) {
                this.databaseStrategy = strategy;
            } else {
                switch (strategy.getType()) {
                    case SOCIAL:
                        this.socialStrategies.add(strategy);
                        break;
                    case ENTERPRISE:
                        this.enterpriseStrategies.add(strategy);
                        break;
                }
            }
        }
    }

    /**
     * Returns the id of the application.
     * @return an ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the tenant who owns the app.
     * @return name of the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * Returns url used to authorize during oauth flow.
     * @return a url string
     */
    public String getAuthorizeURL() {
        return authorizeURL;
    }

    /**
     * Returns url used after a oauth flow.
     * @return a url string
     */
    public String getCallbackURL() {
        return callbackURL;
    }

    /**
     * Returns the type of subscription
     * @return type of subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Returns if the app allows other origins.
     * @return hasAllowedOrigins flag
     */
    public boolean isHasAllowedOrigins() {
        return hasAllowedOrigins;
    }

    /**
     * Returns all available auth strategies for the app.
     * @return
     */
    public List<Strategy> getStrategies() {
        return new ArrayList<>(strategies);
    }

    /**
     * Returns the Database strategy of the app.
     * @return DB strategy
     */
    public Strategy getDatabaseStrategy() {
        return databaseStrategy;
    }

    /**
     * Returns the social strategies of the app.
     * @return list of social strategies
     */
    public List<Strategy> getSocialStrategies() {
        return new ArrayList<>(socialStrategies);
    }

    /**
     * Returns the social enterprise of the app.
     * @return list of enterprise strategies
     */
    public List<Strategy> getEnterpriseStrategies() {
        return new ArrayList<>(enterpriseStrategies);
    }

    /**
     * Returns a {@link Strategy} by its name
     * @param name strategy name
     * @return a {@link Strategy}
     */
    public Strategy strategyForName(String name) {
        for (Strategy strategy: this.strategies) {
            if (strategy.getName().equals(name)) {
                return strategy;
            }
        }
        return null;
    }

    /**
     * Returns the strategy by one of its connections
     * @param connection a connection
     * @return a {@link Strategy}
     */
    public Strategy strategyForConnection(Connection connection) {
        for (Strategy strategy : this.strategies) {
            for (Connection conn : strategy.getConnections()) {
                if (conn.getName().equals(connection.getName())) {
                    return strategy;
                }
            }
        }
        return null;
    }
}
