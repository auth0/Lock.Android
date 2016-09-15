package com.auth0.android.lock.internal.json;

import android.support.annotation.Nullable;

import com.auth0.android.lock.internal.AuthType;
import com.auth0.android.lock.internal.PasswordStrength;

import java.util.Map;

public class DatabaseConnection extends Connection {

    private static final int MIN_USERNAME_LENGTH = 1;
    private static final int MAX_USERNAME_LENGTH = 15;
    private int minUsernameLength;
    private int maxUsernameLength;

    /**
     * Creates a new DatabaseConnection instance
     *
     * @param values Connection values
     */
    DatabaseConnection(Map<String, Object> values) {
        super("auth0", values);
        parseUsernameLength();
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

    public boolean requiresUsername() {
        return booleanForKey("requires_username");
    }

    public boolean showSignup() {
        return booleanForKey("showSignup");
    }

    public boolean showForgot() {
        return booleanForKey("showForgot");
    }

    public int getMinUsernameLength() {
        return minUsernameLength;
    }

    public int getMaxUsernameLength() {
        return maxUsernameLength;
    }

    @Override
    public int getType() {
        return AuthType.DATABASE;
    }
}
