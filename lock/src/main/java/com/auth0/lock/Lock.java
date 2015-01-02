/*
 * Lock.java
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

package com.auth0.lock;

import android.app.Application;
import android.content.Context;

import com.auth0.api.APIClient;
import com.auth0.lock.identity.IdentityProvider;
import com.auth0.lock.identity.WebIdentityProvider;
import com.auth0.lock.provider.APIClientProvider;
import com.auth0.lock.web.CallbackParser;
import com.google.inject.Binder;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

import roboguice.RoboGuice;

/**
 * Created by hernan on 12/7/14.
 */
public class Lock {

    private final String clientId;
    private final String tenant;

    private boolean useWebView;
    private IdentityProvider defaultProvider;
    private Map<String, IdentityProvider> providers;

    public Lock(String clientId, String tenant) {
        this.clientId = clientId;
        this.tenant = tenant;
        this.useWebView = false;
        this.defaultProvider = new WebIdentityProvider(new CallbackParser(), this);
        this.providers = new HashMap<>();
    }

    public void registerForApplication(Application application) {
        RoboGuice.setUseAnnotationDatabases(false);
        RoboGuice.getOrCreateBaseApplicationInjector(
                application,
                RoboGuice.DEFAULT_STAGE,
                RoboGuice.newDefaultRoboModule(application),
                new LockModule(new APIClientProvider(clientId, tenant), this));
    }

    public APIClient getAPIClient(Context context) {
        return RoboGuice.getInjector(context).getInstance(APIClient.class);
    }

    public void setUseWebView(boolean useWebView) {
        this.useWebView = useWebView;
    }

    public boolean isUseWebView() {
        return useWebView;
    }

    public void setProvider(String serviceName, IdentityProvider provider) {
        providers.put(serviceName, provider);
    }

    public IdentityProvider providerForName(String serviceName) {
        IdentityProvider provider = providers.get(serviceName);
        return provider != null ? provider : defaultProvider;
    }

    public IdentityProvider getDefaultProvider() {
        return defaultProvider;
    }

    public void resetAllProviders() {
        for (IdentityProvider provider: this.providers.values()) {
            provider.stop();
        }
        this.defaultProvider.stop();
    }

    private static class LockModule implements Module {

        private final Provider<APIClient> provider;
        private final Lock lock;

        private LockModule(Provider<APIClient> provider, Lock lock) {
            this.provider = provider;
            this.lock = lock;
        }

        @Override
        public void configure(Binder binder) {
            binder.bind(APIClient.class).toProvider(provider).in(Singleton.class);
            binder.bind(Lock.class).toInstance(lock);
        }

    }
}
