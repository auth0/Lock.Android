/*
 * DbChangePasswordEvent.java
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

import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.request.DatabaseConnectionRequest;
import com.auth0.android.request.AuthenticationRequest;

public class DatabaseChangePasswordEvent {

    private final String usernameOrEmail;
    private final String oldPassword;
    private final String newPassword;

    /**
     * Creates a new Database Change Password event with the given email.
     *
     * @param usernameOrEmail a valid email to request a password reset.
     */
    public DatabaseChangePasswordEvent(@NonNull String usernameOrEmail, @NonNull String oldPassword, @NonNull String newPassword) {
        this.usernameOrEmail = usernameOrEmail;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public DatabaseConnectionRequest<Void, AuthenticationException> getChangePasswordRequest(AuthenticationAPIClient apiClient, String connection) {
        return apiClient.changePassword(usernameOrEmail, oldPassword, newPassword, connection);
    }

    public AuthenticationRequest getLogInRequest(AuthenticationAPIClient apiClient, String connection) {
        return apiClient.login(usernameOrEmail, newPassword, connection);
    }

}
