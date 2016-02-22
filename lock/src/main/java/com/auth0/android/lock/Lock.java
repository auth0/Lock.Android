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

import com.auth0.Auth0;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.utils.LockException;
import com.auth0.authentication.ParameterBuilder;
import com.auth0.authentication.result.Authentication;
import com.auth0.authentication.result.Token;
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

    public Options getOptions() {
        return options;
    }

    public static Builder newBuilder(@NonNull Auth0 account, @NonNull AuthenticationCallback callback) {
        return new Lock.Builder(account, callback);
    }

    /**
     * Builds a new intent to launch LockActivity with the given options
     *
     * @param activity a valid Activity context
     * @return the intent to which the user has to call startActivity or startActivityForResult
     */
    public Intent newIntent(Activity activity) {
        Intent lockIntent = new Intent(activity, LockActivity.class);
        lockIntent.putExtra(OPTIONS_EXTRA, options);
        return lockIntent;
    }

    public void onCreate(Activity activity) {
        // nikolaseu: register broadcast listener only when callback is set?
        //            i.e. when NOT using startForResult
        //  L= lets leave this as an improvement

        //if (callback != null) //can the callback be optional?
        IntentFilter filter = new IntentFilter();
        filter.addAction(Lock.AUTHENTICATION_ACTION);
        filter.addAction(Lock.CANCELED_ACTION);
        LocalBroadcastManager.getInstance(activity).registerReceiver(this.receiver, filter);
    }

    public void onDestroy(Activity activity) {
        // unregister listener (if something was registered)
        if (this.receiver != null) {
            LocalBroadcastManager.getInstance(activity).unregisterReceiver(this.receiver);
            this.receiver = null;
        }
    }

    /*
    Evaluate changing the name of this method: parseActivityResult? processResult?
    */
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
        Token token = new Token(idToken, accessToken, tokenType, refreshToken);
        UserProfile profile = (UserProfile) eventData.getSerializableExtra(Lock.PROFILE_EXTRA);

        Authentication authentication = new Authentication(profile, token);

        if (idToken != null && accessToken != null) {
            callback.onAuthentication(authentication);
        } else {
            LockException up = new LockException(R.string.com_auth0_lock_social_error_authentication);
            callback.onError(up);
            //throw up. haha
        }
    }

    public static class Builder {
        private Options options;
        private AuthenticationCallback callback;

        public Builder(Auth0 account, AuthenticationCallback callback) {
            HashMap<String, Object> defaultParams = new HashMap<>(ParameterBuilder.newAuthenticationBuilder().setDevice(Build.MODEL).asDictionary());
            this.callback = callback;
            options = new Options();
            options.setAccount(account);
            options.setAuthenticationParameters(defaultParams);
        }

        public Lock build() {
            if (options.getAccount() == null) {
                throw new IllegalArgumentException("Missing Auth0 account information.");
            }
            if (callback == null) {
                throw new IllegalArgumentException("Missing AuthenticationCallback.");
            }
            return new Lock(options, callback);
        }

        public Builder useBrowser(boolean useBrowser) {
            options.setUseBrowser(useBrowser);
            return this;
        }

        public Builder closable(boolean closable) {
            options.setClosable(closable);
            return this;
        }

        public Builder fullscreen(boolean fullscreen) {
            options.setFullscreen(fullscreen);
            return this;
        }

        public Builder withAuthenticationParameters(@NonNull Map<String, Object> authenticationParameters) {
            if (authenticationParameters instanceof HashMap) {
                options.setAuthenticationParameters((HashMap<String, Object>) authenticationParameters);
            } else {
                options.setAuthenticationParameters(new HashMap<String, Object>(authenticationParameters));
            }

            return this;
        }

        public Builder onlyUseConnections(List<String> connections) {
            options.setConnections(connections);
            return this;
        }

        public Builder doNotSendSDKInfo() {
            options.setSendSDKInfo(false);
            return this;
        }

        public Builder withUsernameStyle(UsernameStyle style) {
            options.setUsernameStyle(style);
            return this;
        }

        public Builder disableSignUp() {
            options.setSignUpEnabled(false);
            return this;
        }

        public Builder disableChangePassword() {
            options.setChangePasswordEnabled(false);
            return this;
        }

        public Builder setDefaultDatabaseConnection(String connectionName) {
            options.useDatabaseConnection(connectionName);
            return this;
        }

        public Builder loginAfterSignUp(boolean login) {
            options.setLoginAfterSignUp(login);
            return this;
        }
    }
}
