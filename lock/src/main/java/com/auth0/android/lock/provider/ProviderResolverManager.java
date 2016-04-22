/*
 * ProviderResolverManager.java
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

package com.auth0.android.lock.provider;

import android.content.Context;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Holds an instance of AuthProviderResolver that can be used to query for IdentityProviders given
 * a connection name.
 * If a new instance is not set before calling get, it provides a default safe implementation of the
 * AuthProviderResolver that always returns a null AuthProvider.
 */
@MainThread
public abstract class ProviderResolverManager {

    private static final String TAG = ProviderResolverManager.class.getSimpleName();
    private static AuthProviderResolver resolver;

    /**
     * Gets the AuthProviderResolver instance to query for IdentityProviders.
     *
     * @return the AuthProviderResolver instance to query for IdentityProviders.
     */
    @NonNull
    public static AuthProviderResolver get() {
        if (resolver == null) {
            Log.w(TAG, "No AuthProviderResolver was specified. All requests to onAuthProviderRequest will return a null AuthProvider.");
            return new NullProviderResolver();
        }
        return resolver;
    }

    /**
     * Sets the AuthProviderResolver instance to query for IdentityProviders.
     */
    public static void set(@NonNull AuthProviderResolver providerResolver) {
        resolver = providerResolver;
    }

    private static class NullProviderResolver implements AuthProviderResolver {
        @Nullable
        @Override
        public AuthProvider onAuthProviderRequest(Context context, @NonNull AuthCallback callback, @NonNull String connectionName) {
            return null;
        }
    }
}