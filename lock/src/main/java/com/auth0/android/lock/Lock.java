/*
 * Lock.java
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

import com.auth0.Auth0;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.utils.LockException;
import com.auth0.authentication.ParameterBuilder;
import com.auth0.authentication.result.Authentication;
import com.auth0.authentication.result.Credentials;
import com.auth0.authentication.result.UserProfile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Lock {

    private AuthenticationCallback callback;
    private final Options options;

    public static final String OPTIONS_EXTRA = "com.auth0.android.lock.key.Options";

    static final String AUTHENTICATION_ACTION = "com.auth0.android.lock.action.Authentication";
    static final String CANCELED_ACTION = "com.auth0.android.lock.action.Canceled";

    static final String ID_TOKEN_EXTRA = "com.auth0.android.lock.extra.IdToken";
    static final String ACCESS_TOKEN_EXTRA = "com.auth0.android.lock.extra.AccessToken";
    static final String TOKEN_TYPE_EXTRA = "com.auth0.android.lock.extra.TokenType";
    static final String REFRESH_TOKEN_EXTRA = "com.auth0.android.lock.extra.RefreshToken";
    static final String PROFILE_EXTRA = "com.auth0.android.lock.extra.Profile";

    /**
     * Listens to LockActivity broadcasts and fires the correct action on the AuthenticationCallback.
     */
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent data) {
            // Get extra data included in the Intent
            String action = data.getAction();
            if (action.equals(Lock.AUTHENTICATION_ACTION)) {
                processEvent(data);
            } else if (action.equals(Lock.CANCELED_ACTION)) {
                callback.onCanceled();
            }
        }
    };

    private Lock(Options options, AuthenticationCallback callback) {
        this.options = options;
        this.callback = callback;
    }

    /**
     * Lock.Options holds the configuration used in the Auth0 Authentication API.
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
    public static Builder newBuilder(@NonNull Auth0 account, @NonNull AuthenticationCallback callback) {
        return new Lock.Builder(account, callback);
    }

    /**
     * Builds a new intent to launch LockActivity with the previously configured options
     *
     * @param activity a valid Activity context
     * @return the intent to which the user has to call startActivity or startActivityForResult
     */
    @SuppressWarnings("unused")
    public Intent newIntent(Activity activity) {
        Intent lockIntent = new Intent(activity, LockActivity.class);
        lockIntent.putExtra(OPTIONS_EXTRA, options);
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
        filter.addAction(Lock.AUTHENTICATION_ACTION);
        filter.addAction(Lock.CANCELED_ACTION);
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
        // unregister listener (if something was registered)
        if (this.receiver != null) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(this.receiver);
            this.receiver = null;
        }
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
        callback.onCanceled();
    }

    /**
     * Extracts the Authentication data from the intent data.
     *
     * @param eventData the intent received at the end of the login process.
     */
    private void processEvent(Intent eventData) {
        String idToken = eventData.getStringExtra(Lock.ID_TOKEN_EXTRA);
        String accessToken = eventData.getStringExtra(Lock.ACCESS_TOKEN_EXTRA);
        String tokenType = eventData.getStringExtra(Lock.TOKEN_TYPE_EXTRA);
        String refreshToken = eventData.getStringExtra(Lock.REFRESH_TOKEN_EXTRA);
        Credentials credentials = new Credentials(idToken, accessToken, tokenType, refreshToken);
        UserProfile profile = (UserProfile) eventData.getSerializableExtra(Lock.PROFILE_EXTRA);

        Authentication authentication = new Authentication(profile, credentials);

        if (idToken != null && accessToken != null) {
            callback.onAuthentication(authentication);
        } else {
            LockException up = new LockException(R.string.com_auth0_lock_social_error_authentication);
            callback.onError(up);
            //throw up. haha
        }
    }

    /**
     * Helper Builder to generate the Lock.Options to use on the Auth0 Authentication.
     */
    public static class Builder {
        private static final String TAG = Builder.class.getSimpleName();
        private Options options;
        private AuthenticationCallback callback;

        /**
         * Creates a new Lock.Builder instance with the given account and callback.
         *
         * @param account  details to use against the Auth0 Authentication API.
         * @param callback that will receive the authentication results.
         */
        public Builder(Auth0 account, AuthenticationCallback callback) {
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
        public Lock build() {
            if (options.getAccount() == null) {
                Log.e(TAG, "You need to specify the com.auth0.Auth0 object with the Auth0 Account details.");
                throw new IllegalStateException("Missing Auth0 account information.");
            }
            if (callback == null) {
                Log.e(TAG, "You need to specify the AuthenticationCallback object to receive the Authentication result.");
                throw new IllegalStateException("Missing AuthenticationCallback.");
            }
            return new Lock(options, callback);
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
         * Whether the LockActivity can be closed when pressing the Back key or not.
         *
         * @param closable or not. By default, the LockActivity is not closable.
         * @return the current builder instance
         */
        public Builder closable(boolean closable) {
            options.setClosable(closable);
            return this;
        }

        /**
         * Whether the LockActivity will go fullscreen or will show the status bar.
         *
         * @param fullscreen or not. By default, the LockActivity will not be Fullscreen.
         * @return the current builder instance
         */
        public Builder fullscreen(boolean fullscreen) {
            options.setFullscreen(fullscreen);
            return this;
        }

        /**
         * Additional Authentication parameters can be set to use with different Identity Providers.
         *
         * @param authenticationParameters a non-null Map containing the parameters as Key-Values
         * @return the current builder instance
         */
        public Builder withAuthenticationParameters(@NonNull Map<String, Object> authenticationParameters) {
            if (authenticationParameters instanceof HashMap) {
                options.setAuthenticationParameters(new HashMap<>(authenticationParameters));
            } else {
                options.setAuthenticationParameters(new HashMap<>(authenticationParameters));
            }

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
         * SDK information sent to the Auth0 API with each request can be disabled here. By default,
         * sending the SDK information is enabled.
         *
         * @return the current builder instance
         */
        public Builder doNotSendSDKInfo() {
            options.setSendSDKInfo(false);
            return this;
        }

        /**
         * Username style to use in the Login and Sign Up text fields. Defaults to the Dashboard
         * configuration of "requires_username".
         *
         * @param style a valid UsernameStyle.
         * @return the current builder instance
         */
        public Builder withUsernameStyle(UsernameStyle style) {
            options.setUsernameStyle(style);
            return this;
        }

        /**
         * Sign Up can be disabled locally, regardless the Dashboard configuration.
         *
         * @return the current builder instance
         */
        public Builder disableSignUp() {
            options.setSignUpEnabled(false);
            return this;
        }

        /**
         * Password change can be disabled locally, regardless the Dashboard configuration.
         *
         * @return the current builder instance
         */
        public Builder disableChangePassword() {
            options.setChangePasswordEnabled(false);
            return this;
        }

        /**
         * Change the connection name to use on the Database authentication flow.
         * Defaults to the first Database connection found.
         *
         * @param connectionName Must exist in the Application configuration on the Dashboard.
         * @return the current builder instance
         */
        public Builder setDefaultDatabaseConnection(String connectionName) {
            options.useDatabaseConnection(connectionName);
            return this;
        }

        /**
         * Whether to login after a successful sign up callback. Defaults to true.
         *
         * @param login after sign up or not
         * @return the current builder instance
         */
        public Builder loginAfterSignUp(boolean login) {
            options.setLoginAfterSignUp(login);
            return this;
        }
    }
}
