/*
 * MyActivity.java
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

package com.auth0.app;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
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
import com.auth0.lock.LockContext;
import com.auth0.lock.passwordless.LockPasswordlessActivity;
import com.auth0.lock.receiver.AuthenticationReceiver;

import static com.auth0.app.R.id;
import static com.auth0.app.R.layout;

public class MyActivity extends AppCompatActivity {

    public static final String TAG = MyActivity.class.getName();

    private static final int TYPE_CODE = 0;

    Token token;
    UserProfile profile;
    Button refreshButton;
    int selectedType;

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
                LockContext.getLock(MyActivity.this).loginFromActivity(MyActivity.this);
            }
        });

        final Button smsButton = (Button) findViewById(id.sms_button);
        smsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = buildPasswordlessTypeSelectionDialog(
                        R.string.passwordless_type_selection_sms_title,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (selectedType == TYPE_CODE) {
                                    LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.MODE_SMS_CODE);
                                } else {
                                    LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.MODE_SMS_MAGIC_LINK);
                                }
                            }
                        });
                dialog.show();
            }
        });

        final Button emailButton = (Button) findViewById(id.email_button);
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog dialog = buildPasswordlessTypeSelectionDialog(
                        R.string.passwordless_type_selection_email_title,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (selectedType == TYPE_CODE) {
                                    LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.MODE_EMAIL_CODE);
                                } else {
                                    LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.MODE_EMAIL_MAGIC_LINK);
                                }
                            }
                        });
                dialog.show();
            }
        });

        refreshButton = (Button) findViewById(id.refresh_jwt_button);
        refreshButton.setEnabled(false);
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Lock lock = LockContext.getLock(MyActivity.this);
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

    private AlertDialog buildPasswordlessTypeSelectionDialog(int titleResId, DialogInterface.OnClickListener okClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MyActivity.this);
        builder.setTitle(titleResId);
        builder.setSingleChoiceItems(R.array.passwordless_types, selectedType, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedType = which;
            }
        });
        builder.setPositiveButton(android.R.string.ok, okClickListener);
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.dismiss();
            }
        });
        return builder.create();
    }
}
