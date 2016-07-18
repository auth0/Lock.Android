/*
 * DbSignUpEvent.java
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

import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.request.DatabaseConnectionRequest;
import com.auth0.android.authentication.request.SignUpRequest;
import com.auth0.android.result.DatabaseUser;

import java.util.HashMap;
import java.util.Map;

public class DatabaseSignUpEvent extends DatabaseEvent {

    private static final String KEY_USER_METADATA = "user_metadata";

    @NonNull
    private String password;
    private Map<String, String> extraFields;

    public DatabaseSignUpEvent(@NonNull String email, @NonNull String password, @Nullable String username) {
        super(email, username);
        this.password = password;
    }

    @NonNull
    public String getPassword() {
        return password;
    }

    @Nullable
    public Map<String, String> extraFields() {
        return extraFields;
    }

    public void setExtraFields(@NonNull Map<String, String> customFields) {
        this.extraFields = customFields;
    }

    public SignUpRequest getSignUpRequest(AuthenticationAPIClient apiClient, String connection) {
        SignUpRequest request;
        if (getUsername() != null) {
            request = apiClient.signUp(getEmail(), getPassword(), getUsername(), connection);
        } else {
            request = apiClient.signUp(getEmail(), getPassword(), connection);
        }
        if (extraFields != null) {
            Map<String, Object> params = new HashMap<>();
            params.put(KEY_USER_METADATA, extraFields);
            request.addSignUpParameters(params);
        }
        return request;
    }

    public DatabaseConnectionRequest<DatabaseUser, AuthenticationException> getCreateUserRequest(AuthenticationAPIClient apiClient, String connection) {
        DatabaseConnectionRequest<DatabaseUser, AuthenticationException> request;
        if (getUsername() != null) {
            request = apiClient.createUser(getEmail(), getPassword(), getUsername(), connection);
        } else {
            request = apiClient.createUser(getEmail(), getPassword(), connection);
        }
        if (extraFields != null) {
            request.addParameter(KEY_USER_METADATA, extraFields);
        }
        return request;
    }
}
