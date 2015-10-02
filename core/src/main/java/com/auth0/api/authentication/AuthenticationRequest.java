/*
 * AuthenticationRequest.java
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

package com.auth0.api.authentication;

import com.auth0.api.ParameterBuilder;
import com.auth0.api.ParameterizableRequest;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;

import java.util.Map;

public class AuthenticationRequest {

    private final ParameterizableRequest<Token> credentialsRequest;
    private final ParameterizableRequest<UserProfile> tokenInfoRequest;

    public AuthenticationRequest(ParameterizableRequest<Token> credentialsRequest, ParameterizableRequest<UserProfile> tokenInfoRequest) {
        this.credentialsRequest = credentialsRequest;
        this.tokenInfoRequest = tokenInfoRequest;
    }

    public AuthenticationRequest setParameters(Map<String, Object> parameters) {
        credentialsRequest.setParameters(parameters);
        return this;
    }

    public void start(final AuthenticationCallback callback) {
        credentialsRequest.start(new BaseCallback<Token>() {
            @Override
            public void onSuccess(final Token token) {
                Map<String, Object> parameters = new ParameterBuilder()
                        .clearAll()
                        .set("id_token", token.getIdToken())
                        .asDictionary();
                tokenInfoRequest
                        .setParameters(parameters)
                        .start(new BaseCallback<UserProfile>() {
                            @Override
                            public void onSuccess(UserProfile profile) {
                                callback.onSuccess(profile, token);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                callback.onFailure(error);
                            }
                        });
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }
}
