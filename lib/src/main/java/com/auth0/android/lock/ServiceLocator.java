package com.auth0.android.lock;

import android.support.annotation.NonNull;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public class ServiceLocator {

    private static Bus bus;
    private static AuthenticationAPIClient apiClient;
    private static Auth0 account;

    public static AuthenticationAPIClient getAPIClient() {
        if (apiClient == null) {
            throw new IllegalStateException("API Client never set. Call ServiceLocator.setAPIClient() first");
        }
        return apiClient;
    }

    public static Auth0 getAccount() {
        if (account == null) {
            throw new IllegalStateException("Account never set. Call ServiceLocator.setAccount() first");
        }
        return account;
    }

    public static Bus getBus() {
        if (bus == null) {
            bus = new Bus(ThreadEnforcer.MAIN);
        }
        return bus;
    }

    public static void clear() {
        bus = null;
        apiClient = null;
        account = null;
    }

    public static void setAPIClient(@NonNull AuthenticationAPIClient client) {
        apiClient = client;
    }

    public static void setAccount(@NonNull Auth0 account) {
        ServiceLocator.account = account;
    }
}
