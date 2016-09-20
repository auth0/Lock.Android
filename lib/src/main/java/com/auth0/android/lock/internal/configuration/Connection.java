package com.auth0.android.lock.internal.configuration;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.auth0.android.util.CheckHelper.checkArgument;

public class Connection implements BaseConnection, DatabaseConnection, OAuthConnection, PasswordlessConnection {

    private String strategy;
    private String name;
    private Map<String, Object> values;
    private int minUsernameLength;
    private int maxUsernameLength;

    private Connection(@NonNull String strategy, Map<String, Object> values) {
        checkArgument(values != null && values.size() > 0, "Must have at least one value");
        final String name = (String) values.remove("name");
        checkArgument(name != null, "Must have a non-null name");
        this.strategy = strategy;
        this.name = name;
        this.values = values;
        parseUsernameLength();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getStrategy() {
        return strategy;
    }

    /**
     * Getter for the Type of Connection
     *
     * @return the connection Type.
     */
    int getType() {
        switch (strategy) {
            case "auth0":
                return AuthType.DATABASE;
            case "sms":
            case "email":
                return AuthType.PASSWORDLESS;
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
            default:
                return AuthType.SOCIAL;
        }
    }

    @SuppressWarnings("unchecked")
    public <T> T getValueForKey(String key) {
        return (T) this.values.get(key);
    }

    @Override
    public boolean booleanForKey(String key) {
        Boolean value = getValueForKey(key);
        if (value == null) {
            return false;
        }
        return value;
    }

    @PasswordStrength
    public int getPasswordPolicy() {
        String value = getValueForKey("passwordPolicy");
        if ("excellent".equals(value)) {
            return PasswordStrength.EXCELLENT;
        }
        if ("good".equals(value)) {
            return PasswordStrength.GOOD;
        }
        if ("fair".equals(value)) {
            return PasswordStrength.FAIR;
        }
        if ("low".equals(value)) {
            return PasswordStrength.LOW;
        }
        return PasswordStrength.NONE;
    }

    @Override
    public boolean requiresUsername() {
        return booleanForKey("requires_username");
    }

    @Override
    public boolean showSignUp() {
        return booleanForKey("showSignup");
    }

    @Override
    public boolean showForgot() {
        return booleanForKey("showForgot");
    }

    @Override
    public int getMinUsernameLength() {
        return minUsernameLength;
    }

    @Override
    public int getMaxUsernameLength() {
        return maxUsernameLength;
    }

    @Override
    public boolean isActiveFlowEnabled() {
        return "ad".equals(getStrategy()) || "adfs".equals(getStrategy()) || "waad".equals(getStrategy());
    }

    @Override
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

    /**
     * Creates a new Connection given a Strategy name and the map of values.
     *
     * @param strategy strategy name for this connection
     * @param values   additional values associated to this connection
     * @return a new instance of Connection. Can be either DatabaseConnection, PasswordlessConnection or OAuthConnection.
     */
    public static Connection connectionFor(@NonNull String strategy, Map<String, Object> values) {
        return new Connection(strategy, values);
    }

    private void parseUsernameLength() {
        Map<String, Object> validations = getValueForKey("validation");
        if (validations == null || !validations.containsKey("username")) {
            minUsernameLength = MIN_USERNAME_LENGTH;
            maxUsernameLength = MAX_USERNAME_LENGTH;
            return;
        }
        final Map<String, Object> usernameValidation = (Map<String, Object>) validations.get("username");
        minUsernameLength = intValue(usernameValidation.get("min"));
        maxUsernameLength = intValue(usernameValidation.get("max"));
        if (minUsernameLength < MIN_USERNAME_LENGTH || minUsernameLength > maxUsernameLength) {
            minUsernameLength = MIN_USERNAME_LENGTH;
            maxUsernameLength = MAX_USERNAME_LENGTH;
        }
    }

    private int intValue(@Nullable Object object) {
        int value = 0;
        try {
            value = object == null ? 0 : (int) Double.parseDouble(String.valueOf(object));
        } catch (Exception ignored) {
        }
        return value;
    }

}
