/*
 * LockOptions.java
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

package com.auth0.android.lock.internal.configuration;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.util.Patterns;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.lock.Auth0Parcelable;
import com.auth0.android.lock.AuthButtonSize;
import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.lock.utils.CustomField;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class to resolve Local settings for configuring the Lock Widget.
 * <p>
 * Disclaimer: The classes in the internal package may change in the future. Don't use them directly.
 */
public class Options implements Parcelable {
    private static final int WITHOUT_DATA = 0x00;
    private static final int HAS_DATA = 0x01;
    private static final String KEY_AUTHENTICATION_PARAMETERS = "authenticationParameters";
    private static final String KEY_CONNECTIONS_SCOPE = "connectionsScope";
    private static final String SCOPE_KEY = "scope";
    private static final String DEVICE_KEY = "device";
    private static final String SCOPE_OFFLINE_ACCESS = "offline_access";

    private Auth0 account;
    private boolean useBrowser;
    private boolean usePKCE;
    private boolean closable;
    private int authButtonSize;
    private int usernameStyle;
    private boolean useCodePasswordless;
    private boolean allowLogIn;
    private boolean allowSignUp;
    private boolean allowForgotPassword;
    private boolean loginAfterSignUp;
    private boolean mustAcceptTerms;
    private boolean useLabeledSubmitButton;
    private boolean hideMainScreenTitle;
    private boolean passwordlessAutoSubmit;
    private String defaultDatabaseConnection;
    private List<String> connections;
    private List<String> enterpriseConnectionsUsingWebForm;
    private HashMap<String, Integer> authStyles;
    private HashMap<String, Object> authenticationParameters;
    private HashMap<String, String> connectionsScope;
    private List<CustomField> customFields;
    private int initialScreen;
    private Theme theme;
    private String privacyURL;
    private String termsURL;
    private String scope;
    private String audience;
    private String scheme;

    public Options() {
        usernameStyle = UsernameStyle.DEFAULT;
        initialScreen = InitialScreen.LOG_IN;
        useBrowser = true;
        allowLogIn = true;
        allowSignUp = true;
        allowForgotPassword = true;
        loginAfterSignUp = true;
        useCodePasswordless = true;
        usePKCE = true;
        useLabeledSubmitButton = true;
        authenticationParameters = new HashMap<>();
        authStyles = new HashMap<>();
        connectionsScope = new HashMap<>();
        customFields = new ArrayList<>();
        theme = Theme.newBuilder().build();
    }

