package com.auth0.android.lock.internal.configuration;

import android.support.annotation.Nullable;

public class PasswordComplexity {

    private final int policy;
    private final Integer minLengthOverride;

    public PasswordComplexity(@PasswordStrength int policy, @Nullable Integer minLengthOverride) {
        this.policy = policy;
        this.minLengthOverride = minLengthOverride;
    }

    /**
     * Getter for the Password Policy associated to this connection.
     *
     * @return the Password Policy level for this connection.
     */
    @PasswordStrength
    public int getPasswordPolicy() {
        return policy;
    }

    /**
     * Getter for the minimum length the password requires
     *
     * @return the minimum length the password requires
     */
    @Nullable
    public Integer getMinLengthOverride() {
        return minLengthOverride;
    }
}
