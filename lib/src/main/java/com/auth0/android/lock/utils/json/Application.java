/*
 * Application.java
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

import com.auth0.android.lock.utils.Strategies;

import java.util.ArrayList;
import java.util.List;

import static com.auth0.android.lock.utils.Strategies.Type.DATABASE;
import static com.auth0.android.lock.utils.Strategies.Type.ENTERPRISE;
import static com.auth0.android.lock.utils.Strategies.Type.PASSWORDLESS;
import static com.auth0.android.lock.utils.Strategies.Type.SOCIAL;

/**
 * Class with your Auth0's application information and the list of enabled connections (DB, Social, Enterprise, Passwordless).
 * Disclaimer: This class may change in future releases. Don't use it directly.
 */
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
    private List<Strategy> passwordlessStrategies;
    private Strategy databaseStrategy;

    public Application() {
        strategies = new ArrayList<>();
        socialStrategies = new ArrayList<>();
        enterpriseStrategies = new ArrayList<>();
        passwordlessStrategies = new ArrayList<>();
    }

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
        passwordlessStrategies = application.passwordlessStrategies;
        databaseStrategy = application.databaseStrategy;
    }

    /**
     * Creates a new application instance
     *
     * @param id                app id.
     * @param tenant            name of the tenant who owns the app.
     * @param authorizeURL      url used to authorize during oauth flow.
     * @param callbackURL       url used after a oauth flow.
     * @param subscription      type of subscription.
     * @param hasAllowedOrigins if the app allows other origins
     * @param strategies        list of the strategies enabled for the app (Social, DB, etc).
     */
    public Application(String id, String tenant, String authorizeURL, String callbackURL, String subscription, boolean hasAllowedOrigins, List<Strategy> strategies) {
        this.id = id;
        this.tenant = tenant;
        this.authorizeURL = authorizeURL;
        this.callbackURL = callbackURL;
        this.subscription = subscription;
        this.hasAllowedOrigins = hasAllowedOrigins;
        this.strategies = strategies;
        this.socialStrategies = new ArrayList<>();
        this.enterpriseStrategies = new ArrayList<>();
        this.passwordlessStrategies = new ArrayList<>();
        for (Strategy strategy : strategies) {
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
                    case PASSWORDLESS:
                        this.passwordlessStrategies.add(strategy);
                    case DATABASE:
                        break;
                }
            }
        }
    }

    /**
     * Returns the id of the application.
     *
     * @return an ID
     */
    public String getId() {
        return id;
    }

    /**
     * Returns the name of the tenant who owns the app.
     *
     * @return name of the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * Returns url used to authorize during oauth flow.
     *
     * @return a url string
     */
    public String getAuthorizeURL() {
        return authorizeURL;
    }

    /**
     * Returns url used after a oauth flow.
     *
     * @return a url string
     */
    public String getCallbackURL() {
        return callbackURL;
    }

    /**
     * Returns the type of subscription
     *
     * @return type of subscription
     */
    public String getSubscription() {
        return subscription;
    }

    /**
     * Returns if the app allows other origins.
     *
     * @return hasAllowedOrigins flag
     */
    public boolean hasAllowedOrigins() {
        return hasAllowedOrigins;
    }

    /**
     * Returns all available auth strategies for the app.
     *
     * @return
     */
    public List<Strategy> getStrategies() {
        return new ArrayList<>(strategies);
    }

    /**
     * Returns the Database strategy of the app.
     *
     * @return DB strategy
     */
    public Strategy getDatabaseStrategy() {
        return databaseStrategy;
    }

    /**
     * Returns the social strategies of the app.
     *
     * @return list of social strategies
     */
    public List<Strategy> getSocialStrategies() {
        return new ArrayList<>(socialStrategies);
    }

    /**
     * Returns the social enterprise of the app.
     *
     * @return list of enterprise strategies
     */
    public List<Strategy> getEnterpriseStrategies() {
        return new ArrayList<>(enterpriseStrategies);
    }

    /**
     * Returns the passwordless strategies of the app.
     *
     * @return list of passwordless strategies
     */
    public List<Strategy> getPasswordlessStrategies() {
        return new ArrayList<>(passwordlessStrategies);
    }

    /**
     * Returns a {@link Strategy} by its name
     *
     * @param name strategy name
     * @return a {@link Strategy}
     */
    public Strategy strategyForName(String name) {
        for (Strategy strategy : this.strategies) {
            if (strategy.getName().equals(name)) {
                return strategy;
            }
        }
        return null;
    }

    /**
     * Returns the strategy by one of its connections
     *
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