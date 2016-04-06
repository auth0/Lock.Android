/*
 * LockIdentityProviderCallback.java
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

package com.auth0.lock.identity;

import android.app.Dialog;

import com.auth0.core.Token;
import com.auth0.identity.IdentityProviderCallback;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthorizationCodeEvent;
import com.auth0.lock.event.IdentityProviderAuthenticationEvent;
import com.auth0.lock.event.SocialCredentialEvent;
import com.auth0.lock.event.SystemErrorEvent;
import com.squareup.otto.Bus;

public class LockIdentityProviderCallback implements IdentityProviderCallback {

    private final Bus bus;

    public LockIdentityProviderCallback(Bus bus) {
        this.bus = bus;
    }

    @Override
    public void onSuccess(String serviceName, String accessToken) {
        bus.post(new SocialCredentialEvent(serviceName, accessToken));
    }

    @Override
    public void onSuccess(Token token) {
        bus.post(new IdentityProviderAuthenticationEvent(token));
    }

    @Override
    public void onAuthorizationCode(String authorizationCode, String codeVerifier, String redirectUri, IdentityProviderCallback callback) {
        bus.post(new AuthorizationCodeEvent(authorizationCode, codeVerifier, redirectUri, callback));
    }

    @Override
    public void onFailure(Dialog dialog) {
        bus.post(new SystemErrorEvent(dialog));
    }

    @Override
    public void onFailure(int titleResource, int messageResource, Throwable cause) {
        bus.post(new AuthenticationError(titleResource, messageResource, cause));
    }
}
