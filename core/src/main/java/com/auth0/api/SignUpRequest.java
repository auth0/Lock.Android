/*
 * SignUpRequest.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.api;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.DatabaseUser;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;

import java.util.Map;

public class SignUpRequest {

    private final ParameterizableRequest<DatabaseUser> signUpRequest;
    private final AuthenticationRequest authenticationRequest;

    public SignUpRequest(ParameterizableRequest<DatabaseUser> signUpRequest, AuthenticationRequest authenticationRequest) {
        this.signUpRequest = signUpRequest;
        this.authenticationRequest = authenticationRequest;
    }

    public SignUpRequest setSignUpParameters(Map<String, Object> parameters) {
        signUpRequest.setParameters(parameters);
        return this;
    }
    public SignUpRequest setAuthenticationParameters(Map<String, Object> parameters) {
        authenticationRequest.setParameters(parameters);
        return this;
    }

    public void start(final AuthenticationCallback callback) {
        signUpRequest.start(new BaseCallback<DatabaseUser>() {
            @Override
            public void onSuccess(final DatabaseUser user) {
                authenticationRequest
                        .start(callback);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }
}
