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

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import com.auth0.api.APIClient;
import com.auth0.api.ParameterBuilder;
import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.internal.RequestFactory;
import com.auth0.core.Auth0;
import com.auth0.core.Strategies;
import com.auth0.identity.IdentityProvider;
import com.auth0.identity.WebIdentityProvider;
import com.auth0.identity.web.CallbackParser;
import com.auth0.lock.credentials.CredentialStore;
import com.auth0.lock.credentials.NullCredentialStore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.squareup.otto.Bus;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Main class of Auth0 Lock SDK for Authentication through Auth0
 * This class handles all your Auth0 configuration and authentication using different Identity Providers.
 * Also this class provides a configured instance of {@link AuthenticationAPIClient} to call Auth0 Authentication API.
 * To start just instantiate it using {@link com.auth0.lock.Lock.Builder} like this inside your {@link Application} object:
 * <pre>
 *     <code>
 *      lock = new Lock.Builder()
 *              .loadFromApplication(this)
 *              .closable(true)
 *              .build();
 *     </code>
 * </pre>
 * <p/>
 * Then just invoke the login activity:
 * <pre>
 *     <code>
 *      Lock.getLock(activity).loginFromActivity(activity);
 *     </code>
 * </pre>
 */
public class Lock {

    /**
     * Action sent in {@link android.support.v4.content.LocalBroadcastManager} when a user authenticates.
     */
    public static final String AUTHENTICATION_ACTION = "Lock.Authentication";
    /**
     * Action sent when the user navigates back closing {@link com.auth0.lock.LockActivity}.
     */
    public static final String CANCEL_ACTION = "Lock.Cancel";
    /**
     * Action sent when the user change its password.
     */
    public static final String CHANGE_PASSWORD_ACTION = "Lock.ChangePassword";
    /**
     * Name of the parameter that will include user's profile.
     */
    public static final String AUTHENTICATION_ACTION_PROFILE_PARAMETER = "profile";
    /**
     * Name of the parameter that will include user's token information.
     */
    public static final String AUTHENTICATION_ACTION_TOKEN_PARAMETER = "token";

    private boolean useWebView;
    private boolean loginAfterSignUp;
    private boolean closable;
    private Map<String, Object> authenticationParameters;
    private boolean useEmail;
    private boolean fullScreen;

    private WebIdentityProvider defaultProvider;
    private Map<String, IdentityProvider> providers;

    private final Bus bus;
    private final APIClient apiClient;
    private final AuthenticationAPIClient authenticationAPIClient;

    private Configuration configuration;
    private List<String> connections;
    private List<String> enterpriseConnectionsUsingWebForm;
    private String defaultDatabaseConnection;
    private boolean signUpEnabled;
    private boolean changePasswordEnabled;
    private boolean requirePasswordOnPasswordReset;
    private CredentialStore credentialStore;

    Lock(Auth0 auth0) {
        this.useWebView = false;
        this.closable = false;
        this.loginAfterSignUp = true;
        this.useEmail = true;
        this.providers = new HashMap<>();
        this.bus = new Bus("Lock");
        this.defaultProvider = new WebIdentityProvider(new CallbackParser(), auth0.getClientId(), auth0.getAuthorizeUrl());
        this.apiClient = auth0.newAPIClient();
        this.authenticationAPIClient = auth0.newAuthenticationAPIClient();
        this.fullScreen = false;
        this.signUpEnabled = true;
        this.changePasswordEnabled = true;
        this.requirePasswordOnPasswordReset = false;
        this.credentialStore = new NullCredentialStore();
        this.enterpriseConnectionsUsingWebForm = new ArrayList<>();
    }

    /**
     * A instance of {@link com.auth0.api.APIClient} used by Lock
     *
     * @return a client
     * @deprecated Use {@link #getAuthenticationAPIClient()}
     */
    @Deprecated
    public APIClient getAPIClient() {
        return apiClient;
    }

    /**
     * An API client for Auth0 authentication API
     *
     * @return
     */
    public AuthenticationAPIClient getAuthenticationAPIClient() {
        return authenticationAPIClient;
    }

    /**
     * A instance of {@link com.squareup.otto.Bus} where all internal events are sent.
     *
     * @return a bus used internally
     */
    public Bus getBus() {
        return bus;
    }

    /**
     * Force Lock to use an embedded {@link android.webkit.WebView}. Default is {@code false}
     * You'll also need to declare the following activity in your {@code AndroidManifest.xml}
     * <pre>{@code
     * <activity android:name="com.auth0.identity.web.WebViewActivity" android:theme="@style/Lock.Theme"/>
     * }</pre>
     *
     * @return if Lock uses a {@link android.webkit.WebView}. Default is false
     */
    public boolean shouldUseWebView() {
        return useWebView;
    }

