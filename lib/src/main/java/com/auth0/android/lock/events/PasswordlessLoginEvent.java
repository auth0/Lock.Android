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

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.PasswordlessType;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.internal.configuration.PasswordlessMode;
import com.auth0.android.request.AuthenticationRequest;
import com.auth0.android.request.Request;

public class PasswordlessLoginEvent {
    private static final String TAG = PasswordlessLoginEvent.class.getSimpleName();
    private static final String KEY_CONNECTION = "connection";
    @PasswordlessMode
    private final int mode;
    private final String emailOrNumber;
    private final String code;
    private final Country country;


    private PasswordlessLoginEvent(@PasswordlessMode int mode, String emailOrNumber, String code, Country country) {
        this.mode = mode;
        this.emailOrNumber = emailOrNumber;
        this.code = code;
        this.country = country;
    }

    @NonNull
    public static PasswordlessLoginEvent requestCode(@PasswordlessMode int mode, @NonNull String email) {
        return new PasswordlessLoginEvent(mode, email, null, null);
    }

    @NonNull
    public static PasswordlessLoginEvent requestCode(@PasswordlessMode int mode, @NonNull String number, @NonNull Country country) {
        String fullNumber = country.getDialCode() + number;
        return new PasswordlessLoginEvent(mode, fullNumber, null, country);
    }

    @NonNull
    public static PasswordlessLoginEvent submitCode(@PasswordlessMode int mode, @NonNull String code) {
        return new PasswordlessLoginEvent(mode, null, code, null);
    }

    @PasswordlessMode
    public int getMode() {
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
     * @param apiClient      the API Client instance
     * @param connectionName the name of the passwordless connection to request the login with. Only 'sms' and 'email' connections are allowed here.
     * @return the Passwordless code request request.
     */
    @NonNull
    public Request<Void, AuthenticationException> getCodeRequest(@NonNull AuthenticationAPIClient apiClient, @NonNull String connectionName) {
        Log.d(TAG, String.format("Generating Passwordless Code/Link request for connection %s", connectionName));
        Request<Void, AuthenticationException> request;
        if (getMode() == PasswordlessMode.EMAIL_CODE) {
            request = apiClient.passwordlessWithEmail(getEmailOrNumber(), PasswordlessType.CODE);
        } else if (getMode() == PasswordlessMode.EMAIL_LINK) {
            request = apiClient.passwordlessWithEmail(getEmailOrNumber(), PasswordlessType.ANDROID_LINK);
        } else if (getMode() == PasswordlessMode.SMS_CODE) {
            request = apiClient.passwordlessWithSMS(getEmailOrNumber(), PasswordlessType.CODE);
        } else {
            request = apiClient.passwordlessWithSMS(getEmailOrNumber(), PasswordlessType.ANDROID_LINK);
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
    @NonNull
    public AuthenticationRequest getLoginRequest(@NonNull AuthenticationAPIClient apiClient, @NonNull String emailOrNumber) {
        Log.d(TAG, String.format("Generating Passwordless Login request for identity %s", emailOrNumber));
        if (getMode() == PasswordlessMode.EMAIL_CODE || getMode() == PasswordlessMode.EMAIL_LINK) {
            return apiClient.loginWithEmail(emailOrNumber, getCode());
        } else {
            return apiClient.loginWithPhoneNumber(emailOrNumber, getCode());
        }
    }
}
