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

package com.auth0.android.lock.internal;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.SocialButtonStyle;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.lock.internal.json.Connection;
import com.auth0.android.lock.internal.json.DatabaseConnection;
import com.auth0.android.lock.internal.json.OAuthConnection;
import com.auth0.android.lock.internal.json.PasswordlessConnection;
import com.auth0.android.lock.utils.CustomField;
import com.auth0.android.lock.views.AuthConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration class to resolve which connections are available after parsing the local options.
 * <p/>
 * Disclaimer: The classes in the internal package may change in the future. Don't use them directly.
 */
public class Configuration {

    private static final String TAG = Configuration.class.getSimpleName();

    private static final String SHOW_SIGNUP_KEY = "showSignup";
    private static final String SHOW_FORGOT_KEY = "showForgot";
    private static final String REQUIRES_USERNAME_KEY = "requires_username";
    private static final String PASSWORD_POLICY_KEY = "passwordPolicy";

    private DatabaseConnection defaultDatabaseConnection;
    private List<PasswordlessConnection> passwordlessConnections;
    private List<OAuthConnection> socialConnections;
    private List<OAuthConnection> enterpriseConnections;

    private boolean allowLogIn;
    private boolean allowSignUp;
    private boolean allowForgotPassword;
    private boolean usernameRequired;
    private boolean mustAcceptTerms;
    @PasswordStrength
    private int passwordPolicy;
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
    private List<CustomField> extraSignUpFields;
    private Map<String, Integer> authStyles;

    public Configuration(List<Connection> connections, Options options) {
        List<String> allowedConnections = options.getConnections();
        String defaultDatabaseName = options.getDefaultDatabaseConnection();
        Set<String> connectionSet = allowedConnections != null ? new HashSet<>(allowedConnections) : new HashSet<String>();
        this.defaultDatabaseConnection = filterDatabaseConnections(connections, connectionSet, defaultDatabaseName);
        this.enterpriseConnections = filterConnections(connections, connectionSet, AuthType.ENTERPRISE);
        this.passwordlessConnections = filterConnections(connections, connectionSet, AuthType.PASSWORDLESS);
        this.socialConnections = filterConnections(connections, connectionSet, AuthType.SOCIAL);
        parseLocalOptions(options);
    }

    @NonNull
    public List<CustomField> getExtraSignUpFields() {
        return extraSignUpFields;
    }

    @Nullable
    public Connection getDatabaseConnection() {
        return defaultDatabaseConnection;
    }

    @Nullable
    public PasswordlessConnection getPasswordlessConnection() {
        if (passwordlessConnections.isEmpty()) {
            return null;
        }

        if (passwordlessConnections.size() == 1) {
            return passwordlessConnections.get(0);
        }

        PasswordlessConnection connection = null;
        for (PasswordlessConnection c : passwordlessConnections) {
            if (c.getName().equals("email")) {
                connection = c;
                break;
            }
        }

        return connection != null ? connection : passwordlessConnections.get(0);
    }

    @NonNull
    public List<OAuthConnection> getSocialConnections() {
        return socialConnections;
    }

    @NonNull
    public List<OAuthConnection> getEnterpriseConnections() {
        return enterpriseConnections;
    }

    @NonNull
    public List<PasswordlessConnection> getPasswordlessConnections() {
        return passwordlessConnections;
    }

    @Nullable
    private DatabaseConnection filterDatabaseConnections(@NonNull List<Connection> connections, Set<String> allowedConnections, String defaultDatabaseName) {
        if (connections.isEmpty()) {
            return null;
        }
        final List<DatabaseConnection> filteredConnections = filterConnections(connections, allowedConnections, AuthType.DATABASE);
        for (DatabaseConnection connection : filteredConnections) {
            if (connection.getName().equals(defaultDatabaseName)) {
                return connection;
            }
        }
        Log.w(TAG, String.format("You've chosen '%s' as your default database name, but it wasn't found in your Auth0 connections configuration.", defaultDatabaseName));

        return filteredConnections.isEmpty() ? null : filteredConnections.get(0);
    }

    @NonNull
    private <T extends Connection> List<T> filterConnections(@NonNull List<Connection> connections, Set<String> allowedConnections, @AuthType int type) {
        if (connections.isEmpty()) {
            return (List<T>) connections;
        }
        List<T> filtered = new ArrayList<>(connections.size());
        for (Connection connection : connections) {
            boolean allowed = allowedConnections.isEmpty() || allowedConnections.contains(connection.getName());
            if (connection.getType() == type && allowed) {
                filtered.add((T) connection);
            }
        }
        return filtered;
    }


    private void parseLocalOptions(Options options) {
        usernameStyle = options.usernameStyle();
        socialButtonStyle = options.socialButtonStyle();
        loginAfterSignUp = options.loginAfterSignUp();
        mustAcceptTerms = options.mustAcceptTerms();

        authStyles = options.getAuthStyles();
        extraSignUpFields = options.getCustomFields();

        if (getDatabaseConnection() != null) {
            allowLogIn = options.allowLogIn();
            allowSignUp = options.allowSignUp() && getDatabaseConnection().booleanForKey(SHOW_SIGNUP_KEY);
            //let user disable password reset only if connection have enabled it.
            allowForgotPassword = getDatabaseConnection().booleanForKey(SHOW_FORGOT_KEY) && options.allowForgotPassword();
            usernameRequired = getDatabaseConnection().booleanForKey(REQUIRES_USERNAME_KEY);
            passwordPolicy = parsePasswordPolicy((String) getDatabaseConnection().getValueForKey(PASSWORD_POLICY_KEY));

            initialScreen = options.initialScreen();
        }

        passwordlessMode = parsePasswordlessMode(options.useCodePasswordless());

        this.termsURL = options.getTermsURL() == null ? "https://auth0.com/terms" : options.getTermsURL();
        this.privacyURL = options.getPrivacyURL() == null ? "https://auth0.com/privacy" : options.getPrivacyURL();
    }

    @StyleRes
    public int authStyleForConnection(String strategy, String connection) {
        if (authStyles.containsKey(connection)) {
            return authStyles.get(connection);
        }
        return AuthConfig.styleForStrategy(strategy);
    }

    @PasswordlessMode
    private int parsePasswordlessMode(boolean requestCode) {
        int mode = PasswordlessMode.DISABLED;
        Connection connection = getPasswordlessConnection();
        if (connection != null) {
            if (connection.getName().equals("email")) {
                mode = requestCode ? PasswordlessMode.EMAIL_CODE : PasswordlessMode.EMAIL_LINK;
            } else if (connection.getName().equals("sms")) {
                mode = requestCode ? PasswordlessMode.SMS_CODE : PasswordlessMode.SMS_LINK;
            }
        }
        return mode;
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

    public boolean hasClassicConnections() {
        return !socialConnections.isEmpty() || !enterpriseConnections.isEmpty() || defaultDatabaseConnection != null;
    }

    public boolean hasPasswordlessConnections() {
        return !socialConnections.isEmpty() || !passwordlessConnections.isEmpty();
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