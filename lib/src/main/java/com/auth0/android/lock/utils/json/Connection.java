package com.auth0.android.lock.utils.json;

import android.support.annotation.NonNull;

import com.auth0.android.lock.enums.AuthType;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.auth0.android.util.CheckHelper.checkArgument;

public class Connection {

    private final String strategy;
    private final String name;
    private final Map<String, Object> values;

    /**
     * Creates a new connection instance
     *
     * @param values Connection values
     */
    public Connection(@NonNull String strategy, Map<String, Object> values) {
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
    public int getType() {
        switch (strategy) {
            case "auth0":
                return AuthType.DATABASE;
            case "ad":
            case "adfs":
            case "auth0-adldap":
            case "custom":
            case "google-apps":
            case "google-openid":
            case "ip":
            case "mscrm":
            case "office365":
            case "pingfederate":
            case "samlp":
            case "sharepoint":
            case "waad":
                return AuthType.ENTERPRISE;
            case "sms":
            case "email":
                return AuthType.PASSWORDLESS;
            default:
                return AuthType.SOCIAL;
        }
    }

    /**
     * Returns if this Connection can use Resource Owner to authenticate
     *
     * @return if the Active Flow (Resource Owner) is enabled.
     */
    public boolean isActiveFlowEnabled() {
        return "ad".equals(strategy) || "adfs".equals(strategy) || "waad".equals(strategy);
    }

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
     * If this connection is of type Enterprise it will return the configured Domains.
     *
     * @return a set with all domains configured
     */
    public Set<String> getDomainSet() {
        Set<String> domains = new HashSet<>();
        String domain = getValueForKey("domain");
        if (domain != null) {
            domains.add(domain.toLowerCase());
            List<String> aliases = getValueForKey("domain_aliases");
            if (aliases != null) {
                for (String alias : aliases) {
                    domains.add(alias.toLowerCase());
                }
            }
        }
        return domains;
    }
}
