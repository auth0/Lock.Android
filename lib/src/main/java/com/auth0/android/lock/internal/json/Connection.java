package com.auth0.android.lock.internal.json;

import android.support.annotation.NonNull;

import com.auth0.android.lock.internal.AuthType;

import java.util.Map;

import static com.auth0.android.util.CheckHelper.checkArgument;

public abstract class Connection {

    private String strategy;
    private String name;
    private Map<String, Object> values;

    protected Connection(@NonNull String strategy, Map<String, Object> values) {
        checkArgument(values != null && values.size() > 0, "Must have at least one value");
        final String name = (String) values.remove("name");
        checkArgument(name != null, "Must have a non-null name");
        this.strategy = strategy;
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public String getStrategy() {
        return strategy;
    }

    @AuthType
    public abstract int getType();

    /**
     * Returns a value using its key
     *
     * @param key a key
     * @param <T> type of value to return
     * @return a value
     */
    @SuppressWarnings("unchecked")
    public <T> T getValueForKey(String key) {
        return (T) this.values.get(key);
    }

    /**
     * Returns a boolean value using its key
     *
     * @param key a key
     * @return the value of the flag
     */
    public boolean booleanForKey(String key) {
        Boolean value = getValueForKey(key);
        if (value == null) {
            return false;
        }
        return value;
    }

    /**
     * Creates a new Connection given a Strategy name and the map of values.
     *
     * @param strategy strategy name for this connection
     * @param values   additional values associated to this connection
     * @return a new instance of Connection. Can be either DatabaseConnection, PasswordlessConnection or OAuthConnection.
     */
    public static Connection connectionFor(@NonNull String strategy, Map<String, Object> values) {
        switch (strategy) {
            case "auth0":
                return new DatabaseConnection(values);
            case "sms":
            case "email":
                return new PasswordlessConnection(strategy, values);
            default:
                return new OAuthConnection(strategy, values);
        }
    }
}
