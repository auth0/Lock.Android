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

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.ParameterBuilder;
import com.auth0.android.lock.LockCallback.LockEvent;
import com.auth0.android.lock.enums.SocialButtonStyle;
import com.auth0.android.lock.provider.AuthProviderResolver;
import com.auth0.android.lock.provider.ProviderResolverManager;
import com.auth0.android.util.Telemetry;

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
        public void onReceive(Context context, Intent data) {
            processEvent(data);
        }
    };

    private PasswordlessLock(Options options, LockCallback callback) {
        this.options = options;
        this.callback = callback;
    }

    /**
     * Lock.Options holds the configuration used in the Auth0 Passwordless Authentication API.
     *
     * @return the Lock.Options for this Lock instance.
     */
    public Options getOptions() {
        return options;
    }

    /**
     * Creates a new Lock.Builder instance with the given account and callback.
     *
     * @param account  details to use against the Auth0 Authentication API.
     * @param callback that will receive the authentication results.
     * @return a new Lock.Builder instance.
     */
    @SuppressWarnings("unused")
    public static Builder newBuilder(@NonNull Auth0 account, @NonNull LockCallback callback) {
        if (account.getTelemetry() != null) {
            Log.v(TAG, String.format("Using Telemetry %s (%s) and Library %s", Constants.LIBRARY_NAME, com.auth0.android.lock.BuildConfig.VERSION_NAME, BuildConfig.VERSION_NAME));
            account.setTelemetry(new Telemetry(Constants.LIBRARY_NAME, com.auth0.android.lock.BuildConfig.VERSION_NAME, BuildConfig.VERSION_NAME));
        }
        return new PasswordlessLock.Builder(account, callback);
    }

    /**
     * Builds a new intent to launch LockActivity with the previously configured options
     *
     * @param activity a valid Activity context
     * @return the intent to which the user has to call startActivity or startActivityForResult
     */
    @SuppressWarnings("unused")
    public Intent newIntent(Activity activity) {
        Intent lockIntent = new Intent(activity, PasswordlessLockActivity.class);
        lockIntent.putExtra(Constants.OPTIONS_EXTRA, options);
        return lockIntent;
    }

    /**
     * Should be called on the Activity holding the Lock instance's OnCreate method, as it
     * ensures the correct Lock lifecycle handling.
     *
     * @param activity a valid Activity context
     */
    @SuppressWarnings("unused")
    public void onCreate(Activity activity) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.AUTHENTICATION_ACTION);
        filter.addAction(Constants.CANCELED_ACTION);
        LocalBroadcastManager.getInstance(activity).registerReceiver(this.receiver, filter);
    }

    /**
     * Should be called on the Activity holding the Lock instance's OnDestroy method, as it
     * ensures the correct Lock lifecycle handling.
     *
     * @param activity a valid Activity context
     */
    @SuppressWarnings("unused")
    public void onDestroy(Activity activity) {
        LocalBroadcastManager.getInstance(activity).unregisterReceiver(this.receiver);
    }

    /**
     * Should be called on the Activity holding the Lock instance's OnActivityResult method, as
     * it ensures the correct parsing of the received Authentication data.
     *
     * @param activity   a valid Activity context
     * @param resultCode received in the OnActivityResult call
     * @param data       intent received in the OnActivityResult call
     */
    @SuppressWarnings("unused")
    public void onActivityResult(Activity activity, int resultCode, @NonNull Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            processEvent(data);
            return;
        }

        //user pressed back.
        callback.onEvent(LockEvent.CANCELED, new Intent());
    }

    private void processEvent(Intent data) {
        String action = data.getAction();
        switch (action) {
            case Constants.AUTHENTICATION_ACTION:
                Log.v(TAG, "AUTHENTICATION action received in our BroadcastReceiver");
                callback.onEvent(LockEvent.AUTHENTICATION, data);
                break;
            case Constants.CANCELED_ACTION:
                Log.v(TAG, "CANCELED action received in our BroadcastReceiver");
                callback.onEvent(LockEvent.CANCELED, new Intent());
                break;
        }
    }

    /**
     * Helper Builder to generate the Lock.Options to use on the Auth0 Passwordless Authentication.
     */
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
        public Builder(Auth0 account, LockCallback callback) {
            HashMap<String, Object> defaultParams = new HashMap<>(ParameterBuilder.newAuthenticationBuilder().setDevice(Build.MODEL).asDictionary());
            this.callback = callback;
            options = new Options();
            options.setAccount(account);
            options.setAuthenticationParameters(defaultParams);
        }

        /**
         * Finishes the construction of the Lock.Options and generates a new Lock instance
         * with those Lock.Options.
         *
         * @return a new Lock instance configured as in the Builder.
         */
        public PasswordlessLock build() {
            if (options.getAccount() == null) {
                Log.e(TAG, "You need to specify the com.auth0.Auth0 object with the Auth0 Account details.");
                throw new IllegalStateException("Missing Auth0 account information.");
            }
            if (callback == null) {
                Log.e(TAG, "You need to specify the callback object to receive the Authentication result.");
                throw new IllegalStateException("Missing callback.");
            }
            Log.v(TAG, "PasswordlessLock instance created");
            return new PasswordlessLock(options, callback);
        }

        /**
         * Defines the Passwordless type to use in the Authentication as Code. Default value is to use Code
         *
         * @return the current Builder instance
         */
        public Builder useCode() {
            options.setUseCodePasswordless(true);
            return this;
        }

        /**
         * Defines the Passwordless type to use in the Authentication as Link. Default value is to use Code
         *
         * @return the current Builder instance
         */
        public Builder useLink() {
            options.setUseCodePasswordless(false);
            return this;
        }

        /**
         * Whether to use the Browser for Authentication with Identity Providers or the inner WebView.
         *
         * @param useBrowser or WebView. By default, the Authentication flow will use the WebView.
         * @return the current Builder instance
         */
        public Builder useBrowser(boolean useBrowser) {
            options.setUseBrowser(useBrowser);
            return this;
        }

        /**
         * Whether to use PKCE or the implicit token grant when performing calls to /authenticate.
         * Default is {@code false}
         *
         * @param usePKCE if Lock will use PKCE instead of the implicit token grant.
         * @return the current Builder instance
         */
        public Builder usePKCE(boolean usePKCE) {
            options.setUsePKCE(usePKCE);
            return this;
        }

        /**
         * Whether the PasswordlessLockActivity can be closed when pressing the Back key or not.
         *
         * @param closable or not. By default, the LockActivity is not closable.
         * @return the current builder instance
         */
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
        public Builder withTheme(@NonNull Theme theme) {
            options.withTheme(theme);
            return this;
        }

        /**
         * Social Button style to use when Social connections are available. If social
         * is the only connection type, by default it will use the Big style. If social and db or
         * enterprise are present and there's only one social connection, the button will use the
         * Big style. In the rest of the cases, it will use Small style.
         *
         * @param style a valid SocialButtonStyle.
         * @return the current builder instance
         */
        public Builder withSocialButtonStyle(@SocialButtonStyle int style) {
            options.setSocialButtonStyle(style);
            return this;
        }

        /**
         * Additional Authentication parameters can be set to use with different Identity Providers.
         *
         * @param authenticationParameters a non-null Map containing the parameters as Key-Values
         * @return the current builder instance
         */
        public Builder withAuthenticationParameters(@NonNull Map<String, Object> authenticationParameters) {
            options.setAuthenticationParameters(new HashMap<>(authenticationParameters));
            return this;
        }

        /**
         * Locally filters the Auth0 Connections that are shown in the login widgets.
         *
         * @param connections a non-null List containing the allowed Auth0 Connections.
         * @return the current builder instance
         */
        public Builder onlyUseConnections(@NonNull List<String> connections) {
            options.setConnections(connections);
            return this;
        }

        /**
         * Uses the given AuthProviderResolver to ask for Native IdentityProviders.
         *
         * @param resolver the AuthProviderResolver to use
         * @return the current builder instance
         */
        public Builder withProviderResolver(@NonNull AuthProviderResolver resolver) {
            ProviderResolverManager.set(resolver);
            return this;
        }
    }
}
