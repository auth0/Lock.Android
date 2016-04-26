/*
 * PasswordlessLoginEvent.java
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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.PasswordlessType;
import com.auth0.authentication.ProfileRequest;
import com.auth0.request.ParameterizableRequest;

public class PasswordlessLoginEvent {
    private static final String TAG = PasswordlessLoginEvent.class.getSimpleName();
    private static final String KEY_CONNECTION = "connection";
    private final PasswordlessMode mode;
    private final String emailOrNumber;
    private final String code;
    private final Country country;


    private PasswordlessLoginEvent(PasswordlessMode mode, String emailOrNumber, String code, Country country) {
        this.mode = mode;
        this.emailOrNumber = emailOrNumber;
        this.code = code;
        this.country = country;
    }

    public static PasswordlessLoginEvent requestCode(PasswordlessMode mode, @NonNull String email) {
        return new PasswordlessLoginEvent(mode, email, null, null);
    }

    public static PasswordlessLoginEvent requestCode(PasswordlessMode mode, @NonNull String number, @NonNull Country country) {
        String fullNumber = country.getDialCode() + number;
        return new PasswordlessLoginEvent(mode, fullNumber, null, country);
    }

    public static PasswordlessLoginEvent submitCode(PasswordlessMode mode, @NonNull String code) {
        return new PasswordlessLoginEvent(mode, null, code, null);
    }

    public PasswordlessMode getMode() {
        return mode;
    }

    @Nullable
    public String getEmailOrNumber() {
        return emailOrNumber;
    }

    @Nullable
    public Country getCountry() {
        return country;
    }

    @Nullable
    public String getCode() {
        return code;
    }

    /**
     * Creates the ParameterizableRequest that will initiate the Passwordless Authentication flow.
     *
     * @param apiClient the API Client instance
     * @return the Passwordless code request request.
     */
    public ParameterizableRequest<Void> getCodeRequest(AuthenticationAPIClient apiClient, String connectionName) {
        Log.d(TAG, String.format("Generating Passwordless Code/Link request for connection %s", connectionName));
        ParameterizableRequest<Void> request;
        if (getMode() == PasswordlessMode.EMAIL_CODE) {
            request = apiClient.passwordlessWithEmail(getEmailOrNumber(), PasswordlessType.CODE);
        } else if (getMode() == PasswordlessMode.EMAIL_LINK) {
            request = apiClient.passwordlessWithEmail(getEmailOrNumber(), PasswordlessType.LINK_ANDROID);
        } else if (getMode() == PasswordlessMode.SMS_CODE) {
            request = apiClient.passwordlessWithSMS(getEmailOrNumber(), PasswordlessType.CODE);
        } else {
            request = apiClient.passwordlessWithSMS(getEmailOrNumber(), PasswordlessType.LINK_ANDROID);
        }
        return request.addParameter(KEY_CONNECTION, connectionName);
    }

    /**
     * Creates the AuthenticationRequest that will finish the Passwordless Authentication flow.
     *
     * @param apiClient     the API Client instance
     * @param emailOrNumber the email or phone number used on the code request.
     * @return the Passwordless login request.
     */
    public ProfileRequest getLoginRequest(AuthenticationAPIClient apiClient, String emailOrNumber) {
        Log.d(TAG, String.format("Generating Passwordless Login request for identity %s", emailOrNumber));
        if (getMode() == PasswordlessMode.EMAIL_CODE || getMode() == PasswordlessMode.EMAIL_LINK) {
            return apiClient.getProfileAfter(apiClient.loginWithEmail(emailOrNumber, getCode()));
        } else {
            return apiClient.getProfileAfter(apiClient.loginWithPhoneNumber(emailOrNumber, getCode()));
        }
    }
}
