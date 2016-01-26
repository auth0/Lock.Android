package com.auth0.android.lock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.authentication.result.Authentication;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by nikolaseu on 1/21/16.
 */
public class Lock {

    public static final String OPTIONS_EXTRA = "options";
    private AuthenticationCallback callback;
    private final LockOptions options;

    public static final String OPTIONS_KEY = "com.auth0.android.lock.key.options";
    public static final String AUTHENTICATION_ACTION = "com.auth0.android.lock.action.Authentication";
    public static final String CANCELED_ACTION = "com.auth0.android.lock.action.Canceled";

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

    protected Lock(LockOptions options, AuthenticationCallback callback) {
        this.options = options;
        this.callback = callback;
    }

    public static Builder newBuilder() {
        return new Lock.Builder();
    }

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
    public void onActivityResult(Activity activity, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            processEvent(data);
            return;
        }

        //user pressed back.
        callback.onCanceled();
    }

    private void processEvent(Intent eventData) {
        //black magic. (?) parse eventData
        Authentication authentication = eventData.getParcelableExtra("authentication");

        if (authentication != null) {
            callback.onAuthentication(authentication);
        } else {
            Auth0Exception up = new Auth0Exception("wops!");
            callback.onError(up);
            //throw up. haha
        }
    }

    public static class Builder {
        private LockOptions options;
        private AuthenticationCallback callback;

        public Builder() {
            options = new LockOptions();
        }

        public Lock build() {
            return new Lock(options, callback);
        }

        public Builder withAccount(Auth0 account) {
            options.account = account;
            return this;
        }

        public Builder withCallback(AuthenticationCallback callback) {
            this.callback = callback;
            return this;
        }

        public Builder useBrowser(boolean useBrowser) {
            options.useBrowser = useBrowser;
            return this;
        }

        public Builder closable(boolean closable) {
            options.closable = closable;
            return this;
        }

        public Builder fullscreen(boolean fullscreen) {
            options.fullscreen = fullscreen;
            return this;
        }

        public Builder withAuthenticationParameters(Map<String, Object> authenticationParameters) {
            if (authenticationParameters instanceof HashMap) {
                options.authenticationParameters = (HashMap<String, Object>) authenticationParameters;
            } else {
                options.authenticationParameters = new HashMap<String, Object>(authenticationParameters);
            }

            return this;
        }

        public Builder onlyUseConnections(List<String> connections) {
            options.connections = connections;
            return this;
        }

        public Builder doNotSendSDKInfo() {
            options.sendSDKInfo = false;
            return this;
        }

        public Builder useEmail() {
            options.useEmail = true;
            return this;
        }

        public Builder disableSignUp() {
            options.signUpEnabled = false;
            return this;
        }

        public Builder disableChangePassword() {
            options.changePasswordEnabled = false;
            return this;
        }
    }
}
