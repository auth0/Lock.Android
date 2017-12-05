package com.auth0.android.lock.views.next.configuration.internal;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.auth0.android.lock.internal.configuration.AuthType;
import com.auth0.android.lock.internal.configuration.PasswordStrength;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.auth0.android.util.CheckHelper.checkArgument;

public class Connection implements DatabaseConnection, OAuthConnection, PasswordlessConnection {

    private String strategy;
    private String name;
    private HashMap<String, Object> values;
    private int minUsernameLength;
    private int maxUsernameLength;
    private boolean isCustomDatabase;

    private Connection(@NonNull String strategy, Map<String, Object> values) {
        checkArgument(values != null && values.size() > 0, "Must have at least one value");
        final String name = (String) values.remove("name");
        checkArgument(name != null, "Must have a non-null name");
        this.strategy = strategy;
        this.name = name;
        this.values = new HashMap<>(values);
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
    @AuthType
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

    @Nullable
    public <T> T valueForKey(@NonNull String key, @NonNull Class<T> tClazz) {
        final Object value = this.values.get(key);
        return tClazz.isInstance(value) ? tClazz.cast(value) : null;
    }

    @Override
    public boolean booleanForKey(@NonNull String key) {
        final Boolean value = valueForKey(key, Boolean.class);
        return value != null && value;
    }

    @PasswordStrength
    public int getPasswordPolicy() {
        String value = valueForKey("passwordPolicy", String.class);
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
    public boolean isCustomDatabase() {
        return isCustomDatabase;
    }

    @Override
    public boolean isActiveFlowEnabled() {
        return "ad".equals(getStrategy()) || "adfs".equals(getStrategy()) || "waad".equals(getStrategy());
    }

    @Override
    public Set<String> getDomainSet() {
        Set<String> domains = new HashSet<>();
        String domain = valueForKey("domain", String.class);
        if (domain != null) {
            domains.add(domain.toLowerCase());
            List<String> aliases = valueForKey("domain_aliases", List.class);
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
    static Connection newConnectionFor(@NonNull String strategy, Map<String, Object> values) {
        return new Connection(strategy, values);
    }

    private void parseUsernameLength() {
        Map<String, Object> validations = valueForKey("validation", Map.class);
        if (validations == null || !validations.containsKey("username")) {
            isCustomDatabase = true;
            minUsernameLength = 1;
            maxUsernameLength = Integer.MAX_VALUE;
            return;
        }

        final Map<String, Object> usernameValidation = (Map<String, Object>) validations.get("username");
        minUsernameLength = intValue(usernameValidation.get("min"), 0);
        maxUsernameLength = intValue(usernameValidation.get("max"), 0);
        if (minUsernameLength < 1 || maxUsernameLength < 1 || minUsernameLength > maxUsernameLength) {
            minUsernameLength = 1;
            maxUsernameLength = Integer.MAX_VALUE;
        }
    }

    /**
     * Will try to get the int value of a given object. If the value cannot be obtained, it will return the default value.
     *
     * @param object       to get an int from.
     * @param defaultValue to return if the int value cannot be obtained.
     * @return the int value of the object or the default value if it cannot be obtained.
     */
    private int intValue(@Nullable Object object, int defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        if (object instanceof String) {
            return Integer.parseInt((String) object, 10);
        }
        return defaultValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.strategy);
        dest.writeString(this.name);
        dest.writeSerializable(this.values);
        dest.writeInt(this.minUsernameLength);
        dest.writeInt(this.maxUsernameLength);
        dest.writeByte(this.isCustomDatabase ? (byte) 1 : (byte) 0);
    }

    protected Connection(Parcel in) {
        this.strategy = in.readString();
        this.name = in.readString();
        this.values = (HashMap<String, Object>) in.readSerializable();
        this.minUsernameLength = in.readInt();
        this.maxUsernameLength = in.readInt();
        this.isCustomDatabase = in.readByte() != 0;
    }

    public static final Creator<Connection> CREATOR = new Creator<Connection>() {
        @Override
        public Connection createFromParcel(Parcel source) {
            return new Connection(source);
        }

        @Override
        public Connection[] newArray(int size) {
            return new Connection[size];
        }
    };
}
