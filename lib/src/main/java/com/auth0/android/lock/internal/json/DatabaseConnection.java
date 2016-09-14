package com.auth0.android.lock.internal.json;

import com.auth0.android.lock.internal.AuthType;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.android.util.CheckHelper.checkArgument;

public class DatabaseConnection extends Connection {

    private static final int MIN_USERNAME_LENGTH = 1;
    private static final int MAX_USERNAME_LENGTH = 15;
    private int minUsernameLength;
    private int maxUsernameLength;

    /**
     * Creates a new DatabaseConnection instance
     *
     * @param values   Connection values
     */
    public DatabaseConnection(Map<String, Object> values) {
        super("auth0", values);
        parseUsernameLength();
    }

    private void parseUsernameLength() {
        HashMap<String, Object> validations = getValueForKey("validation");
        if (validations == null || !validations.containsKey("username")) {
            minUsernameLength = MIN_USERNAME_LENGTH;
            maxUsernameLength = MAX_USERNAME_LENGTH;
            return;
        }
        final HashMap<String, Object> usernameValidation = (HashMap<String, Object>) validations.get("username");
        minUsernameLength = (int) usernameValidation.get("min");
        maxUsernameLength = (int) usernameValidation.get("max");
        if (minUsernameLength < MIN_USERNAME_LENGTH || minUsernameLength > maxUsernameLength) {
            minUsernameLength = MIN_USERNAME_LENGTH;
            maxUsernameLength = MAX_USERNAME_LENGTH;
        }
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
