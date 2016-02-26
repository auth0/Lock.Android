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

package com.auth0.android.lock;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.utils.Application;
import com.auth0.android.lock.utils.Connection;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.Strategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Configuration {

    private static final String TAG = Configuration.class.getSimpleName();

    private static final String SHOW_SIGNUP_KEY = "showSignup";
    private static final String SHOW_FORGOT_KEY = "showForgot";
    private static final String REQUIRES_USERNAME_KEY = "requires_username";

    private Connection defaultDatabaseConnection;

    private Connection defaultActiveDirectoryConnection;

    private Strategy passwordlessStrategy;

    private Strategy activeDirectoryStrategy;

    private List<Strategy> socialStrategies;

    private List<Strategy> enterpriseStrategies;

    private Application application;

    private boolean signUpEnabled;
    private boolean changePasswordEnabled;
    private boolean usernameRequired;
    private UsernameStyle usernameStyle;
    private boolean loginAfterSignUp;
    private PasswordlessMode passwordlessMode;

    public Configuration(Application application, Options options) {
        List<String> connections = options.getConnections();
        String defaultDatabaseName = options.getDefaultDatabaseConnection();
        Set<String> connectionSet = connections != null ? new HashSet<>(connections) : new HashSet<String>();
        this.defaultDatabaseConnection = filterDatabaseConnections(application.getDatabaseStrategy(), connectionSet, defaultDatabaseName);
        this.enterpriseStrategies = filterEnterpriseStrategies(application.getEnterpriseStrategies(), connectionSet);
        this.passwordlessStrategy = filterPasswordlessStrategies(application.getPasswordlessStrategies(), connectionSet);
        this.activeDirectoryStrategy = filterStrategy(application.strategyForName(Strategies.ActiveDirectory.getName()), connectionSet);
        this.defaultActiveDirectoryConnection = filteredDefaultADConnection(this.activeDirectoryStrategy);
        this.socialStrategies = filterSocialStrategies(application.getSocialStrategies(), connectionSet);
        this.application = application;
        parseLocalOptions(options);
    }

    public Connection getDefaultDatabaseConnection() {
        return defaultDatabaseConnection;
    }

    public Connection getDefaultActiveDirectoryConnection() {
        return defaultActiveDirectoryConnection;
    }

    @Nullable
    public Strategy getPasswordlessStrategy() {
        return passwordlessStrategy;
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

    public Application getApplication() {
        return application;
    }

    public boolean shouldUseNativeAuthentication(Connection connection, @NonNull List<String> enterpriseConnectionsUsingWebForm) {
        final Strategy strategy = getApplication().strategyForConnection(connection);
        return strategy.isActiveFlowEnabled() && !enterpriseConnectionsUsingWebForm.contains(connection.getName());
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
        for (Connection db : dbs) {
            if (db.getName().equals(defaultDatabaseName) || shouldSelect(db, set)) {
                connection = db;
                break;
            }
        }

        if (connection == null || (defaultDatabaseName != null && !connection.getName().equals(defaultDatabaseName))) {
            Log.w(TAG, "Your chosen default database name was not found in your Auth0 connections configuration.");
        }
        return connection;
    }

    private Strategy filterStrategy(Strategy strategy, Set<String> connections) {
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

    private List<Strategy> filterSocialStrategies(List<Strategy> strategies, Set<String> connections) {
        if (strategies == null || connections.isEmpty()) {
            return strategies;
        }
        List<Strategy> filtered = new ArrayList<>(strategies.size());
        for (Strategy strategy : strategies) {
            if (connections.contains(strategy.getName())) {
                filtered.add(strategy);
            }
        }
        return filtered;
    }

    private List<Strategy> filterEnterpriseStrategies(List<Strategy> strategies, Set<String> connections) {
        if (strategies == null || connections.isEmpty()) {
            return strategies;
        }
        List<Strategy> filtered = new ArrayList<>(strategies.size());
        for (Strategy strategy : strategies) {
            Strategy str = filterStrategy(strategy, connections);
            if (str != null) {
                filtered.add(str);
            }
        }
        return filtered;
    }

    private Strategy filterPasswordlessStrategies(List<Strategy> strategies, Set<String> connections) {
        if (strategies == null || strategies.isEmpty()) {
            return null;
        }

        if (connections.isEmpty()) {
            for (Strategy s : strategies) {
                if (s.getName().equals(Strategies.Email.getName())) {
                    return s;
                }
            }

            for (Strategy s : strategies) {
                if (s.getName().equals(Strategies.SMS.getName())) {
                    return s;
                }
            }
        } else {
            for (Strategy s : strategies) {
                if (s.getName().equals(Strategies.Email.getName())) {
                    for (Connection c : s.getConnections()) {
                        if (connections.contains(c.getName())) {
                            return s;
                        }
                    }
                }
            }

            for (Strategy s : strategies) {
                if (s.getName().equals(Strategies.SMS.getName())) {
                    for (Connection c : s.getConnections()) {
                        if (connections.contains(c.getName())) {
                            return s;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void parseLocalOptions(Options options) {
        usernameStyle = options.usernameStyle();
        loginAfterSignUp = options.loginAfterSignUp();

        if (getDefaultDatabaseConnection() != null) {
            //let user disable signUp only if connection have enabled it.
            signUpEnabled = getDefaultDatabaseConnection().booleanForKey(SHOW_SIGNUP_KEY);
            if (signUpEnabled && !options.isSignUpEnabled()) {
                signUpEnabled = false;
            }

            //let user disable signUp only if connection have enabled it.
            changePasswordEnabled = getDefaultDatabaseConnection().booleanForKey(SHOW_FORGOT_KEY);
            if (changePasswordEnabled && !options.isChangePasswordEnabled()) {
                changePasswordEnabled = false;
            }

            usernameRequired = getDefaultDatabaseConnection().booleanForKey(REQUIRES_USERNAME_KEY);
        }

        if (getPasswordlessStrategy() == null) {
            return;
        }

        if (getPasswordlessStrategy().getName().equals(Strategies.Email.getName())) {
            passwordlessMode = options.useCodePasswordless() ? PasswordlessMode.EMAIL_CODE : PasswordlessMode.EMAIL_LINK;
        } else if (getPasswordlessStrategy().getName().equals(Strategies.SMS.getName())) {
            passwordlessMode = options.useCodePasswordless() ? PasswordlessMode.SMS_CODE : PasswordlessMode.SMS_LINK;
        }
    }

    private boolean shouldSelect(Connection connection, Set<String> connections) {
        return connections.isEmpty() || connections.contains(connection.getName());
    }

    private Connection filteredDefaultADConnection(Strategy activeDirectoryStrategy) {
        if (activeDirectoryStrategy == null) {
            return null;
        }
        final List<Connection> connections = activeDirectoryStrategy.getConnections();
        return !connections.isEmpty() ? connections.get(0) : null;
    }

    public boolean isSignUpEnabled() {
        return signUpEnabled;
    }

    public boolean isChangePasswordEnabled() {
        return changePasswordEnabled;
    }

    public boolean isUsernameRequired() {
        return usernameRequired;
    }

    public UsernameStyle getUsernameStyle() {
        return usernameStyle;
    }

    @Nullable
    public PasswordlessMode getPasswordlessMode() {
        return passwordlessMode;
    }

    public boolean loginAfterSignUp() {
        return loginAfterSignUp;
    }
}