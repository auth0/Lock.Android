/*
 * FacebookIdentityProvider.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.facebook;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.identity.IdentityProvider;
import com.auth0.identity.IdentityProviderCallback;
import com.auth0.identity.IdentityProviderRequest;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.Session;
import com.facebook.SessionState;

public class FacebookIdentityProvider implements IdentityProvider {

    private IdentityProviderCallback callback;

    @Override
    public void setCallback(IdentityProviderCallback callback) {
        this.callback = callback;
    }

    @Override
    public void start(Activity activity, IdentityProviderRequest event, Application application) {
        Session.openActiveSession(activity, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState sessionState, Exception e) {
                Log.v(FacebookIdentityProvider.class.getName(), "Login FB callback with state " + sessionState + " and session " + session);
                if (e != null) {
                    Log.e(FacebookIdentityProvider.class.getName(), "Failed to authenticate with FB", e);
                    int messageResource = e instanceof FacebookOperationCanceledException ? R.string.facebook_cancelled_error_message : R.string.social_access_denied_message;
                    callback.onFailure(R.string.facebook_error_title, messageResource, e);
                } else {
                    switch (sessionState) {
                        case OPENED:
                        case OPENED_TOKEN_UPDATED:
                            callback.onSuccess("facebook", session.getAccessToken());
                            break;
                        case CLOSED_LOGIN_FAILED:
                            callback.onFailure(R.string.social_error_title, R.string.social_error_message, null);
                            if (session != null) {
                                session.closeAndClearTokenInformation();
                            }
                            break;
                    }
                }
            }

        });
    }

    @Override
    public void stop() {}

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        final Session session = Session.getActiveSession();
        return session != null && session.onActivityResult(activity, requestCode, resultCode, data);
    }

    @Override
    public void clearSession() {
        final Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
    }
}
