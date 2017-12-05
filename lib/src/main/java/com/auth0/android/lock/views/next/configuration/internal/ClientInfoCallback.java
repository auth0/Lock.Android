package com.auth0.android.lock.views.next.configuration.internal;

import android.support.annotation.NonNull;

import com.auth0.android.Auth0Exception;

import java.util.List;

/**
 * Created by lbalmaceda on 01/12/2017.
 */

public interface ClientInfoCallback {

    void onClientInfoReceived(@NonNull List<Connection> connections);

    void onClientInfoError(Auth0Exception exception);

}
