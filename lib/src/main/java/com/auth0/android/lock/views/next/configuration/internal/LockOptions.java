package com.auth0.android.lock.views.next.configuration.internal;

import android.support.annotation.NonNull;

import java.util.Collections;
import java.util.List;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public class LockOptions {

    private static final String TAG = LockOptions.class.getSimpleName();

    @IdentityStyle
    private int identityStyle;
    private List<DatabaseConnection> databaseConnections = Collections.emptyList();
    private List<OAuthConnection> oauthConnections = Collections.emptyList();

    @IdentityStyle
    public int getIdentityStyle() {
        return identityStyle;
    }

    public void setIdentityStyle(@IdentityStyle int identityStyle) {
        this.identityStyle = identityStyle;
    }

    @NonNull
    public List<DatabaseConnection> getDatabaseConnections() {
        return databaseConnections;
    }

    public void setDatabaseConnections(@NonNull List<DatabaseConnection> databaseConnections) {
        this.databaseConnections = databaseConnections;
    }

    @NonNull
    public List<OAuthConnection> getOAuthConnections() {
        return oauthConnections;
    }

    public void setOauthConnections(@NonNull List<OAuthConnection> oauthConnections) {
        this.oauthConnections = oauthConnections;
    }

}
