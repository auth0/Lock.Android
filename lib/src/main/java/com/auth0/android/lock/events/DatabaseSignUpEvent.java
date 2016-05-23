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

import com.auth0.authentication.AuthenticationAPIClient;
import com.auth0.authentication.DatabaseConnectionRequest;
import com.auth0.authentication.SignUpRequest;
import com.auth0.authentication.result.DatabaseUser;

import java.util.Map;

public class DatabaseSignUpEvent {

    @NonNull
    private String email;
    @Nullable
    private String username;
    @NonNull
    private String password;
    private Map<String, String> extraFields;

    public DatabaseSignUpEvent(@NonNull String email, @NonNull String password, @Nullable String username) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getUsername() {
        return username;
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

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    public SignUpRequest getSignUpRequest(AuthenticationAPIClient apiClient) {
        SignUpRequest request;
        if (getUsername() != null) {
            request = apiClient.signUp(getEmail(), getPassword(), getUsername());
        } else {
            request = apiClient.signUp(getEmail(), getPassword());
        }
        return request;
    }

    public DatabaseConnectionRequest<DatabaseUser> getCreateUserRequest(AuthenticationAPIClient apiClient) {
        DatabaseConnectionRequest<DatabaseUser> request;
        if (getUsername() != null) {
            request = apiClient.createUser(getEmail(), getPassword(), getUsername());
        } else {
            request = apiClient.createUser(getEmail(), getPassword());
        }
        return request;
    }
}
