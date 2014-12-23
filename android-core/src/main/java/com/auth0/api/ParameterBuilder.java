package com.auth0.api;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by hernan on 12/2/14.
 */
public class ParameterBuilder {

    public static final String SCOPE_OPENID = "openid";
    public static final String SCOPE_OFFLINE_ACCESS = "openid offline_access";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String ACCESS_TOKEN = "access_token";

    private Map<String, String> parameters;

    public ParameterBuilder() {
        this.parameters = new HashMap<String, String>();
        setScope(SCOPE_OFFLINE_ACCESS);
    }

    public ParameterBuilder(Map<String, String> parameters) {
        checkArgument(parameters != null, "Must provide non-null parameters");
        this.parameters = new HashMap<String, String>(parameters);
    }

    public ParameterBuilder setClientId(String clientId) {
        return set("client_id", clientId);
    }

    public ParameterBuilder setGrantType(String grantType) {
        return set("grant_type", grantType);
    }

    public ParameterBuilder setConnection(String connection) {
        return set("connection", connection);
    }

    public ParameterBuilder setScope(String scope) {
        if (scope.contains("offline_access")) {
            setDevice(Build.MODEL);
        } else {
            setDevice(null);
        }
        return set("scope", scope);
    }

    public ParameterBuilder setDevice(String device) {
        return set("device", device);
    }

    public ParameterBuilder setAccessToken(String accessToken) {
        return set(ACCESS_TOKEN, accessToken);
    }

    public ParameterBuilder set(String key, String value) {
        this.parameters.put(key, value);
        return this;
    }

    public ParameterBuilder addAll(Map<String, String> parameters) {
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    public Map<String, String> asDictionary() {
        return new HashMap<String, String>(this.parameters);
    }

    public static ParameterBuilder newBuilder() {
        return new ParameterBuilder();
    }

    public static ParameterBuilder newBuilder(Map<String, String> parameters) {
        return new ParameterBuilder(parameters);
    }

}
