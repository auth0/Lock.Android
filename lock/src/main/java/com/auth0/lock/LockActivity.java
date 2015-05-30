package com.auth0.lock;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Application;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.IdentityProvider;
import com.auth0.identity.IdentityProviderCallback;
import com.auth0.lock.error.ErrorDialogBuilder;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.ChangePasswordEvent;
import com.auth0.lock.event.EnterpriseAuthenticationRequest;
import com.auth0.lock.event.IdentityProviderAuthenticationEvent;
import com.auth0.lock.event.IdentityProviderAuthenticationRequestEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.event.SignUpEvent;
import com.auth0.lock.event.SocialCredentialEvent;
import com.auth0.lock.event.SystemErrorEvent;
import com.auth0.lock.fragment.LoadingFragment;
import com.auth0.lock.identity.LockIdentityProviderCallback;
import com.auth0.lock.util.LockFragmentBuilder;
import com.squareup.otto.Subscribe;

/**
 * Activity that handles DB, Social and Enterprise Authentication.
 * You'll need to declare it in your app's {@code AndroidManifest.xml}:
 * <pre>{@code
 * <activity
 *      android:name="com.auth0.lock.LockActivity"
 *      android:theme="@style/Lock.Theme"
 *      android:screenOrientation="portrait"
 *      android:launchMode="singleTask">
 *      <intent-filter>
 *          <action android:name="android.intent.action.VIEW"/>
 *          <category android:name="android.intent.category.DEFAULT"/>
 *          <category android:name="android.intent.category.BROWSABLE"/>
 *          <data android:scheme="a0YOUR_CLIENT_ID" android:host="YOUR_AUTH0_APP_DOMAIN"/>
 *      </intent-filter>
 * </activity>
 * }</pre>
 *
 * Then just start it like any other Android activity:
 * <pre>{@code
 * Intent lockIntent = new Intent(this, LockActivity.class);
 * startActivity(lockIntent);
 * }
 * </pre>
 *
 * And finally register listeners in {@link android.support.v4.content.LocalBroadcastManager} for these actions:
 * <ul>
 *     <li><b>Lock.Authentication</b>: Sent on a successful authentication with {@link com.auth0.core.UserProfile} and {@link com.auth0.core.Token}.
 *     Or both {@code null} when {@link Lock#loginAfterSignUp} is {@code false} </li>
 *     <li><b>Lock.Cancel</b>: Sent when the user's closes the activity by pressing the back button without authenticating. (Only if {@link Lock#closable} is {@code true}</li>
 *     <li><b>Lock.ChangePassword</b>: Sent when the user changes the password successfully.</li>
 * </ul>
 *
 * All these action names are defined in these constants: {@link Lock#AUTHENTICATION_ACTION}, {@link Lock#CANCEL_ACTION} and {@link com.auth0.lock.Lock#CHANGE_PASSWORD_ACTION}.
 */
public class LockActivity extends FragmentActivity {

    public static final String SIGN_UP_MODE_ARGUMENT = "SIGN_UP_MODE";

    private static final String TAG = LockActivity.class.getName();

    LockFragmentBuilder builder;
    Lock lock;

