package com.auth0.android.lock.internal.configuration;

import androidx.annotation.NonNull;

public interface DatabaseConnection extends BaseConnection {

    int MIN_USERNAME_LENGTH = 1;
    int MAX_USERNAME_LENGTH = 15;

    /**
     * Getter for the Password Validation settings
     *
     * @return The Password Validation for this connection.
     */
    @NonNull
    PasswordComplexity getPasswordComplexity();

    /**
     * Whether this connection requires username or not.
     *
     * @return true if this connection requires username.
     */
    boolean requiresUsername();

    /**
     * Whether this connection is allowed to show the Sign Up screen.
     *
     * @return true if this connection can show the Sign Up screen.
     */
    boolean showSignUp();

    /**
     * Whether this connection is allowed to show the Forgot Password screen.
     *
     * @return true if this connection can show the Forgot Password screen.
     */
    boolean showForgot();

    /**
     * Getter for the minimum username length. Will default to 1 if not available.
     *
     * @return the minimum username length.
     */
    int getMinUsernameLength();

    /**
     * Getter for the maximum username length. Will default to 15 if not available.
     *
     * @return the maximum username length.
     */
    int getMaxUsernameLength();

    /**
     * Whether this connection is a Custom/Imported Database.
     *
     * @return true if this connection is a Custom/Imported Database.
     */
    boolean isCustomDatabase();
}
