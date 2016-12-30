package com.auth0.android.lock;

import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.request.AuthenticationRequest;
import com.auth0.android.result.Credentials;

import java.util.HashMap;
import java.util.Map;

public class MockAuthenticationRequest implements AuthenticationRequest {

    String grantType;
    String connection;
    String scope;
    String audience;
    String device;
    String accessToken;
    HashMap<String, Object> parameters;
    BaseCallback<Credentials, AuthenticationException> callback;
    boolean executed;
    boolean started;

    @Override
    public AuthenticationRequest setGrantType(String grantType) {
        this.grantType = grantType;
        return this;
    }

    @Override
    public AuthenticationRequest setConnection(String connection) {
        this.connection = connection;
        return this;
    }

    @Override
    public AuthenticationRequest setScope(String scope) {
        this.scope = scope;
        return this;
    }

    @Override
    public AuthenticationRequest setDevice(String device) {
        this.device = device;
        return this;
    }

    @Override
    public AuthenticationRequest setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    @Override
    public AuthenticationRequest setAccessToken(String accessToken) {
        this.accessToken = accessToken;
        return this;
    }

    @Override
    public AuthenticationRequest addAuthenticationParameters(Map<String, Object> parameters) {
        this.parameters = new HashMap<>(parameters);
        return this;
    }

    @Override
    public void start(BaseCallback<Credentials, AuthenticationException> callback) {
        this.callback = callback;
        this.started = true;
    }

    @Override
    public Credentials execute() throws Auth0Exception {
        this.executed = true;
        return null;
    }
}