    private ProgressDialog progressDialog;
    private IdentityProvider identity;
    private LocalBroadcastManager broadcastManager;
    private IdentityProviderCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.com_auth0_activity_lock);
        lock = getLock();
        builder = newFragmentBuilder(getLock());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.com_auth0_container, new LoadingFragment())
                    .commit();
        }
        broadcastManager = LocalBroadcastManager.getInstance(this);
        callback = new LockIdentityProviderCallback(lock.getBus());
        if (lock.isFullScreen()) {
            fullscreenMode();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.lock.getBus().register(this);
        lock.resetAllProviders();
    }

    @Override
    protected void onStop() {
        super.onStop();
        this.lock.getBus().unregister(this);
        lock.resetAllProviders();
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Uri uri = getIntent().getData();
        Log.v(TAG, "Resuming activity with data " + uri);
        if (identity != null) {
            boolean valid = identity.authorize(this, IdentityProvider.WEBVIEW_AUTH_REQUEST_CODE, RESULT_OK, getIntent());
            if (!valid) {
                dismissProgressDialog();
            }
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (lock.isFullScreen()) {
            fullscreenMode();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        getIntent().setData(null);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.v(TAG, "Received new Intent with URI " + intent.getData());
        identity = lock.getDefaultProvider();
        setIntent(intent);
    }

    @Override
    protected void onDestroy() {
        identity = null;
        dismissProgressDialog();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "Child activity result obtained");
        if (identity != null) {
            identity.authorize(this, requestCode, resultCode, data);
        }
        lock.getCredentialStore().onActivityResult(this, requestCode, resultCode, data);
    }


    @Override
    public void onBackPressed() {
        final double count = getSupportFragmentManager().getBackStackEntryCount();
        if ((!lock.isClosable() && count >= 1) || lock.isClosable()) {
            if (count == 0) {
                broadcastManager.sendBroadcast(new Intent(Lock.CANCEL_ACTION));
            }
            super.onBackPressed();
        }
    }

    @Subscribe public void onApplicationLoaded(Application application) {
        Log.d(TAG, "Application configuration loaded for id " + application.getId());
        Configuration configuration = new Configuration(application, lock.getConnections(), lock.getDefaultDatabaseConnection());
        lock.setConfiguration(configuration);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.com_auth0_container, builder.root())
                .commit();
    }

    @Subscribe public void onAuthentication(AuthenticationEvent event) {
        UserProfile profile = event.getProfile();
        Token token = event.getToken();
        Log.i(TAG, "Authenticated user " + profile.getName());
        Intent result = new Intent(Lock.AUTHENTICATION_ACTION)
                .putExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER, profile)
                .putExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER, token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(result);
        dismissProgressDialog();
        finish();
    }

    @Subscribe public void onSignUpEvent(SignUpEvent event) {
        Log.i(TAG, "Signed up user " + event.getUsername());
        broadcastManager.sendBroadcast(new Intent(Lock.AUTHENTICATION_ACTION));
        dismissProgressDialog();
        finish();
    }

    @Subscribe public void onResetPassword(ChangePasswordEvent event) {
        Log.d(TAG, "Changed password");
        ErrorDialogBuilder.showAlertDialog(this, event);
        broadcastManager.sendBroadcast(new Intent(Lock.CHANGE_PASSWORD_ACTION));
        getSupportFragmentManager().popBackStack();
    }

    @Subscribe public void onAuthenticationError(AuthenticationError error) {
        Log.e(TAG, "Failed to authenticate user", error.getThrowable());
        if (identity != null) {
            identity.clearSession();
        }
        dismissProgressDialog();
        ErrorDialogBuilder.showAlertDialog(this, error);
    }

    @Subscribe public void onSystemError(SystemErrorEvent event) {
        Log.e(TAG, "Android System error", event.getError());
        dismissProgressDialog();
        event.getErrorDialog().show();
    }

    @Subscribe public void onNavigationEvent(NavigationEvent event) {
        Log.v(TAG, "About to handle navigation " + event);
        if (NavigationEvent.BACK.equals(event)) {
            getSupportFragmentManager().popBackStack();
            return;
        }

        Fragment fragment = null;
        switch (event) {
            case SIGN_UP:
                fragment = builder.signUp();
                break;
            case RESET_PASSWORD:
                fragment = builder.resetPassword();
                break;
        }
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.com_auth0_container, fragment)
                    .addToBackStack(event.name())
                    .commit();
        }
    }

    @Subscribe public void onEnterpriseAuthenticationRequest(EnterpriseAuthenticationRequest event) {
        Fragment fragment = builder.enterpriseLoginWithConnection(event.getConnection());
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.com_auth0_container, fragment)
                .addToBackStack(event.getConnection().getName())
                .commit();
    }

    @Subscribe public void onIdentityProviderAuthentication(IdentityProviderAuthenticationRequestEvent event) {
        Log.v(TAG, "About to authenticate with service " + event.getServiceName());
        identity = lock.providerForName(event.getServiceName());
        identity.setCallback(callback);
        identity.start(this, event, lock.getConfiguration().getApplication());
        progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Subscribe
    public void onSocialAuthentication(IdentityProviderAuthenticationEvent event) {
        final Token token = event.getToken();
        lock.getAPIClient().fetchUserProfile(token.getIdToken(), new BaseCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile userProfile) {
                lock.getBus().post(new AuthenticationEvent(userProfile, token));
            }

            @Override
            public void onFailure(Throwable error) {
                lock.getBus().post(new LoginAuthenticationErrorBuilder().buildFrom(error));
            }
        });
    }

    @Subscribe public void onSocialCredentialEvent(SocialCredentialEvent event) {
        Log.v(TAG, "Received social accessToken " + event.getAccessToken());
        lock.getAPIClient().socialLogin(event.getService(), event.getAccessToken(), lock.getAuthenticationParameters(), new AuthenticationCallback() {
            @Override
            public void onSuccess(UserProfile profile, Token token) {
                lock.getBus().post(new AuthenticationEvent(profile, token));
            }

            @Override
            public void onFailure(Throwable error) {
                lock.getBus().post(new LoginAuthenticationErrorBuilder().buildFrom(error));
            }
        });
    }

    private LockFragmentBuilder newFragmentBuilder(Lock lock) {
        final LockFragmentBuilder builder = new LockFragmentBuilder(getLock());
        Intent intent = getIntent();
        if (intent != null) {
            Bundle extras = intent.getExtras();
            builder.setSignUpMode(extras != null && extras.getBoolean(SIGN_UP_MODE_ARGUMENT));
        }
        return builder;
    }

    private void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
        progressDialog = null;
    }

    private Lock getLock() {
        if (lock != null) {
            return lock;
        }
        LockProvider provider = (LockProvider) getApplication();
        return provider.getLock();
    }

    private void fullscreenMode() {
        if (Build.VERSION.SDK_INT >= 16) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        } else {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }
}
