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

import com.auth0.api.APIClient;
import com.auth0.lock.identity.IdentityProvider;
import com.auth0.lock.identity.WebIdentityProvider;
import com.auth0.lock.web.CallbackParser;
import com.squareup.otto.Bus;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hernan on 12/7/14.
 */
public class Lock {

    private boolean useWebView;
    private boolean loginAfterSignUp;
    private boolean closable;
    private Map<String, String> authenticationParameters;
    private boolean useEmail;

    private IdentityProvider defaultProvider;
    private Map<String, IdentityProvider> providers;

    private final Bus bus;
    private final APIClient apiClient;

    public Lock(APIClient apiClient) {
        this.useWebView = false;
        this.closable = false;
        this.loginAfterSignUp = true;
        this.useEmail = true;
        this.providers = new HashMap<>();
        this.bus = new Bus("Lock");
        this.defaultProvider = new WebIdentityProvider(new CallbackParser(), this);
        this.apiClient = apiClient;
    }

    public APIClient getAPIClient() {
        return apiClient;
    }

    public Bus getBus() {
        return bus;
    }

    public void setUseWebView(boolean useWebView) {
        this.useWebView = useWebView;
    }

    public boolean isUseWebView() {
        return useWebView;
    }

    public void setLoginAfterSignUp(boolean loginAfterSignUp) {
        this.loginAfterSignUp = loginAfterSignUp;
    }

    public boolean isLoginAfterSignUp() {
        return loginAfterSignUp;
    }

    public boolean isClosable() {
        return closable;
    }

    public void setClosable(boolean closable) {
        this.closable = closable;
    }

    public void setUseEmail(boolean useEmail) {
        this.useEmail = useEmail;
    }

    public boolean isUseEmail() {
        return useEmail;
    }

    public Map<String, String> getAuthenticationParameters() {
        return authenticationParameters != null ? new HashMap<>(authenticationParameters) : new HashMap<String, String>();
    }

    public void setAuthenticationParameters(Map<String, String> authenticationParameters) {
        this.authenticationParameters = authenticationParameters;
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
}
