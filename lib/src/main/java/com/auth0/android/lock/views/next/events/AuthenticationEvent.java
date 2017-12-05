package com.auth0.android.lock.views.next.events;

import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public class AuthenticationEvent {

    public static final int USERNAME_PASSWORD_LOGIN = 0;
    public static final int FORGOT_PASSWORD = 1;
    public static final int WEBAUTH_LOGIN = 2;

    @Retention(SOURCE)
    @IntDef({USERNAME_PASSWORD_LOGIN, FORGOT_PASSWORD, WEBAUTH_LOGIN})
    public @interface Action {

    }

    @Action
    private final int action;

    private final String identity;
    private final String password;
    private final String connection;

    private AuthenticationEvent(@Action int action, String identity, String password, String connection) {
        this.action = action;
        this.identity = identity;
        this.password = password;
        this.connection = connection;
    }

    public static AuthenticationEvent webAuthLogin(@NonNull String connection, @Nullable String identity) {
        return new AuthenticationEvent(WEBAUTH_LOGIN, identity, null, connection);
    }

    public static AuthenticationEvent realmLogin(@NonNull String identity, @NonNull String password) {
        return new AuthenticationEvent(USERNAME_PASSWORD_LOGIN, identity, password, null);
    }

    public static AuthenticationEvent forgotPassword(@NonNull String email) {
        return new AuthenticationEvent(FORGOT_PASSWORD, email, null, null);
    }

    @Action
    public int getAction() {
        return action;
    }

    public String getIdentity() {
        return identity;
    }

    public String getPassword() {
        return password;
    }

    public String getConnection() {
        return connection;
    }
}