    /**
     * Make Lock login a newly created user. Default is {@code true}
     *
     * @return If Lock performs signup + login
     */
    public boolean shouldLoginAfterSignUp() {
        return loginAfterSignUp;
    }

    /**
     * Allows Lock activities to be closed by pressing back button. Default is {@code false}
     *
     * @return if back button is enabled for Lock
     */
    public boolean isClosable() {
        return closable;
    }

    /**
     * Use Email to authenticate, otherwise use username. Default is {@code true}
     *
     * @return use email or username
     */
    public boolean shouldUseEmail() {
        return useEmail;
    }

    /**
     * Extra parameters sent to Auth0 API during authentication
     *
     * @return extra parameters for the API
     */
    public Map<String, Object> getAuthenticationParameters() {
        return authenticationParameters != null ? new HashMap<>(authenticationParameters) : new HashMap<String, Object>();
    }

    /**
     * Set a native handler for a specific Identity Provider (IdP), e.g.: Facebook
     *
     * @param serviceName name of the Auth0 strategy to handle. (For all valid values check {@link com.auth0.core.Strategies}
     * @param provider    IdP handler
     * @deprecated use {@link com.auth0.lock.Lock.Builder#withIdentityProvider(Strategies, IdentityProvider)} instead
     */
    public void setProvider(String serviceName, IdentityProvider provider) {
        providers.put(serviceName, provider);
    }

    /**
     * Finds a custom Identity Provider handler by service name.
     *
     * @param serviceName name of the service
     * @return a custom handler or null
     */
    public IdentityProvider providerForName(String serviceName) {
        IdentityProvider provider = providers.get(serviceName);
        return provider != null ? provider : defaultProvider;
    }

    /**
     * Default provider for every Auth0 Authentication strategy
     *
     * @return a default provider
     */
    public IdentityProvider getDefaultProvider() {
        return defaultProvider;
    }