    protected Options(Parcel in) {
        Auth0Parcelable auth0Parcelable = (Auth0Parcelable) in.readValue(Auth0Parcelable.class.getClassLoader());
        account = auth0Parcelable.getAuth0();
        useBrowser = in.readByte() != WITHOUT_DATA;
        usePKCE = in.readByte() != WITHOUT_DATA;
        closable = in.readByte() != WITHOUT_DATA;
        allowLogIn = in.readByte() != WITHOUT_DATA;
        allowSignUp = in.readByte() != WITHOUT_DATA;
        allowForgotPassword = in.readByte() != WITHOUT_DATA;
        loginAfterSignUp = in.readByte() != WITHOUT_DATA;
        mustAcceptTerms = in.readByte() != WITHOUT_DATA;
        useCodePasswordless = in.readByte() != WITHOUT_DATA;
        useLabeledSubmitButton = in.readByte() != WITHOUT_DATA;
        hideMainScreenTitle = in.readByte() != WITHOUT_DATA;
        passwordlessAutoSubmit = in.readByte() != WITHOUT_DATA;
        defaultDatabaseConnection = in.readString();
        usernameStyle = in.readInt();
        initialScreen = in.readInt();
        authButtonSize = in.readInt();
        theme = in.readParcelable(Theme.class.getClassLoader());
        privacyURL = in.readString();
        termsURL = in.readString();
        scope = in.readString();
        audience = in.readString();
        scheme = in.readString();
        if (in.readByte() == HAS_DATA) {
            connections = new ArrayList<>();
            in.readList(connections, String.class.getClassLoader());
        } else {
            connections = null;
        }
        if (in.readByte() == HAS_DATA) {
            enterpriseConnectionsUsingWebForm = new ArrayList<>();
            in.readList(enterpriseConnectionsUsingWebForm, String.class.getClassLoader());
        } else {
            enterpriseConnectionsUsingWebForm = null;
        }
        if (in.readByte() == HAS_DATA) {
            // FIXME this is something to improve
            Bundle mapBundle = in.readBundle();
            authenticationParameters = (HashMap<String, Object>) mapBundle.getSerializable(KEY_AUTHENTICATION_PARAMETERS);
        } else {
            authenticationParameters = null;
        }
        if (in.readByte() == HAS_DATA) {
            List<String> authStylesKeys = new ArrayList<>();
            List<Integer> authStylesValues = new ArrayList<>();
            in.readList(authStylesKeys, String.class.getClassLoader());
            in.readList(authStylesValues, Integer.class.getClassLoader());
            authStyles = new HashMap<>();
            for (int i = 0; i < authStylesKeys.size(); i++) {
                authStyles.put(authStylesKeys.get(i), authStylesValues.get(i));
            }
        } else {
            authStyles = null;
        }
        if (in.readByte() == HAS_DATA) {
            // FIXME this is something to improve
            Bundle mapBundle = in.readBundle();
            connectionsScope = (HashMap<String, String>) mapBundle.getSerializable(KEY_CONNECTIONS_SCOPE);
        } else {
            connectionsScope = null;
        }
        if (in.readByte() == HAS_DATA) {
            customFields = new ArrayList<>();
            in.readList(customFields, CustomField.class.getClassLoader());
        } else {
            customFields = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(new Auth0Parcelable(account));
        dest.writeByte((byte) (useBrowser ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (usePKCE ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (closable ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (allowLogIn ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (allowSignUp ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (allowForgotPassword ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (loginAfterSignUp ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (mustAcceptTerms ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (useCodePasswordless ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (useLabeledSubmitButton ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (hideMainScreenTitle ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (passwordlessAutoSubmit ? HAS_DATA : WITHOUT_DATA));
        dest.writeString(defaultDatabaseConnection);
        dest.writeInt(usernameStyle);
        dest.writeInt(initialScreen);
        dest.writeInt(authButtonSize);
        dest.writeParcelable(theme, flags);
        dest.writeString(privacyURL);
        dest.writeString(termsURL);
        dest.writeString(scope);
        dest.writeString(audience);
        dest.writeString(scheme);
        if (connections == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            dest.writeList(connections);
        }
        if (enterpriseConnectionsUsingWebForm == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            dest.writeList(enterpriseConnectionsUsingWebForm);
        }
        if (authenticationParameters == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            // FIXME this is something to improve
            Bundle mapBundle = new Bundle();
            mapBundle.putSerializable(KEY_AUTHENTICATION_PARAMETERS, authenticationParameters);
            dest.writeBundle(mapBundle);
        }
        if (authStyles == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            dest.writeList(new ArrayList<>(authStyles.keySet()));
            dest.writeList(new ArrayList<>(authStyles.values()));
        }
        if (connectionsScope == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            // FIXME this is something to improve
            Bundle mapBundle = new Bundle();
            mapBundle.putSerializable(KEY_CONNECTIONS_SCOPE, connectionsScope);
            dest.writeBundle(mapBundle);
        }
        if (customFields == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            dest.writeList(customFields);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Options> CREATOR = new Parcelable.Creator<Options>() {
        @Override
        public Options createFromParcel(Parcel in) {
            return new Options(in);
        }

        @Override
        public Options[] newArray(int size) {
            return new Options[size];
        }
    };

    public Auth0 getAccount() {
        return account;
    }

    public void setAccount(Auth0 account) {
        this.account = account;
    }

    @Deprecated
    public boolean useBrowser() {
        return useBrowser;
    }

    @Deprecated
    public void setUseBrowser(boolean useBrowser) {
        this.useBrowser = useBrowser;
    }

    public void withTheme(Theme theme) {
        this.theme = theme;
    }

    public Theme getTheme() {
        return theme;
    }

    @Deprecated
    public boolean usePKCE() {
        return usePKCE;
    }

    @Deprecated
    public void setUsePKCE(boolean usePKCE) {
        this.usePKCE = usePKCE;
    }

    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    @UsernameStyle
    public int usernameStyle() {
        return usernameStyle;
    }

    public void setUsernameStyle(@UsernameStyle int usernameStyle) {
        this.usernameStyle = usernameStyle;
    }

    public void setAllowLogIn(boolean allowLogIn) {
        this.allowLogIn = allowLogIn;
    }

    public void setAuthButtonSize(@AuthButtonSize int authButtonSize) {
        this.authButtonSize = authButtonSize;
    }

    @AuthButtonSize
    public int authButtonSize() {
        return authButtonSize;
    }

    public boolean allowLogIn() {
        return allowLogIn;
    }

    public boolean allowSignUp() {
        return allowSignUp;
    }

    public void setAllowSignUp(boolean allowSignUp) {
        this.allowSignUp = allowSignUp;
    }

    public void setAllowForgotPassword(boolean allowForgotPassword) {
        this.allowForgotPassword = allowForgotPassword;
    }

    public boolean allowForgotPassword() {
        return allowForgotPassword;
    }

    public String getDefaultDatabaseConnection() {
        return defaultDatabaseConnection;
    }

    public void useDatabaseConnection(String defaultDatabaseConnection) {
        this.defaultDatabaseConnection = defaultDatabaseConnection;
    }

    public List<String> getConnections() {
        return connections;
    }

    public void setConnections(List<String> connections) {
        this.connections = connections;
    }

    public List<String> getEnterpriseConnectionsUsingWebForm() {
        return enterpriseConnectionsUsingWebForm;
    }

    public void setEnterpriseConnectionsUsingWebForm(List<String> enterpriseConnectionsUsingWebForm) {
        this.enterpriseConnectionsUsingWebForm = enterpriseConnectionsUsingWebForm;
    }

    @NonNull
    public HashMap<String, Object> getAuthenticationParameters() {
        return authenticationParameters;
    }

    public void setAuthenticationParameters(@NonNull HashMap<String, Object> authenticationParameters) {
        final String scope = (String) authenticationParameters.get(SCOPE_KEY);
        final String device = (String) authenticationParameters.get(DEVICE_KEY);

        if (scope != null && scope.contains(SCOPE_OFFLINE_ACCESS) && device == null) {
            authenticationParameters.put(DEVICE_KEY, Build.MODEL);
        }
        this.authenticationParameters = authenticationParameters;
    }

    public boolean loginAfterSignUp() {
        return loginAfterSignUp;
    }

    public void setLoginAfterSignUp(boolean loginAfterSignUp) {
        this.loginAfterSignUp = loginAfterSignUp;
    }

    public AuthenticationAPIClient getAuthenticationAPIClient() {
        return new AuthenticationAPIClient(account);
    }

    public void setUseCodePasswordless(boolean useCode) {
        this.useCodePasswordless = useCode;
    }

    public boolean useCodePasswordless() {
        return this.useCodePasswordless;
    }

    public void setCustomFields(@NonNull List<CustomField> customFields) {
        this.customFields = customFields;
    }

    @NonNull
    public List<CustomField> getCustomFields() {
        return customFields;
    }

    public void setInitialScreen(@InitialScreen int screen) {
        this.initialScreen = screen;
    }

    @InitialScreen
    public int initialScreen() {
        return initialScreen;
    }

    public void setPrivacyURL(@NonNull String url) throws IllegalArgumentException {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            throw new IllegalArgumentException("The given Policy Privacy URL doesn't have a valid URL format: " + url);
        }
        this.privacyURL = url;
    }

    public String getPrivacyURL() {
        return privacyURL;
    }

    public void setTermsURL(@NonNull String url) throws IllegalArgumentException {
        if (!Patterns.WEB_URL.matcher(url).matches()) {
            throw new IllegalArgumentException("The given Terms of Service URL doesn't have a valid URL format: " + url);
        }
        this.termsURL = url;
    }

    public String getTermsURL() {
        return termsURL;
    }

    public void setMustAcceptTerms(boolean mustAcceptTerms) {
        this.mustAcceptTerms = mustAcceptTerms;
    }

    public boolean mustAcceptTerms() {
        return mustAcceptTerms;
    }

    public void withAuthStyle(@NonNull String connectionName, @StyleRes int style) {
        authStyles.put(connectionName, style);
    }

    @NonNull
    public Map<String, Integer> getAuthStyles() {
        return new HashMap<>(authStyles);
    }

    public void setUseLabeledSubmitButton(boolean useLabeledSubmitButton) {
        this.useLabeledSubmitButton = useLabeledSubmitButton;
    }

    public boolean useLabeledSubmitButton() {
        return useLabeledSubmitButton;
    }

    public void setHideMainScreenTitle(boolean hideMainScreenTitle) {
        this.hideMainScreenTitle = hideMainScreenTitle;
    }

    public boolean hideMainScreenTitle() {
        return hideMainScreenTitle;
    }

    public void setPasswordlessAutoSubmit(boolean autoSubmit) {
        this.passwordlessAutoSubmit = autoSubmit;
    }

    public boolean usePasswordlessAutoSubmit() {
        return passwordlessAutoSubmit;
    }

    public void withConnectionScope(@NonNull String connectionName, @NonNull String scope) {
        connectionsScope.put(connectionName, scope);
    }

    @NonNull
    public Map<String, String> getConnectionsScope() {
        return connectionsScope;
    }

    public void withScope(@NonNull String scope) {
        this.scope = scope;
    }

    @Nullable
    public String getScope() {
        return scope;
    }

    public void withAudience(@NonNull String audience) {
        this.audience = audience;
    }

    @Nullable
    public String getAudience() {
        return audience;
    }

    public void withScheme(@NonNull String scheme) {
        this.scheme = scheme;
    }

    @Nullable
    public String getScheme() {
        return scheme;
    }
}