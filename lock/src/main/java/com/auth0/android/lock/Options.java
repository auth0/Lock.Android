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

package com.auth0.android.lock;

import android.os.Build;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.auth0.Auth0;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.authentication.AuthenticationAPIClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

class Options implements Parcelable {
    private static final int WITHOUT_DATA = 0x00;
    private static final int HAS_DATA = 0x01;
    private static final String KEY_AUTHENTICATION_PARAMETERS = "authenticationParameters";
    private static final String SCOPE_KEY = "scope";
    private static final String DEVICE_KEY = "device";
    private static final String SCOPE_OFFLINE_ACCESS = "offline_access";

    private Auth0 account;
    private boolean useBrowser;
    private boolean usePKCE;
    private boolean closable;
    private boolean fullscreen;
    private UsernameStyle usernameStyle;
    private boolean useCodePasswordless;
    private boolean signUpEnabled;
    private boolean changePasswordEnabled;
    private boolean loginAfterSignUp;
    private String defaultDatabaseConnection;
    private List<String> connections;
    private List<String> enterpriseConnectionsUsingWebForm;
    private HashMap<String, Object> authenticationParameters;

    public Options() {
        usernameStyle = UsernameStyle.DEFAULT;
        signUpEnabled = true;
        changePasswordEnabled = true;
        loginAfterSignUp = true;
        useCodePasswordless = true;
    }

    protected Options(Parcel in) {
        Auth0Parcelable auth0Parcelable = (Auth0Parcelable) in.readValue(Auth0Parcelable.class.getClassLoader());
        account = auth0Parcelable.getAuth0();
        useBrowser = in.readByte() != WITHOUT_DATA;
        usePKCE = in.readByte() != WITHOUT_DATA;
        closable = in.readByte() != WITHOUT_DATA;
        fullscreen = in.readByte() != WITHOUT_DATA;
        signUpEnabled = in.readByte() != WITHOUT_DATA;
        changePasswordEnabled = in.readByte() != WITHOUT_DATA;
        loginAfterSignUp = in.readByte() != WITHOUT_DATA;
        useCodePasswordless = in.readByte() != WITHOUT_DATA;
        defaultDatabaseConnection = in.readString();
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
            usernameStyle = (UsernameStyle) in.readSerializable();
        } else {
            usernameStyle = null;
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
        dest.writeByte((byte) (fullscreen ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (signUpEnabled ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (changePasswordEnabled ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (loginAfterSignUp ? HAS_DATA : WITHOUT_DATA));
        dest.writeByte((byte) (useCodePasswordless ? HAS_DATA : WITHOUT_DATA));
        dest.writeString(defaultDatabaseConnection);
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
        if (usernameStyle == null) {
            dest.writeByte((byte) (WITHOUT_DATA));
        } else {
            dest.writeByte((byte) (HAS_DATA));
            dest.writeSerializable(usernameStyle);
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

    public boolean useBrowser() {
        return useBrowser;
    }

    public void setUseBrowser(boolean useBrowser) {
        this.useBrowser = useBrowser;
    }

    public boolean usePKCE() {
        return usePKCE;
    }

    public void setUsePKCE(boolean usePKCE) {
        this.usePKCE = usePKCE;
    }

    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public UsernameStyle usernameStyle() {
        return usernameStyle;
    }

    public void setUsernameStyle(UsernameStyle usernameStyle) {
        this.usernameStyle = usernameStyle;
    }

    public boolean isSignUpEnabled() {
        return signUpEnabled;
    }

    public void setSignUpEnabled(boolean signUpEnabled) {
        this.signUpEnabled = signUpEnabled;
    }

    public boolean isChangePasswordEnabled() {
        return changePasswordEnabled;
    }

    public void setChangePasswordEnabled(boolean changePasswordEnabled) {
        this.changePasswordEnabled = changePasswordEnabled;
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
}