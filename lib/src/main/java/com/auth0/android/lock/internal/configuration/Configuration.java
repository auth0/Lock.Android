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

package com.auth0.android.lock.internal.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Log;

import com.auth0.android.lock.AuthButtonSize;
import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.lock.utils.CustomField;
import com.auth0.android.lock.views.AuthConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration class to resolve which connections are available after parsing the local options.
 * Disclaimer: The classes in the internal package may change in the future. Don't use them directly.
 */
public class Configuration {

    private static final String TAG = Configuration.class.getSimpleName();

    private DatabaseConnection defaultDatabaseConnection;
    private List<PasswordlessConnection> passwordlessConnections;
    private List<OAuthConnection> socialConnections;
    private List<OAuthConnection> enterpriseConnections;

    private boolean allowLogIn;
    private boolean allowSignUp;
    private boolean allowForgotPassword;
    private boolean allowShowPassword;
    private boolean usernameRequired;
    private boolean mustAcceptTerms;
    private boolean useLabeledSubmitButton;
    private boolean hideMainScreenTitle;
    private boolean passwordlessAutoSubmit;
    @UsernameStyle
    private int usernameStyle;
    @AuthButtonSize
    private int socialButtonStyle;
    private boolean loginAfterSignUp;
    @PasswordlessMode
    private int passwordlessMode;
    @InitialScreen
    private int initialScreen;
    private String termsURL;
    private String privacyURL;
    private String supportURL;
    private List<CustomField> extraSignUpFields;
    private Map<String, Integer> authStyles;

    public Configuration(List<Connection> connections, Options options) {
        List<String> allowedConnections = options.getConnections();
        String defaultDatabaseName = options.getDefaultDatabaseConnection();
        Set<String> connectionSet = allowedConnections != null ? new HashSet<>(allowedConnections) : new HashSet<String>();
        this.defaultDatabaseConnection = filterDatabaseConnections(connections, connectionSet, defaultDatabaseName);

        List<String> webAuthEnabledConnections = options.getEnterpriseConnectionsUsingWebForm();
        Set<String> webAuthEnabledConnectionSet = webAuthEnabledConnections != null ? new HashSet<>(webAuthEnabledConnections) : new HashSet<String>();
        List<OAuthConnection> allEnterprise = filterConnections(connections, connectionSet, AuthType.ENTERPRISE);
        this.enterpriseConnections = enableWebAuthentication(allEnterprise, webAuthEnabledConnectionSet);

        this.passwordlessConnections = filterConnections(connections, connectionSet, AuthType.PASSWORDLESS);
        this.socialConnections = filterConnections(connections, connectionSet, AuthType.SOCIAL);
        parseLocalOptions(options);
    }

    @NonNull
    public List<CustomField> getExtraSignUpFields() {
        return extraSignUpFields;
    }

    @Nullable
    public DatabaseConnection getDatabaseConnection() {
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
    private <T extends BaseConnection> List<T> filterConnections(@NonNull List<Connection> connections, Set<String> allowedConnections, @AuthType int type) {
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

    @NonNull
    private List<OAuthConnection> enableWebAuthentication(@NonNull List<OAuthConnection> connections, @NonNull Set<String> webAuthEnabledConnections) {
        for (OAuthConnection c : connections) {
            if (webAuthEnabledConnections.contains(c.getName())) {
                ((Connection) c).disableActiveFlow();
            }
        }
        return connections;
    }

    private void parseLocalOptions(Options options) {
        usernameStyle = options.usernameStyle();
        socialButtonStyle = options.authButtonSize();
        loginAfterSignUp = options.loginAfterSignUp();
        mustAcceptTerms = options.mustAcceptTerms();
        useLabeledSubmitButton = options.useLabeledSubmitButton();
        hideMainScreenTitle = options.hideMainScreenTitle();
        passwordlessAutoSubmit = options.rememberLastPasswordlessAccount();

        authStyles = options.getAuthStyles();
        extraSignUpFields = options.getCustomFields();

        if (getDatabaseConnection() != null) {
            allowLogIn = options.allowLogIn();
            allowSignUp = options.allowSignUp() && getDatabaseConnection().showSignUp();
            //let user disable password reset only if connection have enabled it.
            allowForgotPassword = getDatabaseConnection().showForgot() && options.allowForgotPassword();
            usernameRequired = getDatabaseConnection().requiresUsername();

            initialScreen = options.initialScreen();
        }

        allowShowPassword = options.allowShowPassword();
        passwordlessMode = parsePasswordlessMode(options.useCodePasswordless());

        this.termsURL = options.getTermsURL() == null ? "https://auth0.com/terms" : options.getTermsURL();
        this.privacyURL = options.getPrivacyURL() == null ? "https://auth0.com/privacy" : options.getPrivacyURL();
        this.supportURL = options.getSupportURL();
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
        PasswordlessConnection connection = getPasswordlessConnection();
        if (connection != null) {
            if (connection.getName().equals("email")) {
                mode = requestCode ? PasswordlessMode.EMAIL_CODE : PasswordlessMode.EMAIL_LINK;
            } else if (connection.getName().equals("sms")) {
                mode = requestCode ? PasswordlessMode.SMS_CODE : PasswordlessMode.SMS_LINK;
            }
        }
        return mode;
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

    public boolean allowShowPassword() {
        return allowShowPassword;
    }

    public boolean isUsernameRequired() {
        return usernameRequired;
    }

    @InitialScreen
    public int getInitialScreen() {
        return initialScreen;
    }

    @AuthButtonSize
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
        return defaultDatabaseConnection == null ? PasswordStrength.NONE : defaultDatabaseConnection.getPasswordPolicy();
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

    @Nullable
    public String getSupportURL() {
        return supportURL;
    }

    public boolean mustAcceptTerms() {
        return mustAcceptTerms;
    }

    public boolean useLabeledSubmitButton() {
        return useLabeledSubmitButton;
    }

    public boolean hideMainScreenTitle() {
        return hideMainScreenTitle;
    }

    public boolean usePasswordlessAutoSubmit() {
        return passwordlessAutoSubmit;
    }
}