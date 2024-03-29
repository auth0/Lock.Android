/*
 * DbConnectionEvent.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.events;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DatabaseLoginEvent extends DatabaseEvent {

    private final String password;
    private String mfaOTP;
    private String mfaOOBCode;
    private String mfaToken;
    private String mfaChallengeType;

    public DatabaseLoginEvent(@NonNull String usernameOrEmail, @NonNull String password) {
        super(usernameOrEmail);
        this.password = password;
    }

    @NonNull
    public String getUsernameOrEmail() {
        return getEmail() != null ? getEmail() : getUsername();
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    public void setMultifactorOTP(@NonNull String code) {
        this.mfaOTP = code;
    }

    @Nullable
    public String getMultifactorOTP() {
        return mfaOTP;
    }

    public void setMultifactorOOBCode(@Nullable String code) {
        this.mfaOOBCode = code;
    }

    @Nullable
    public String getMultifactorOOBCode() {
        return mfaOOBCode;
    }

    public void setMultifactorToken(@NonNull String mfaToken) {
        this.mfaToken = mfaToken;
    }

    @Nullable
    public String getMultifactorToken() {
        return mfaToken;
    }

    public void setMultifactorChallengeType(@NonNull String challengeType) {
        this.mfaChallengeType = challengeType;
    }

    @Nullable
    public String getMultifactorChallengeType() {
        return mfaChallengeType;
    }
}
