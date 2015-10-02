/*
 * DelegationRequest.java
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

import com.auth0.api.callback.BaseCallback;
import com.auth0.api.callback.RefreshIdTokenCallback;

import java.util.Map;

public class DelegationRequest {

    private static final String TOKEN_TYPE_KEY = "token_type";
    private static final String EXPIRES_IN_KEY = "expires_in";
    private static final String ID_TOKEN_KEY = "id_token";

    private final ParameterizableRequest<Map<String, Object>> request;

    public DelegationRequest(ParameterizableRequest<Map<String, Object>> request) {
        this.request = request;
    }

    public DelegationRequest setParameters(Map<String, Object> parameters) {
        request.setParameters(parameters);
        return this;
    }

    public void start(final RefreshIdTokenCallback callback) {
        request.start(new BaseCallback<Map<String, Object>>() {
            @Override
            public void onSuccess(final Map<String, Object> payload) {
                final String id_token = (String) payload.get(ID_TOKEN_KEY);
                final String token_type = (String) payload.get(TOKEN_TYPE_KEY);
                final Integer expires_in = (Integer) payload.get(EXPIRES_IN_KEY);
                callback.onSuccess(id_token, token_type, expires_in);
            }

            @Override
            public void onFailure(Throwable error) {
                callback.onFailure(error);
            }
        });
    }

}
