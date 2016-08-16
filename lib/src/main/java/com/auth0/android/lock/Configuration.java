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
import android.support.annotation.StyleRes;
import android.util.Log;

import com.auth0.android.lock.enums.InitialScreen;
import com.auth0.android.lock.enums.PasswordStrength;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.enums.SocialButtonStyle;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.json.Application;
import com.auth0.android.lock.utils.json.Connection;
import com.auth0.android.lock.utils.json.Strategy;
import com.auth0.android.lock.views.AuthConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Configuration {

    private static final String TAG = Configuration.class.getSimpleName();

    private static final String SHOW_SIGNUP_KEY = "showSignup";
    private static final String SHOW_FORGOT_KEY = "showForgot";
    private static final String REQUIRES_USERNAME_KEY = "requires_username";
    private static final String PASSWORD_POLICY_KEY = "passwordPolicy";
    private final List<CustomField> extraSignUpFields;

    private Connection defaultDatabaseConnection;

    private Connection defaultActiveDirectoryConnection;

    private List<Strategy> passwordlessStrategies;

    private Strategy activeDirectoryStrategy;

    private List<Strategy> socialStrategies;

    private List<Strategy> enterpriseStrategies;

    private Application application;

    private boolean allowLogIn;
    private boolean allowSignUp;
    private boolean allowForgotPassword;
    private boolean usernameRequired;
    private boolean mustAcceptTerms;
    @PasswordStrength
    private int passwordPolicy;
    private final boolean classicLockAvailable;
    private final boolean passwordlessLockAvailable;
    @UsernameStyle
    private int usernameStyle;
    @SocialButtonStyle
    private int socialButtonStyle;
    private boolean loginAfterSignUp;
    @PasswordlessMode
    private int passwordlessMode;
    @InitialScreen
    private int initialScreen;
    private String termsURL;
    private String privacyURL;
    private Map<String, Integer> authStyles;

    public Configuration(Application application, Options options) {
        List<String> connections = options.getConnections();
        String defaultDatabaseName = options.getDefaultDatabaseConnection();
        Set<String> connectionSet = connections != null ? new HashSet<>(connections) : new HashSet<String>();
        this.extraSignUpFields = options.getCustomFields();
        this.defaultDatabaseConnection = filterDatabaseConnections(application.getDatabaseStrategy(), connectionSet, defaultDatabaseName);
        this.enterpriseStrategies = filterStrategiesByConnections(application.getEnterpriseStrategies(), connectionSet);
        this.passwordlessStrategies = filterStrategiesByConnections(application.getPasswordlessStrategies(), connectionSet);
        this.activeDirectoryStrategy = filterStrategy(application.strategyForName(Strategies.ActiveDirectory.getName()), connectionSet);
        this.defaultActiveDirectoryConnection = filteredDefaultADConnection(this.activeDirectoryStrategy);
        this.socialStrategies = filterSocialStrategies(application.getSocialStrategies(), connectionSet);
        this.application = application;
        parseLocalOptions(options);
        boolean atLeastOneSocialModeEnabled = allowLogIn || allowSignUp;
        boolean atLeastOneDatabaseModeEnabled = atLeastOneSocialModeEnabled || allowForgotPassword;
        this.classicLockAvailable = (!socialStrategies.isEmpty() && atLeastOneSocialModeEnabled) || (!enterpriseStrategies.isEmpty() && allowLogIn)
                || (defaultDatabaseConnection != null && atLeastOneDatabaseModeEnabled);
        this.passwordlessLockAvailable = !socialStrategies.isEmpty() || !passwordlessStrategies.isEmpty();
    }

    @NonNull
    public List<CustomField> getExtraSignUpFields() {
        return extraSignUpFields;
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

    @Nullable
    public Strategy getDefaultPasswordlessStrategy() {
        if (passwordlessStrategies.isEmpty()) {
            return null;
        }

        if (passwordlessStrategies.size() == 1) {
            return passwordlessStrategies.get(0);
        }

        Strategy strategy = null;
        for (Strategy s : passwordlessStrategies) {
            if (s.getName().equals(Strategies.Email.getName())) {
                strategy = s;
                break;
            }
        }

        return strategy != null ? strategy : passwordlessStrategies.get(0);
    }

    public List<Strategy> getSocialStrategies() {
        return socialStrategies;
    }

    public List<Strategy> getEnterpriseStrategies() {
        return enterpriseStrategies;
    }

    public List<Strategy> getPasswordlessStrategies() {
        return passwordlessStrategies;
    }

    public String getFirstConnectionOfStrategy(@NonNull Strategy strategy) {
        return strategy.getConnections().get(0).getName();
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

        Connection defaultDb = getConnectionForName(dbs, defaultDatabaseName);
        if (defaultDb != null && shouldSelect(defaultDb, connections)) {
            return defaultDb;
        }

        if (defaultDatabaseName != null) {
            Log.w(TAG, String.format("You've chosen '%s' as your default database name, but it wasn't found in your Auth0 connections configuration.", defaultDatabaseName));
        }

        for (Connection db : dbs) {
            if (shouldSelect(db, connections)) {
                return db;
            }
        }
        return null;
    }

    private Connection getConnectionForName(List<Connection> connections, String name) {
        for (Connection c : connections) {
            if (c.getName().equals(name)) {
                return c;
            }
        }
        return null;
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

    private List<Strategy> filterStrategiesByConnections(List<Strategy> strategies, Set<String> connections) {
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

    private void parseLocalOptions(Options options) {
        usernameStyle = options.usernameStyle();
        socialButtonStyle = options.socialButtonStyle();
        loginAfterSignUp = options.loginAfterSignUp();
        mustAcceptTerms = options.mustAcceptTerms();

        final boolean socialAvailable = !getSocialStrategies().isEmpty();
        final boolean dbAvailable = getDefaultDatabaseConnection() != null;
        final boolean enterpriseAvailable = !getEnterpriseStrategies().isEmpty();
        if (dbAvailable || enterpriseAvailable || socialAvailable) {
            //let user disable logIn only if connection have enabled it.
            allowLogIn = options.allowLogIn();
        }
        //let user disable signUp only if connection have enabled it.
        allowSignUp = options.allowSignUp() && (socialAvailable || dbAvailable && getDefaultDatabaseConnection().booleanForKey(SHOW_SIGNUP_KEY));
        authStyles = options.getAuthStyles();

        if (dbAvailable) {
            //let user disable password reset only if connection have enabled it.
            allowForgotPassword = getDefaultDatabaseConnection().booleanForKey(SHOW_FORGOT_KEY) && options.allowForgotPassword();

            usernameRequired = getDefaultDatabaseConnection().booleanForKey(REQUIRES_USERNAME_KEY);
            passwordPolicy = parsePasswordPolicy((String) getDefaultDatabaseConnection().getValueForKey(PASSWORD_POLICY_KEY));

            initialScreen = options.initialScreen();
            switch (initialScreen) {
                case InitialScreen.FORGOT_PASSWORD:
                    if (!allowForgotPassword) {
                        //Continue to the LOG_IN case to try to default to another option.
                        Log.w(TAG, "You chose 'FORGOT_PASSWORD' as the initial screen but your configuration doesn't have 'allowForgotPassword' enabled. Trying to default to 'LOG_IN'.");
                    } else {
                        break;
                    }
                case InitialScreen.LOG_IN:
                    if (allowLogIn) {
                        initialScreen = InitialScreen.LOG_IN;
                    } else if (allowSignUp) {
                        Log.w(TAG, "You chose 'LOG_IN' as the initial screen but your configuration doesn't have 'allowLogIn' enabled. Defaulting to 'SIGN_UP'.");
                        initialScreen = InitialScreen.SIGN_UP;
                    } else if (allowForgotPassword) {
                        initialScreen = InitialScreen.FORGOT_PASSWORD;
                        Log.w(TAG, "You chose 'LOG_IN' as the initial screen but your configuration doesn't have 'allowLogIn' enabled. Defaulting to 'FORGOT_PASSWORD'");
                    } else {
                        Log.w(TAG, "You chose 'LOG_IN' as the initial screen but your configuration doesn't have 'allowLogIn' enabled.");
                    }
                    break;
                case InitialScreen.SIGN_UP:
                    if (allowSignUp) {
                        initialScreen = InitialScreen.SIGN_UP;
                    } else if (allowLogIn) {
                        Log.w(TAG, "You chose 'SIGN_UP' as the initial screen but your configuration doesn't have 'allowSignUp' enabled. Defaulting to 'LOG_IN'.");
                        initialScreen = InitialScreen.LOG_IN;
                    } else if (allowForgotPassword) {
                        initialScreen = InitialScreen.FORGOT_PASSWORD;
                        Log.w(TAG, "You chose 'SIGN_UP' as the initial screen but your configuration doesn't have 'allowSignUp' enabled. Defaulting to 'FORGOT_PASSWORD'");
                    } else {
                        Log.w(TAG, "You chose 'SIGN_UP' as the initial screen but your configuration doesn't have 'allowSignUp' enabled.");
                    }
                    break;
            }
        }

        Strategy passwordlessStrategy = getDefaultPasswordlessStrategy();
        if (passwordlessStrategy != null) {
            if (passwordlessStrategy.getName().equals(Strategies.Email.getName())) {
                passwordlessMode = options.useCodePasswordless() ? PasswordlessMode.EMAIL_CODE : PasswordlessMode.EMAIL_LINK;
            } else if (passwordlessStrategy.getName().equals(Strategies.SMS.getName())) {
                passwordlessMode = options.useCodePasswordless() ? PasswordlessMode.SMS_CODE : PasswordlessMode.SMS_LINK;
            }
        } else {
            passwordlessMode = PasswordlessMode.DISABLED;
        }

        this.termsURL = options.getTermsURL() == null ? "https://auth0.com/terms" : options.getTermsURL();
        this.privacyURL = options.getPrivacyURL() == null ? "https://auth0.com/privacy" : options.getPrivacyURL();
    }

    @StyleRes
    public int authStyleForStrategy(String strategy) {
        if (authStyles.containsKey(strategy)) {
            return authStyles.get(strategy);
        }
        return AuthConfig.styleForStrategy(strategy);
    }

    @PasswordStrength
    private int parsePasswordPolicy(String policyName) {
        if ("excellent".equalsIgnoreCase(policyName)) {
            return PasswordStrength.EXCELLENT;
        } else if ("good".equalsIgnoreCase(policyName)) {
            return PasswordStrength.GOOD;
        } else if ("fair".equalsIgnoreCase(policyName)) {
            return PasswordStrength.FAIR;
        } else if ("low".equalsIgnoreCase(policyName)) {
            return PasswordStrength.LOW;
        } else {
            return PasswordStrength.NONE;
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

    public boolean allowLogIn() {
        return allowLogIn;
    }

    public boolean allowSignUp() {
        return allowSignUp;
    }

    public boolean allowForgotPassword() {
        return allowForgotPassword;
    }

    public boolean isUsernameRequired() {
        return usernameRequired;
    }

    @InitialScreen
    public int getInitialScreen() {
        return initialScreen;
    }

    @SocialButtonStyle
    public int getSocialButtonStyle() {
        return socialButtonStyle;
    }

    @UsernameStyle
    public int getUsernameStyle() {
        return usernameStyle;
    }

    @PasswordlessMode
    public int getPasswordlessMode() {
        return passwordlessMode;
    }

    @PasswordStrength
    public int getPasswordPolicy() {
        return passwordPolicy;
    }

    public boolean loginAfterSignUp() {
        return loginAfterSignUp;
    }

    public boolean hasExtraFields() {
        return !extraSignUpFields.isEmpty();
    }

    public boolean isClassicLockAvailable() {
        return classicLockAvailable;
    }

    public boolean isPasswordlessLockAvailable() {
        return passwordlessLockAvailable;
    }

    @NonNull
    public String getTermsURL() {
        return termsURL;
    }

    @NonNull
    public String getPrivacyURL() {
        return privacyURL;
    }

    public boolean mustAcceptTerms() {
        return mustAcceptTerms;
    }
}