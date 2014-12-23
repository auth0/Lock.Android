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
import com.auth0.lock.Lock;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.SocialAuthenticationRequestEvent;
import com.auth0.lock.event.SocialCredentialEvent;
import com.auth0.lock.identity.IdentityProvider;
import com.auth0.lock.provider.BusProvider;
import com.facebook.Session;
import com.facebook.SessionState;

/**
 * Created by hernan on 12/22/14.
 */
public class FacebookIdentityProvider implements IdentityProvider {

    private Lock lock;
    private BusProvider provider;

    @Override
    public void initialize(Lock lock, BusProvider provider) {
        this.lock = lock;
        this.provider = provider;
    }

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        final Session session = Session.getActiveSession();
        return session != null && session.onActivityResult(activity, requestCode, resultCode, data);
    }

    @Override
    public void authenticate(Activity activity, SocialAuthenticationRequestEvent event, Application application) {
        Session.openActiveSession(activity, true, new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState sessionState, Exception e) {
                Log.v(FacebookIdentityProvider.class.getName(), "Login FB callback with state " + sessionState + " and session " + session);
                if (e != null) {
                    Log.e(FacebookIdentityProvider.class.getName(), "Failed to authenticate with FB", e);
                    provider.getBus().post(new AuthenticationError(R.string.social_error_title, R.string.social_error_message));
                } else {
                    switch (sessionState) {
                        case OPENED:
                        case OPENED_TOKEN_UPDATED:
                            provider.getBus().post(new SocialCredentialEvent("facebook", session.getAccessToken()));
                            break;
                        case CLOSED_LOGIN_FAILED:
                            provider.getBus().post(new AuthenticationError(R.string.social_error_title, R.string.social_error_message));
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
    public void clearSession() {
        final Session session = Session.getActiveSession();
        if (session != null) {
            session.closeAndClearTokenInformation();
        }
    }
}