    /**
     * Clears all session information stored in custom IdP handlers.
     */
    public void resetAllProviders() {
        for (IdentityProvider provider : this.providers.values()) {
            provider.stop();
        }
        this.defaultProvider.stop();
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    public List<String> getConnections() {
        return connections;
    }

    public List<String> getEnterpriseConnectionsUsingWebForm() {
        return enterpriseConnectionsUsingWebForm;
    }

    public String getDefaultDatabaseConnection() {
        return defaultDatabaseConnection;
    }

    /**
     * If Lock is displayed in fullscreen mode.
     * By default is false
     *
     * @return if lock will be displayed in fullscreen
     */
    public boolean isFullScreen() {
        return fullScreen;
    }

    /**
     * If Lock has SignUp action enabled
     * By default is true
     *
     * @return if the sign up action is enabled
     */
    public boolean isSignUpEnabled() {
        return signUpEnabled;
    }

    /**
     * If Lock has Change Password action enabled
     * By default is true
     *
     * @return if the change password action is enabled
     */
    public boolean isChangePasswordEnabled() {
        return changePasswordEnabled;
    }

    /**
     * If Lock will ask for the new password when resetting the old one (previous behaviour)
     * By default is false
     *
     * @return if the new password is required when resetting the old password.
     */
    public boolean shouldRequirePasswordOnPasswordReset() {
        return requirePasswordOnPasswordReset;
    }

    /**
     * Lock's credential store for user's credentials e.g. Google's Smart Lock
     * By default no credentials are stored.
     *
     * @return an instance of CredentialStore
     */
    public CredentialStore getCredentialStore() {
        return credentialStore;
    }

    /**
     * Starts LockActivity from a given Activity
     *
     * @param activity from which LockActivity will be started
     */
    public void loginFromActivity(Activity activity) {
        Intent loginIntent = new Intent(activity, LockActivity.class);
        activity.startActivity(loginIntent);
    }

    /**
     * Returns the Lock object from the Application object.
     *
     * @param activity that needs Lock instance
     * @return a Lock instance
     * @see com.auth0.lock.LockContext
     * @deprecated Please use {@link com.auth0.lock.LockContext}
     */
    @Deprecated
    public static Lock getLock(Activity activity) {
        return LockContext.getLock(activity);
    }

    /**
     * Builder for {@link com.auth0.lock.Lock}
     */
    public static class Builder {

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
        private String domain;
        private String configuration;
        private boolean useWebView;
        private boolean closable;
        private boolean loginAfterSignUp;
        private Map<String, Object> parameters;
        private boolean useEmail;
        private String defaultDBConnectionName;
        private List<String> connections;
        private List<String> enterpriseConnectionsUsingWebForm;
        private boolean fullscreen;
        private boolean disableSignUp;
        private boolean disableChangePassword;
        private boolean requirePasswordOnPasswordReset;
        private CredentialStore store;
        private Map<String, IdentityProvider> providers;
        private boolean sendSdkInfo;


        public Builder() {
            this.loginAfterSignUp = true;
            this.useEmail = true;
            this.fullscreen = false;
            this.parameters = ParameterBuilder.newBuilder().asDictionary();
            this.enterpriseConnectionsUsingWebForm = new ArrayList<>();
            this.store = new NullCredentialStore();
            this.providers = new HashMap<>();
            this.sendSdkInfo = true;
            this.requirePasswordOnPasswordReset = false;
        }

        /**
         * Set Auth0 application ClientID
         *
         * @param clientId clientId
         * @return the Builder instance being used
         */
        public Builder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        /**
         * Set Auth0 account tenant name
         *
         * @param tenant tenant name
         * @return the Builder instance being used
         * @deprecated since 1.7.0
         */
        @Deprecated
        public Builder tenant(String tenant) {
            if (tenant != null) {
                domainUrl(tenant + ".auth0.com");
            }
            return this;
        }

        /**
         * Set the default domain Url for Auth0 API
         *
         * @param domain url of the domain where Auth0 API is deployed
         * @return the Builder instance being used
         */
        public Builder domainUrl(String domain) {
            if (domain != null && domain.startsWith("http://")) {
                Log.w(Builder.class.getName(), "You should use (https) instead of (http) for url " + domain);
            }
            this.domain = domain;
            return this;
        }

        /**
         * Set the Url where the app information can be retrieved
         *
         * @param configuration Url that returns the app info.
         * @return the Builder instance being used
         */
        public Builder configurationUrl(String configuration) {
            if (configuration != null && configuration.startsWith("http://")) {
                Log.w(Builder.class.getName(), "You should use (https) instead of (http) for url " + configuration);
            }
            this.configuration = configuration;
            return this;
        }

        /**
         * Use an embedded WebView instead of an external browser
         *
         * @param useWebView if Lock will use an embedded WebView or an external browser
         * @return the Builder instance being used
         */
        public Builder useWebView(boolean useWebView) {
            this.useWebView = useWebView;
            return this;
        }

        /**
         * If the login screen can be closed/dismissed
         *
         * @param closable if Lock will allow the login screen to be closed
         * @return the Builder instance being used
         */
        public Builder closable(boolean closable) {
            this.closable = closable;
            return this;
        }

        /**
         * If after a successful sign up, the user will be logged in too.
         *
         * @param loginAfterSignUp if Lock should login a user after sign up
         * @return the Builder instance being used
         */
        public Builder loginAfterSignUp(boolean loginAfterSignUp) {
            this.loginAfterSignUp = loginAfterSignUp;
            return this;
        }

        /**
         * Extra authentication parameters to send to Auth0 Auth API.
         *
         * @param parameters a map with extra parameters for the API.
         * @return the Builder instance being used
         */
        public Builder authenticationParameters(Map<String, Object> parameters) {
            this.parameters = parameters != null ? parameters : ParameterBuilder.newBuilder().asDictionary();
            return this;
        }

        /**
         * Make Lock pick these connections for authentication from all the enabled connections in your app.
         * If the connection is not active in your application it will be ignored.
         *
         * @param connectionNames List of names of connections to use.
         * @return the Builder instance being used
         */
        @SuppressWarnings("unused")
        public Builder useConnections(String... connectionNames) {
            this.connections = Arrays.asList(connectionNames);
            return this;
        }

        public Builder enterpriseConnectionsUsingWebForm(String... connectionNames) {
            this.enterpriseConnectionsUsingWebForm = Arrays.asList(connectionNames);
            return this;
        }

        /**
         * Specify the DB connection used by Lock.
         *
         * @param name DB connection name
         * @return the Builder instance being used.
         */
        @SuppressWarnings("unused")
        public Builder defaultDatabaseConnection(String name) {
            this.defaultDBConnectionName = name;
            return this;
        }

        /**
         * Shows Lock in Fullscreen mode.
         *
         * @param fullscreen if lock is displayed in fullscreen
         * @return the Builder instance being used
         */
        public Builder fullscreen(boolean fullscreen) {
            this.fullscreen = fullscreen;
            return this;
        }

        /**
         * Disables Sign Up action
         *
         * @param disableSignUp or not
         * @return the Builder instance being used
         */
        public Builder disableSignUp(boolean disableSignUp) {
            this.disableSignUp = disableSignUp;
            return this;
        }

        /**
         * Disables Change Password action
         *
         * @param disableChangePassword or not
         * @return the Builder instance being used
         */
        public Builder disableChangePassword(boolean disableChangePassword) {
            this.disableChangePassword = disableChangePassword;
            return this;
        }

        /**
         * If Lock will ask for the password now (old behaviour). By default is  <code>false</code>
         *
         * @return the Builder instance being used
         */
        public Builder requirePasswordOnPasswordReset() {
            this.requirePasswordOnPasswordReset = true;
            return this;
        }

        /**
         * If it should ask for email or username. By default is <code>true</code>
         *
         * @param useEmail if Lock ask for email or username.
         * @return the Builder instance being used
         */
        public Builder useEmail(boolean useEmail) {
            this.useEmail = useEmail;
            return this;
        }

        /**
         * The credential store that Lock will use to store user's credentials on Sign Up.
         *
         * @param store a credential store
         * @return the Builder instance being used
         */
        public Builder useCredentialStore(CredentialStore store) {
            if (store != null) {
                this.store = store;
            } else {
                this.store = new NullCredentialStore();
            }
            return this;
        }

        /**
         * Sets a native handler for a specific Identity Provider (IdP), e.g.: Facebook
         *
         * @param strategy         Auth0 strategy to handle. (For all valid values check {@link com.auth0.core.Strategies}
         * @param identityProvider IdP handler
         * @return the Builder instance being used
         */
        public Builder withIdentityProvider(Strategies strategy, IdentityProvider identityProvider) {
            providers.put(strategy.getName(), identityProvider);
            return this;
        }

        /**
         * Avoid sending SDK info with API requests
         *
         * @return the Builder instance being used
         */
        @SuppressWarnings("unused")
        public Builder doNotSendSDKInfo() {
            sendSdkInfo = false;
            return this;
        }

        /**
         * Create a {@link com.auth0.lock.Lock} instance with the values stored.
         *
         * @return a new Lock instance`
         */
        public Lock build() {
            Lock lock = buildLock();
            lock.useWebView = useWebView;
            lock.defaultProvider.setUseWebView(useWebView);
            lock.loginAfterSignUp = loginAfterSignUp;
            lock.closable = closable;
            lock.authenticationParameters = parameters;
            lock.defaultProvider.setParameters(parameters);
            lock.useEmail = useEmail;
            lock.connections = connections;
            lock.enterpriseConnectionsUsingWebForm = enterpriseConnectionsUsingWebForm;
            lock.defaultDatabaseConnection = defaultDBConnectionName;
            lock.fullScreen = fullscreen;
            lock.signUpEnabled = !disableSignUp;
            lock.changePasswordEnabled = !disableChangePassword;
            lock.requirePasswordOnPasswordReset = requirePasswordOnPasswordReset;
            lock.credentialStore = store;
            lock.providers = new HashMap<>(providers);
            if (sendSdkInfo) {
                final String clientInfo = buildClientInfo();
                lock.apiClient.setClientInfo(clientInfo);
                lock.defaultProvider.setClientInfo(clientInfo);
                RequestFactory.setClientInfo(clientInfo);
            }
            return lock;
        }

        /**
         * Load ClientID, Tenant name, Domain and configuration URLs from the Android app's metadata (if available).
         * These are the values that can be defined and it's keys:
         * <ul>
         * <li>{@link #CLIENT_ID_KEY}: Application's clientId in Auth0.</li>
         * <li>{@link #TENANT_KEY}: Application's owner tenant name. (Optional if you supply Domain and Configuration URLs)</li>
         * <li>{@link #DOMAIN_URL_KEY}: URL where the Auth0 API is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)</li>
         * <li>{@link #CONFIGURATION_URL_KEY}: URL where Auth0 apps information is available. (Optional if you supply ClientID/Tenant and you use Auth0 in the cloud)</li>
         * </ul>
         *
         * @param application an instance of {@link android.app.Application}
         * @return the Builder instance being used
         */
        @SuppressWarnings("deprecation")
        public Builder loadFromApplication(Application application) {
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

        protected Auth0 buildAuth0() {
            if (this.clientId == null || this.domain == null) {
                throw new IllegalArgumentException("Missing Auth0 credentials. Please make sure you supplied at least ClientID and Domain.");
            }
            return new Auth0(this.clientId, this.domain, this.configuration);
        }

        protected Lock buildLock() {
            return new Lock(buildAuth0());
        }

        protected String buildClientInfo() {
            Map<String, String> info = new HashMap<>();
            info.put("name", "Lock.Android");
            info.put("version", BuildConfig.VERSION_NAME);
            String clientInfo = null;
            try {
                String json = new ObjectMapper().writeValueAsString(info);
                clientInfo = Base64.encodeToString(json.getBytes(Charset.defaultCharset()), Base64.URL_SAFE | Base64.NO_WRAP);
            } catch (JsonProcessingException e) {
                Log.w(Lock.class.getName(), "Failed to build client info", e);
            }
            return clientInfo;
        }
    }
}
