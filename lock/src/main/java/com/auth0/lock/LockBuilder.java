/*
 * LockBuilder.java
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

package com.auth0.lock;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import com.auth0.api.APIClient;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hernan on 1/5/15.
 */
public class LockBuilder {

    public static final String CLIENT_ID_KEY = "com.auth0.client-id";
    public static final String TENANT_KEY = "com.auth0.tenant";
    public static final String DOMAIN_KEY = "com.auth0.domain";
    public static final String CONFIGURATION_KEY = "com.auth0.configuration";

    private String clientId;
    private String tenant;
    private String domain;
    private String configuration;
    private boolean useWebView;
    private boolean closable;
    private boolean loginAfterSignUp;
    private Map<String, Object> parameters;
    private boolean useEmail;

    public LockBuilder() {
        this.loginAfterSignUp = true;
        this.useEmail = true;
        this.parameters = new HashMap<>();
    }

    public LockBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public LockBuilder tenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    public LockBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    public LockBuilder configuration(String configuration) {
        this.configuration = configuration;
        return this;
    }

    public LockBuilder useWebView(boolean useWebView) {
        this.useWebView = useWebView;
        return this;
    }

    public LockBuilder closable(boolean closable) {
        this.closable = closable;
        return this;
    }

    public LockBuilder loginAfterSignUp(boolean loginAfterSignUp) {
        this.loginAfterSignUp = loginAfterSignUp;
        return this;
    }

    public LockBuilder authenticationParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }

    public Lock build() {
        resolveConfiguration();
        Lock lock = buildLock();
        lock.setUseWebView(this.useWebView);
        lock.setLoginAfterSignUp(this.loginAfterSignUp);
        lock.setClosable(this.closable);
        lock.setAuthenticationParameters(this.parameters);
        lock.setUseEmail(this.useEmail);
        return lock;
    }

    private Lock buildLock() {
        Lock lock;
        if (this.clientId == null) {
            throw new IllegalArgumentException("Must supply a non-null ClientId");
        }
        if (this.domain != null) {
            lock = new Lock(new APIClient(this.clientId, this.domain, this.configuration));
        } else if(this.tenant != null) {
            lock = new Lock(new APIClient(this.clientId, this.tenant));
        } else {
            throw new IllegalArgumentException("Missing Auth0 credentials. Please make sure you supplied at least ClientID and Tenant.");
        }
        return lock;
    }

    private void resolveConfiguration() {
        if (this.configuration == null && this.domain != null) {
            final Uri domainUri = Uri.parse(this.domain);
            if (domainUri.getHost().endsWith("auth0.com")) {
                this.configuration = String.format(APIClient.APP_INFO_CDN_URL_FORMAT, this.clientId);
            } else {
                this.configuration = this.domain;
            }
        }
    }

    public LockBuilder loadFromApplication(Application application) {
        try {
            ApplicationInfo ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            this.clientId = bundle.getString(CLIENT_ID_KEY);
            this.tenant = bundle.getString(TENANT_KEY);
            this.domain = bundle.getString(DOMAIN_KEY);
            this.configuration = bundle.getString(CONFIGURATION_KEY);
        } catch (PackageManager.NameNotFoundException e) {
            throw new IllegalArgumentException("Failed to read info from AndroidManifest.xml", e);
        }
        return this;
    }

    public LockBuilder useEmail(boolean useEmail) {
        this.useEmail = useEmail;
        return this;
    }
}
