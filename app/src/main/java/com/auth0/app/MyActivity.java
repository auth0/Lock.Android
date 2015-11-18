package com.auth0.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.auth0.api.callback.RefreshIdTokenCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.auth0.lock.email.LockEmailActivity;
import com.auth0.lock.passwordless.LockPasswordlessActivity;
import com.auth0.lock.receiver.AuthenticationReceiver;
import com.auth0.lock.sms.LockSMSActivity;

import static com.auth0.app.R.id;
import static com.auth0.app.R.layout;

public class MyActivity extends AppCompatActivity {

    public static final String TAG = MyActivity.class.getName();

    Token token;
    UserProfile profile;
    Button refreshButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_my);
        Toolbar toolbar = (Toolbar) findViewById(id.lock_toolbar);
        setSupportActionBar(toolbar);
        final Button loginButton = (Button) findViewById(id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lock.getLock(MyActivity.this).loginFromActivity(MyActivity.this);
            }
        });

        final Button smsButton = (Button) findViewById(id.sms_button);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.TYPE_SMS, true);
                //LockSMSActivity.showFrom(MyActivity.this, true);
            }
        });

        final Button emailButton = (Button) findViewById(id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.TYPE_EMAIL, true);
                //LockEmailActivity.showFrom(MyActivity.this, true);
            }
        });

        refreshButton = (Button) findViewById(id.refresh_jwt_button);
        refreshButton.setEnabled(false);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lock lock = Lock.getLock(MyActivity.this);
                lock.getAuthenticationAPIClient().delegationWithRefreshToken(token.getRefreshToken())
                        .start(new RefreshIdTokenCallback() {
                            @Override
                            public void onSuccess(String idToken, String tokenType, int expiresIn) {
                                Log.d(TAG, "User " + profile.getName() + " with new token " + idToken);
                            }

                            @Override
                            public void onFailure(Throwable error) {
                                Log.e(TAG, "Failed to refresh JWT", error);
                            }
                        });
            }
        });
        authenticationReceiver.registerIn(LocalBroadcastManager.getInstance(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        authenticationReceiver.unregisterFrom(LocalBroadcastManager.getInstance(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return item.getItemId() == R.id.action_settings || super.onOptionsItemSelected(item);
    }

    private AuthenticationReceiver authenticationReceiver = new AuthenticationReceiver() {
        @Override
        public void onAuthentication(@NonNull UserProfile profile, @NonNull Token token) {
            MyActivity.this.profile = profile;
            MyActivity.this.token = token;
            Log.d(TAG, "User " + profile.getName() + " with token " + token.getIdToken());
            TextView welcomeLabel = (TextView) findViewById(id.welcome_label);
            welcomeLabel.setText("Herzlich Willkommen " + profile.getName());
            refreshButton.setEnabled(token.getRefreshToken() != null);
        }

        @Override
        protected void onSignUp() {
            Log.i(TAG, "Signed Up user");
            refreshButton.setEnabled(token != null && token.getRefreshToken() != null);
        }

        @Override
        protected void onCancel() {
            Log.i(TAG, "User Cancelled");
        }
    };

}
