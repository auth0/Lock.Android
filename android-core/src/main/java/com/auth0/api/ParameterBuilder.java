package com.auth0.api;

import android.os.Build;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Created by hernan on 12/2/14.
 */
public class ParameterBuilder {

    public static final String SCOPE_OPENID = "openid";
    public static final String SCOPE_OFFLINE_ACCESS = "openid offline_access";
    public static final String GRANT_TYPE_PASSWORD = "password";
    public static final String ACCESS_TOKEN = "access_token";
    public static final String CONNECTION = "connection";

    private Map<String, Object> parameters;

    public ParameterBuilder() {
        this.parameters = new HashMap<>();
        setScope(SCOPE_OFFLINE_ACCESS);
    }

    public ParameterBuilder(Map<String, Object> parameters) {
        checkArgument(parameters != null, "Must provide non-null parameters");
        this.parameters = new HashMap<>(parameters);
    }

    public ParameterBuilder setClientId(String clientId) {
        return set("client_id", clientId);
    }

    public ParameterBuilder setGrantType(String grantType) {
        return set("grant_type", grantType);
    }

    public ParameterBuilder setConnection(String connection) {
        return set(CONNECTION, connection);
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

    public ParameterBuilder set(String key, Object value) {
        this.parameters.put(key, value);
        return this;
    }

    public ParameterBuilder addAll(Map<String, String> parameters) {
        if (parameters != null) {
            this.parameters.putAll(parameters);
        }
        return this;
    }

    public ParameterBuilder clearAll() {
        parameters.clear();
        return this;
    }

    public Map<String, Object> asDictionary() {
        return new HashMap<>(this.parameters);
    }

    public static ParameterBuilder newBuilder() {
        return new ParameterBuilder();
    }

    public static ParameterBuilder newBuilder(Map<String, Object> parameters) {
        return new ParameterBuilder(parameters);
    }

}
