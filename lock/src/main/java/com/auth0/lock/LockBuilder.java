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
 * Builder for {@link com.auth0.lock.Lock}
 */
public class LockBuilder {

    public static final String CLIENT_ID_KEY = "com.auth0.lock.client-id";
    public static final String TENANT_KEY = "com.auth0.lock.tenant";
    public static final String DOMAIN_KEY = "com.auth0.lock.domain-url";
    public static final String CONFIGURATION_KEY = "com.auth0.lock.configuration-url";

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

    /**
     * Set Auth0 application ClientID
     * @param clientId clientId
     * @return itself
     */
    public LockBuilder clientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    /**
     * Set Auth0 account tenant name
     * @param tenant tenant name
     * @return itself
     */
    public LockBuilder tenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    /**
     * Set the default domain Url for Auth0 API
     * @param domain url of the domain where Auth0 API is deployed
     * @return itself
     */
    public LockBuilder domain(String domain) {
        this.domain = domain;
        return this;
    }

    /**
     * Set the Url where the app information can be retrieved
     * @param configuration Url that returns the app info.
     * @return itself
     */
    public LockBuilder configuration(String configuration) {
        this.configuration = configuration;
        return this;
    }

    /**
     * Use an embedded WebView instead of an external browser
     * @param useWebView if Lock will use an embedded WebView or an external browser
     * @return itself
     */
    public LockBuilder useWebView(boolean useWebView) {
        this.useWebView = useWebView;
        return this;
    }

    /**
     * If the login screen can be closed/dismissed
     * @param closable if Lock will allow the login screen to be closed
     * @return itself
     */
    public LockBuilder closable(boolean closable) {
        this.closable = closable;
        return this;
    }

    /**
     * If after a successful sign up, the user will be logged in too.
     * @param loginAfterSignUp if Lock should login a user after sign up
     * @return itself
     */
    public LockBuilder loginAfterSignUp(boolean loginAfterSignUp) {
        this.loginAfterSignUp = loginAfterSignUp;
        return this;
    }

    /**
     * Extra authentication parameters to send to Auth0 Auth API.
     * @param parameters a map with extra parameters for the API.
     * @return itself
     */
    public LockBuilder authenticationParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
        return this;
    }

    /**
     * Create a {@link com.auth0.lock.Lock} instance with the values stored.
     * @return
     */
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

    /**
     * Load ClientID, Tenant name, Domain and configuration URLs from the Android app's metadata (if available).
     * This are the values that can be defined and it's keys:
     * <ul>
     *     <li>{@link #CLIENT_ID_KEY}: Application's clientId in Auth0.</li>
     *     <li>{@link #TENANT_KEY}: Application's owner tenant name. (Optional if you supply Domain and Configuration URLs)</li>
     *     <li>{@link #DOMAIN_KEY}: URL where the Auth0 API is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)</li>
     *     <li>{@link #CONFIGURATION_KEY}: URL where Auth0 apps information is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)</li>
     * </ul>
     * @param application an instance of {@link android.app.Application}
     * @return itself
     */
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

    /**
     * If it should ask for email or username. By default is <code>true</code>
     * @param useEmail if Lock ask for email or username.
     * @return itself
     */
    public LockBuilder useEmail(boolean useEmail) {
        this.useEmail = useEmail;
        return this;
    }
}
