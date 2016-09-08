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

import com.auth0.android.lock.enums.AuthType;
import com.auth0.android.lock.enums.InitialScreen;
import com.auth0.android.lock.enums.PasswordStrength;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.enums.SocialButtonStyle;
import com.auth0.android.lock.enums.Strategies;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.utils.json.Application;
import com.auth0.android.lock.utils.json.AuthData;
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

    private AuthData defaultDatabaseConnection;
    private List<AuthData> passwordlessConnections;
    private List<AuthData> socialConnections;
    private List<AuthData> enterpriseConnections;

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
        this.defaultDatabaseConnection = filterDatabaseConnections(application.getConnections(), connectionSet, defaultDatabaseName);
        this.enterpriseConnections = filterConnections(application.getConnections(), connectionSet, AuthType.ENTERPRISE);
        this.passwordlessConnections = filterConnections(application.getConnections(), connectionSet, AuthType.PASSWORDLESS);
        this.socialConnections = filterConnections(application.getConnections(), connectionSet, AuthType.SOCIAL);
        this.application = application;
        parseLocalOptions(options);
        boolean atLeastOneSocialModeEnabled = allowLogIn || allowSignUp;
        boolean atLeastOneDatabaseModeEnabled = atLeastOneSocialModeEnabled || allowForgotPassword;
        this.classicLockAvailable = (!socialConnections.isEmpty() && atLeastOneSocialModeEnabled) || (!enterpriseConnections.isEmpty() && allowLogIn)
                || (defaultDatabaseConnection != null && atLeastOneDatabaseModeEnabled);
        this.passwordlessLockAvailable = !socialConnections.isEmpty() || !passwordlessConnections.isEmpty();
    }

    @NonNull
    public List<CustomField> getExtraSignUpFields() {
        return extraSignUpFields;
    }

    public AuthData getDefaultDatabaseConnection() {
        return defaultDatabaseConnection;
    }

    @Nullable
    public AuthData getDefaultPasswordlessConnection() {
        if (passwordlessConnections.isEmpty()) {
            return null;
        }

        if (passwordlessConnections.size() == 1) {
            return passwordlessConnections.get(0);
        }

        AuthData strategy = null;
        for (AuthData s : passwordlessConnections) {
            if (s.getName().equals(Strategies.Email)) {
                strategy = s;
                break;
            }
        }

        return strategy != null ? strategy : passwordlessConnections.get(0);
    }

    public List<AuthData> getSocialConnections() {
        return socialConnections;
    }

    public List<AuthData> getEnterpriseConnections() {
        return enterpriseConnections;
    }

    public List<AuthData> getPasswordlessConnections() {
        return passwordlessConnections;
    }

    public Application getApplication() {
        return application;
    }

    private AuthData filterDatabaseConnections(@NonNull List<AuthData> connections, Set<String> allowedConnections, String defaultDatabaseName) {
        if (connections.isEmpty()) {
            return null;
        }
        final List<AuthData> filteredConnections = filterConnections(connections, allowedConnections, AuthType.DATABASE);
        for (AuthData connection : filteredConnections) {
            if (connection.getName().equals(defaultDatabaseName)) {
                return connection;
            }
        }
        Log.w(TAG, String.format("You've chosen '%s' as your default database name, but it wasn't found in your Auth0 connections configuration.", defaultDatabaseName));

        return filteredConnections.isEmpty() ? null : filteredConnections.get(0);
    }

    private List<AuthData> filterConnections(@NonNull List<AuthData> connections, Set<String> allowedConnections, @AuthType int type) {
        if (connections.isEmpty()) {
            return connections;
        }
        List<AuthData> filtered = new ArrayList<>(connections.size());
        for (AuthData connection : connections) {
            boolean allowed = allowedConnections.isEmpty() || allowedConnections.contains(connection.getName());
            if (connection.getType() == type && allowed) {
                filtered.add(connection);
            }
        }
        return filtered;
    }


    private void parseLocalOptions(Options options) {
        usernameStyle = options.usernameStyle();
        socialButtonStyle = options.socialButtonStyle();
        loginAfterSignUp = options.loginAfterSignUp();
        mustAcceptTerms = options.mustAcceptTerms();

        final boolean socialAvailable = !getSocialConnections().isEmpty();
        final boolean dbAvailable = getDefaultDatabaseConnection() != null;
        final boolean enterpriseAvailable = !getEnterpriseConnections().isEmpty();
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

        AuthData passwordlessConnection = getDefaultPasswordlessConnection();
        if (passwordlessConnection != null) {
            if (passwordlessConnection.getName().equals(Strategies.Email)) {
                passwordlessMode = options.useCodePasswordless() ? PasswordlessMode.EMAIL_CODE : PasswordlessMode.EMAIL_LINK;
            } else if (passwordlessConnection.getName().equals(Strategies.SMS)) {
                passwordlessMode = options.useCodePasswordless() ? PasswordlessMode.SMS_CODE : PasswordlessMode.SMS_LINK;
            }
        } else {
            passwordlessMode = PasswordlessMode.DISABLED;
        }

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