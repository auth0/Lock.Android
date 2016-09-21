package com.auth0.lock.util;

import android.support.annotation.Nullable;

import com.auth0.core.Connection;

import java.util.Map;


public class UsernameLengthParser {

    private static final int MIN_USERNAME_LENGTH = 1;
    private static final int MAX_USERNAME_LENGTH = 15;
    private int minLength = MIN_USERNAME_LENGTH;
    private int maxLength = MAX_USERNAME_LENGTH;

    public UsernameLengthParser(@Nullable Connection dbConnection) {
        if (dbConnection != null) {
            parseUsernameLength(dbConnection);
        }
    }

    private void parseUsernameLength(@Nullable Connection connection) {
        if (connection == null || !connection.getValues().containsKey("validation")) {
            return;
        }
        Map<String, Object> validations = (Map<String, Object>) connection.getValues().get("validation");
        if (validations == null || !validations.containsKey("username")) {
            return;
        }
        final Map<String, Object> usernameValidation = (Map<String, Object>) validations.get("username");
        minLength = intValue(usernameValidation.get("min"), MIN_USERNAME_LENGTH);
        maxLength = intValue(usernameValidation.get("max"), MAX_USERNAME_LENGTH);
        if (minLength < MIN_USERNAME_LENGTH || minLength > maxLength) {
            minLength = MIN_USERNAME_LENGTH;
            maxLength = MAX_USERNAME_LENGTH;
        }
    }

    private int intValue(@Nullable Object object, int defaultValue) {
        if (object instanceof Number) {
            return ((Number) object).intValue();
        }
        if (object instanceof String) {
            return Integer.parseInt((String) object, 10);
        }
        return defaultValue;
    }

    /**
     * Getter for the minimum username length. If it wasn't specified by this connection, it will default to 1.
     *
     * @return the minimum username length.
     */
    public int getMinLength() {
        return minLength;
    }

    /**
     * Getter for the maximum username length. If it wasn't specified by this connection, it will default to 15.
     *
     * @return the maximum username length.
     */
    public int getMaxLength() {
        return maxLength;
    }
}
