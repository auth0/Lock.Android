/*
 * PasswordlessLock.java
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

package com.auth0.android.lock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StyleRes;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.lock.LockCallback.LockEvent;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.lock.internal.configuration.Theme;
import com.auth0.android.lock.provider.AuthResolver;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.provider.AuthHandler;
import com.auth0.android.provider.CustomTabsOptions;
import com.auth0.android.util.Auth0UserAgent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PasswordlessLock {

    private static final String TAG = PasswordlessLock.class.getSimpleName();
    private final LockCallback callback;
    private final Options options;

    /**
     * Listens to PasswordlessLockActivity broadcasts and fires the correct action on the LockCallback.
     */
    private final BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(@NonNull Context context, @NonNull Intent data) {
            processEvent(context, data);
        }
    };

    private PasswordlessLock(@NonNull Options options, @NonNull LockCallback callback) {
        this.options = options;
        this.callback = callback;
    }

    /**
     * Lock.Options holds the configuration used in the Auth0 Passwordless Authentication API.
     *
     * @return the Lock.Options for this Lock instance.
     */
    @NonNull
    public Options getOptions() {
        return options;
    }

    /**
     * Creates a new Lock.Builder instance with the given account and callback.
     * Use of Passwordless connections requires your Application to have the <b>Resource Owner</b> Legacy Grant Type enabled.
     * See <a href="https://auth0.com/docs/clients/client-grant-types">Client Grant Types</a> to learn how to enable it.
     *
     * @param account  details to use against the Auth0 Authentication API.
     * @param callback that will receive the authentication results.
     * @return a new Lock.Builder instance.
     */
    @NonNull
    public static Builder newBuilder(@Nullable Auth0 account, @NonNull LockCallback callback) {
        return new PasswordlessLock.Builder(account, callback);
    }

    /**
     * Creates a new Lock.Builder instance with the given callback. The account information
     * will be retrieved from the String resources file (strings.xml) using
     * the keys 'com_auth0_client_id' and 'com_auth0_domain'.
     * Use of Passwordless connections requires your Application to have the <b>Resource Owner</b> Legacy Grant Type enabled.
     * See <a href="https://auth0.com/docs/clients/client-grant-types">Client Grant Types</a> to learn how to enable it.
     *
     * @param callback that will receive the authentication results.
     * @return a new Lock.Builder instance.
     */
    @NonNull
    public static Builder newBuilder(@NonNull LockCallback callback) {
        return newBuilder(null, callback);
    }

    /**
     * Builds a new intent to launch LockActivity with the previously configured options
     *
     * @param context a valid Context
     * @return the intent to which the user has to call startActivity or startActivityForResult
     */
    @NonNull
    public Intent newIntent(@NonNull Context context) {
        Intent lockIntent = new Intent(context, PasswordlessLockActivity.class);
        lockIntent.putExtra(Constants.OPTIONS_EXTRA, options);
        lockIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return lockIntent;
    }

    /**
     * This method ensures proper Lock's lifecycle handling. Must be called from the class
     * holding the Lock instance whenever you're done using it. i.e. in the Activity's onDestroy method.
     *
     * @param context a valid Context
     */
    public void onDestroy(@NonNull Context context) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this.receiver);
    }

    private void initialize(Context context) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.AUTHENTICATION_ACTION);
        filter.addAction(Constants.CANCELED_ACTION);
        filter.addAction(Constants.INVALID_CONFIGURATION_ACTION);
        LocalBroadcastManager.getInstance(context).registerReceiver(this.receiver, filter);
    }

    private void processEvent(Context context, Intent data) {
        LocalBroadcastManager.getInstance(context).unregisterReceiver(this.receiver);
        String action = data.getAction();
        switch (action) {
            case Constants.AUTHENTICATION_ACTION:
                Log.v(TAG, "AUTHENTICATION action received in our BroadcastReceiver");
                if (data.getExtras().containsKey(Constants.ERROR_EXTRA)) {
                    callback.onError(new LockException(data.getStringExtra(Constants.ERROR_EXTRA)));
                } else {
                    callback.onEvent(LockEvent.AUTHENTICATION, data);
                }
                break;
            case Constants.CANCELED_ACTION:
                Log.v(TAG, "CANCELED action received in our BroadcastReceiver");
                callback.onEvent(LockEvent.CANCELED, new Intent());
                break;
            case Constants.INVALID_CONFIGURATION_ACTION:
                Log.v(TAG, "INVALID_CONFIGURATION_ACTION action received in our BroadcastReceiver");
                callback.onError(new LockException(data.getStringExtra(Constants.ERROR_EXTRA)));
                break;
        }
    }

    /**
     * Helper Builder to generate the Lock.Options to use on the Auth0 Passwordless Authentication.
     */
    @SuppressWarnings({"unused", "UnusedReturnValue"})
    public static class Builder {
        private static final String TAG = Builder.class.getSimpleName();
        private Options options;
        private LockCallback callback;

        /**
         * Creates a new Lock.Builder instance with the given account and callback.
         *
         * @param account  details to use against the Auth0 Authentication API.
         * @param callback that will receive the authentication results.
         */
        public Builder(@Nullable Auth0 account, @NonNull LockCallback callback) {
            this.callback = callback;
            options = new Options();
            //noinspection ConstantConditions
            options.setAccount(account);
        }

        /**
         * Finishes the construction of the Lock.Options and generates a new Lock instance
         * with those Lock.Options.
         *
         * @param context a valid Context
         * @return a new Lock instance configured as in the Builder.
         */
        @NonNull
        public PasswordlessLock build(@NonNull Context context) {
            //noinspection ConstantConditions
            if (options.getAccount() == null) {
                Log.w(TAG, "com.auth0.android.Auth0 account details not defined. Trying to create it from the String resources.");
                try {
                    options.setAccount(new Auth0(context));
                } catch (IllegalArgumentException e) {
                    throw new IllegalStateException("Missing Auth0 account information.", e);
                }
            }
            if (callback == null) {
                Log.e(TAG, "You need to specify the callback object to receive the Authentication result.");
                throw new IllegalStateException("Missing callback.");
            }
            Log.v(TAG, "PasswordlessLock instance created");

            Auth0UserAgent lockUserAgent = new Auth0UserAgent(Constants.LIBRARY_NAME, BuildConfig.VERSION_NAME, com.auth0.android.auth0.BuildConfig.VERSION_NAME);
            options.getAccount().setAuth0UserAgent(lockUserAgent);

            final PasswordlessLock lock = new PasswordlessLock(options, callback);
            lock.initialize(context);
            return lock;
        }

        /**
         * Control the visibility of the header's Title on the main screen. By default it will show the header's Title on the main screen.
         *
         * @param hideMainScreenTitle if it should show or hide the header's Title on the main screen.
         * @return the current builder instance
         */
        @NonNull
        public Builder hideMainScreenTitle(boolean hideMainScreenTitle) {
            options.setHideMainScreenTitle(hideMainScreenTitle);
            return this;
        }

        /**
         * Defines the Passwordless type to use in the Authentication as Code. Default value is to use Code
         *
         * @return the current Builder instance
         */
        @NonNull
        public Builder useCode() {
            options.setUseCodePasswordless(true);
            return this;
        }

        /**
         * Defines the Passwordless type to use in the Authentication as Link. Default value is to use Code
         *
         * @return the current Builder instance
         */
        @NonNull
        public Builder useLink() {
            options.setUseCodePasswordless(false);
            return this;
        }

        /**
         * Whether Lock should remember the last used passwordless identity and auto request a sign or not. By default, lock will not remember the last login.
         *
         * @return the current Builder instance
         */
        @NonNull
        public Builder rememberLastLogin(boolean remember) {
            options.setRememberLastPasswordlessLogin(remember);
            return this;
        }

        /**
         * Whether to use the Browser for Authentication with Identity Providers or the inner WebView.
         *
         * @param useBrowser or WebView. By default, the Authentication flow will use the Browser.
         * @return the current Builder instance
         * @deprecated This method has been deprecated since Google is no longer supporting WebViews to perform login.
         */
        @Deprecated
        @NonNull
        public Builder useBrowser(boolean useBrowser) {
            //noinspection deprecation
            options.setUseBrowser(useBrowser);
            return this;
        }

        /**
         * Whether to use implicit grant or code grant when performing calls to /authorize. This only affects passive authentication.
         * Default is {@code false}
         *
         * @param useImplicitGrant if Lock will use implicit grant instead of code grant.
         * @return the current Builder instance
         * @deprecated Lock should always use the code grant for passive authentication. This is the default behavior.
         */
        @Deprecated
        @NonNull
        public Builder useImplicitGrant(boolean useImplicitGrant) {
            //noinspection deprecation
            options.setUsePKCE(!useImplicitGrant);
            return this;
        }

        /**
         * Whether the PasswordlessLockActivity can be closed when pressing the Back key or not.
         *
         * @param closable or not. By default, the LockActivity is not closable.
         * @return the current builder instance
         */
        @NonNull
        public Builder closable(boolean closable) {
            options.setClosable(closable);
            return this;
        }

        /**
         * Customize Lock's appearance.
         *
         * @param theme to use.
         * @return the current Builder instance
         */
        private Builder withTheme(@NonNull Theme theme) {
            options.withTheme(theme);
            return this;
        }

        /**
         * Auth Button size to use when Social connections are available. If Social
         * is the only connection type it will default to the BIG size. If Database or
         * Enterprise are present and there's only one Social connection, the button will use the
         * BIG size. In the rest of the cases, it will use SMALL size.
         *
         * @param style a valid AuthButtonSize.
         * @return the current builder instance
         * @deprecated Small button style is no longer offered since it is not compliant
         * to some providers branding guidelines. e.g. google
         */
        @Deprecated
        @NonNull
        public Builder withAuthButtonSize(@AuthButtonSize int style) {
            return this;
        }

        /**
         * Authentication Style to use with the given strategy or connection name. It will override any lock defaults.
         *
         * @param connectionName to use this style with
         * @param style          a valid Style with the Auth0.BackgroundColor, Auth0.Logo and Auth0.Name values defined.
         * @return the current builder instance
         */
        @NonNull
        public Builder withAuthStyle(@NonNull String connectionName, @StyleRes int style) {
            options.withAuthStyle(connectionName, style);
            return this;
        }

        /**
         * Additional Authentication parameters can be set to use with different Identity Providers.
         *
         * @param authenticationParameters a non-null Map containing the parameters as Key-Values
         * @return the current builder instance
         */
        @NonNull
        public Builder withAuthenticationParameters(@NonNull Map<String, String> authenticationParameters) {
            options.setAuthenticationParameters(new HashMap<>(authenticationParameters));
            return this;
        }

        /**
         * Locally filters the Auth0 Connections that are shown in the login widgets.
         *
         * @param connections a non-null List containing the allowed Auth0 Connections.
         * @return the current builder instance
         */
        @NonNull
        public Builder allowedConnections(@NonNull List<String> connections) {
            options.setConnections(connections);
            return this;
        }

        /**
         * Uses the given AuthHandlers to query for AuthProviders on a new authentication request.
         *
         * @param handlers that Lock will query for AuthProviders.
         * @return the current builder instance
         */
        @NonNull
        public Builder withAuthHandlers(@NonNull AuthHandler... handlers) {
            AuthResolver.setAuthHandlers(Arrays.asList(handlers));
            return this;
        }

        /**
         * Sets the Scope to request when performing the Authentication.
         *
         * @param scope to use in the Authentication.
         * @return the current builder instance
         */
        @NonNull
        public Builder withScope(@NonNull String scope) {
            options.withScope(scope);
            return this;
        }

        /**
         * Sets the Audience or API Identifier to request access to when performing the Authentication.
         *
         * @param audience to use in the Authentication.
         * @return the current builder instance
         */
        @NonNull
        public Builder withAudience(@NonNull String audience) {
            options.withAudience(audience);
            return this;
        }

        /**
         * Specify a custom Scheme for the redirect url used to send the Web Auth results. Default redirect url scheme is 'https'.
         *
         * @param scheme to use in the Web Auth redirect uri.
         * @return the current builder instance
         */
        @NonNull
        public Builder withScheme(@NonNull String scheme) {
            options.withScheme(scheme);
            return this;
        }

        /**
         * Specify style and other additional configuration for when the Web Auth flow is used with Custom Tabs.
         *
         * @param customTabsOptions to use in the Web Auth flow.
         * @return the current builder instance
         */
        @NonNull
        public Builder withCustomTabsOptions(@NonNull CustomTabsOptions customTabsOptions) {
            options.withCustomTabsOptions(customTabsOptions);
            return this;
        }

        /**
         * Sets the url of your support page for your application that will be used when an error occurs and Lock is unable to handle it. In this case it will show an error screen and if there is a support url will also show a button to open that page in the browser.
         *
         * @param url to your support page or where your customers can request assistance. By default no page is set.
         * @return the current builder instance
         */
        @NonNull
        public Builder setSupportURL(@NonNull String url) {
            options.setSupportURL(url);
            return this;
        }

        /**
         * Sets the Connection Scope to request when performing an Authentication with the given Connection.
         *
         * @param connectionName to which specify the scopes.
         * @param scope          recognized by this specific authentication provider.
         * @return the current builder instance
         */
        @NonNull
        public Builder withConnectionScope(@NonNull String connectionName, @NonNull String... scope) {
            StringBuilder sb = new StringBuilder();
            for (String s : scope) {
                sb.append(s.trim()).append(",");
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(sb.length() - 1);
                options.withConnectionScope(connectionName, sb.toString());
            }
            return this;
        }
    }
}
