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
import android.util.Log;

import com.auth0.api.APIClient;
import com.auth0.api.ParameterBuilder;
import com.auth0.lock.credentials.CredentialStore;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Builder for {@link com.auth0.lock.Lock}
 */
public class LockBuilder {

    /**
     * Key for application meta-data where Lock checks for application's ClientId
     */
    public static final String CLIENT_ID_KEY = "com.auth0.lock.client-id";
    /**
     * Key for application meta-data where Lock checks for tenant name
     */
    public static final String TENANT_KEY = "com.auth0.lock.tenant";
    /**
     * Key for application meta-data where Lock checks for domain Url
     */
    public static final String DOMAIN_URL_KEY = "com.auth0.lock.domain-url";
    /**
     * Key for application meta-data where Lock checks for configuration Url
     */
    public static final String CONFIGURATION_URL_KEY = "com.auth0.lock.configuration-url";

    private String clientId;
    private String tenant;
    private String domain;
    private String configuration;
    private boolean useWebView;
    private boolean closable;
    private boolean loginAfterSignUp;
    private Map<String, Object> parameters;
    private boolean useEmail;
    private String defaultDBConnectionName;
    private List<String> connections;
    private boolean fullscreen;
    private boolean disableSignUp;
    private boolean disableChangePassword;
    private CredentialStore store;

    public LockBuilder() {
        this.loginAfterSignUp = true;
        this.useEmail = true;
        this.fullscreen = false;
        this.parameters = ParameterBuilder.newBuilder().asDictionary();
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
     * @deprecated since 1.7.0
     */
    @Deprecated
    public LockBuilder tenant(String tenant) {
        this.tenant = tenant;
        return this;
    }

    /**
     * Set the default domain Url for Auth0 API
     * @param domain url of the domain where Auth0 API is deployed
     * @return itself
     */
    public LockBuilder domainUrl(String domain) {
        this.domain = ensureUrlString(domain);
        return this;
    }

    /**
     * Set the Url where the app information can be retrieved
     * @param configuration Url that returns the app info.
     * @return itself
     */
    public LockBuilder configurationUrl(String configuration) {
        this.configuration = ensureUrlString(configuration);
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
        this.parameters = parameters != null ? parameters : ParameterBuilder.newBuilder().asDictionary();
        return this;
    }

    /**
     * Make Lock pick these connections for authentication from all the enabled connections in your app.
     * If the connection is not active in your application it will be ignored.
     * @param connectionNames List of names of connections to use.
     * @return itself
     */
    public LockBuilder useConnections(String ...connectionNames) {
        this.connections = Arrays.asList(connectionNames);
        return this;
    }

    /**
     * Specify the DB connection used by Lock.
     * @param name DB connection name
     * @return itself.
     */
    public LockBuilder defaultDatabaseConnection(String name) {
        this.defaultDBConnectionName = name;
        return this;
    }

    /**
     * Shows Lock in Fullscreen mode.
     * @param fullscreen if lock is displayed in fullscreen
     * @return itself
     */
    public LockBuilder fullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
        return this;
    }

    public LockBuilder disableSignUp(boolean disableSignUp) {
        this.disableSignUp = disableSignUp;
        return this;
    }

    public LockBuilder disableChangePassword(boolean disableChangePassword) {
        this.disableChangePassword = disableChangePassword;
        return this;
    }

    /**
     * Create a {@link com.auth0.lock.Lock} instance with the values stored.
     * @return
     */
    public Lock build() {
        resolveConfiguration();
        Lock lock = buildLock();
        lock.setUseWebView(useWebView);
        lock.setLoginAfterSignUp(loginAfterSignUp);
        lock.setClosable(closable);
        lock.setAuthenticationParameters(parameters);
        lock.setUseEmail(useEmail);
        lock.setConnections(connections);
        lock.setDefaultDatabaseConnection(defaultDBConnectionName);
        lock.setFullScreen(fullscreen);
        lock.setSignUpEnabled(!disableSignUp);
        lock.setChangePasswordEnabled(!disableChangePassword);
        lock.setCredentialStore(store);
        return lock;
    }

    /**
     * Load ClientID, Tenant name, Domain and configuration URLs from the Android app's metadata (if available).
     * These are the values that can be defined and it's keys:
     * <ul>
     *     <li>{@link #CLIENT_ID_KEY}: Application's clientId in Auth0.</li>
     *     <li>{@link #TENANT_KEY}: Application's owner tenant name. (Optional if you supply Domain and Configuration URLs)</li>
     *     <li>{@link #DOMAIN_URL_KEY}: URL where the Auth0 API is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)</li>
     *     <li>{@link #CONFIGURATION_URL_KEY}: URL where Auth0 apps information is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)</li>
     * </ul>
     * @param application an instance of {@link android.app.Application}
     * @return itself
     */
    public LockBuilder loadFromApplication(Application application) {
        try {
            ApplicationInfo ai = application.getPackageManager().getApplicationInfo(application.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            this.clientId(bundle.getString(CLIENT_ID_KEY))
                .tenant(bundle.getString(TENANT_KEY))
                .domainUrl(bundle.getString(DOMAIN_URL_KEY))
                .configurationUrl(bundle.getString(CONFIGURATION_URL_KEY));
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

    /**
     * The credential store that Lock will use to store user's credentials on Sign Up.
     * @param store a credential store
     * @return itself
     */
    public LockBuilder useCredentialStore(CredentialStore store) {
        this.store = store;
        return this;
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
            final String host = domainUri.getHost();
            if (host.endsWith(".auth0.com")) {
                this.configuration = host.endsWith(".eu.auth0.com") ? APIClient.AUTH0_EU_CDN_URL : APIClient.AUTH0_US_CDN_URL;
            } else {
                this.configuration = this.domain;
            }
        }
    }

    private String ensureUrlString(String url) {
        String safeUrl = null;
        if (url != null) {
            safeUrl = url.startsWith("http") ? url : "https://" + url;
            if (safeUrl.startsWith("http://")) {
                Log.w(LockBuilder.class.getName(), "You should use (https) instead of (http) for url " + url);
            }
        } else {
            Log.w(LockBuilder.class.getName(), "A null url was supplied to LockBuilder");
        }
        return safeUrl;
    }
}
