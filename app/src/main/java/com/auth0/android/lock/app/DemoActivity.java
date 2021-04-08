/*
 * DemoActivity.java
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

package com.auth0.android.lock.app;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.Lock;
import com.auth0.android.lock.LockCallback;
import com.auth0.android.lock.PasswordlessLock;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.lock.utils.LockException;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.VoidCallback;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;

import java.util.ArrayList;
import java.util.List;

public class DemoActivity extends AppCompatActivity {
    private Lock lock;
    private PasswordlessLock passwordlessLock;

    private View rootLayout;
    private RadioGroup groupSubmitMode;
    private CheckBox checkboxClosable;
    private RadioGroup groupPasswordlessChannel;
    private RadioGroup groupPasswordlessMode;
    private CheckBox checkboxConnectionsDB;
    private CheckBox checkboxConnectionsEnterprise;
    private CheckBox checkboxConnectionsSocial;
    private CheckBox checkboxConnectionsPasswordless;
    private CheckBox checkboxHideMainScreenTitle;
    private RadioGroup groupDefaultDB;
    private RadioGroup groupUsernameStyle;
    private CheckBox checkboxLoginAfterSignUp;
    private CheckBox checkboxScreenLogIn;
    private CheckBox checkboxScreenSignUp;
    private CheckBox checkboxScreenReset;
    private RadioGroup groupInitialScreen;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.demo_activity);

        rootLayout = findViewById(R.id.scrollView);

        //Basic
        groupSubmitMode = findViewById(R.id.group_submitmode);
        checkboxClosable = findViewById(R.id.checkbox_closable);
        checkboxHideMainScreenTitle = findViewById(R.id.checkbox_hide_title);

        checkboxConnectionsDB = findViewById(R.id.checkbox_connections_db);
        checkboxConnectionsEnterprise = findViewById(R.id.checkbox_connections_enterprise);
        checkboxConnectionsSocial = findViewById(R.id.checkbox_connections_social);
        checkboxConnectionsPasswordless = findViewById(R.id.checkbox_connections_Passwordless);

        groupPasswordlessChannel = findViewById(R.id.group_passwordless_channel);
        groupPasswordlessMode = findViewById(R.id.group_passwordless_mode);

        //Advanced
        groupDefaultDB = findViewById(R.id.group_default_db);
        groupUsernameStyle = findViewById(R.id.group_username_style);
        checkboxLoginAfterSignUp = findViewById(R.id.checkbox_login_after_signup);

        checkboxScreenLogIn = findViewById(R.id.checkbox_enable_login);
        checkboxScreenSignUp = findViewById(R.id.checkbox_enable_signup);
        checkboxScreenReset = findViewById(R.id.checkbox_enable_reset);
        groupInitialScreen = findViewById(R.id.group_initial_screen);

        //Buttons
        final LinearLayout advancedContainer = findViewById(R.id.advanced_container);
        CheckBox checkboxShowAdvanced = findViewById(R.id.checkbox_show_advanced);
        checkboxShowAdvanced.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                advancedContainer.setVisibility(b ? View.VISIBLE : View.GONE);
            }
        });

        Button btnShowLockClassic = findViewById(R.id.btn_show_lock_classic);
        btnShowLockClassic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showClassicLock();
            }
        });

        Button btnShowLockPasswordless = findViewById(R.id.btn_show_lock_passwordless);
        btnShowLockPasswordless.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPasswordlessLock();
            }
        });

        Button btnShowUniversalLogin = findViewById(R.id.btn_show_universal_login);
        btnShowUniversalLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showWebAuth();
            }
        });

        Button btnClearSession = findViewById(R.id.btn_clear_session);
        btnClearSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearSession();
            }
        });
    }

    private void showWebAuth() {
        WebAuthProvider.login(getAccount())
                .withScheme("demo")
                .withAudience(String.format("https://%s/userinfo", getString(R.string.com_auth0_domain)))
                .start(this, loginCallback);
    }

    private void clearSession() {
        WebAuthProvider.logout(getAccount())
                .withScheme("demo")
                .start(this, logoutCallback);
    }

    private void showClassicLock() {
        final Lock.Builder builder = Lock.newBuilder(getAccount(), callback);
        builder.withScheme("demo");
        builder.closable(checkboxClosable.isChecked());
        builder.useLabeledSubmitButton(groupSubmitMode.getCheckedRadioButtonId() == R.id.radio_use_label);
        builder.loginAfterSignUp(checkboxLoginAfterSignUp.isChecked());

        if (groupUsernameStyle.getCheckedRadioButtonId() == R.id.radio_username_style_email) {
            builder.withUsernameStyle(UsernameStyle.EMAIL);
        } else if (groupUsernameStyle.getCheckedRadioButtonId() == R.id.radio_username_style_username) {
            builder.withUsernameStyle(UsernameStyle.USERNAME);
        }

        builder.allowLogIn(checkboxScreenLogIn.isChecked());
        builder.allowSignUp(checkboxScreenSignUp.isChecked());
        builder.allowForgotPassword(checkboxScreenReset.isChecked());

        if (groupInitialScreen.getCheckedRadioButtonId() == R.id.radio_initial_reset) {
            builder.initialScreen(InitialScreen.FORGOT_PASSWORD);
        } else if (groupInitialScreen.getCheckedRadioButtonId() == R.id.radio_initial_signup) {
            builder.initialScreen(InitialScreen.SIGN_UP);
        } else {
            builder.initialScreen(InitialScreen.LOG_IN);
        }

        builder.allowedConnections(generateConnections());
        if (checkboxConnectionsDB.isChecked()) {
            if (groupDefaultDB.getCheckedRadioButtonId() == R.id.radio_default_db_policy) {
                builder.setDefaultDatabaseConnection("with-strength");
            } else if (groupDefaultDB.getCheckedRadioButtonId() == R.id.radio_default_db_mfa) {
                builder.setDefaultDatabaseConnection("mfa-connection");
            } else {
                builder.setDefaultDatabaseConnection("Username-Password-Authentication");
            }
        }

        builder.hideMainScreenTitle(checkboxHideMainScreenTitle.isChecked());
        lock = builder.build(this);

        startActivity(lock.newIntent(this));
    }


    private void showPasswordlessLock() {
        final PasswordlessLock.Builder builder = PasswordlessLock.newBuilder(getAccount(), callback);
        builder.withScheme("demo");
        builder.closable(checkboxClosable.isChecked());

        if (groupPasswordlessMode.getCheckedRadioButtonId() == R.id.radio_use_link) {
            builder.useLink();
        } else {
            builder.useCode();
        }

        builder.allowedConnections(generateConnections());
        builder.hideMainScreenTitle(checkboxHideMainScreenTitle.isChecked());

        passwordlessLock = builder.build(this);

        startActivity(passwordlessLock.newIntent(this));
    }

    private Auth0 getAccount() {
        Auth0 account = new Auth0(getString(R.string.com_auth0_client_id), getString(R.string.com_auth0_domain));
        account.setOIDCConformant(true);
        account.setLoggingEnabled(true);
        return account;
    }

    private List<String> generateConnections() {
        List<String> connections = new ArrayList<>();
        if (checkboxConnectionsDB.isChecked()) {
            connections.add("Username-Password-Authentication");
            connections.add("mfa-connection");
            connections.add("with-strength");
        }
        if (checkboxConnectionsEnterprise.isChecked()) {
            connections.add("ad");
            connections.add("another");
            connections.add("fake-saml");
            connections.add("contoso-ad");
        }
        if (checkboxConnectionsSocial.isChecked()) {
            connections.add("google-oauth2");
            connections.add("twitter");
            connections.add("facebook");
            connections.add("paypal-sandbox");
        }
        if (checkboxConnectionsPasswordless.isChecked()) {
            connections.add(groupPasswordlessChannel.getCheckedRadioButtonId() == R.id.radio_use_sms ? "sms" : "email");
        }
        if (connections.isEmpty()) {
            connections.add("no-connection");
        }
        return connections;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (lock != null) {
            lock.onDestroy(this);
        }
        if (passwordlessLock != null) {
            passwordlessLock.onDestroy(this);
        }
    }

    /**
     * Shows a Snackbar on the bottom of the layout
     *
     * @param message the text to show.
     */
    private void showResult(String message) {
        Snackbar.make(rootLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private final LockCallback callback = new AuthenticationCallback() {
        @Override
        public void onAuthentication(@NonNull Credentials credentials) {
            showResult("OK > " + credentials.getAccessToken());
        }

        @Override
        public void onCanceled() {
            showResult("User pressed back.");
        }

        @Override
        public void onError(@NonNull LockException error) {
            showResult(error.getMessage());
        }
    };

    private final AuthCallback loginCallback = new AuthCallback() {
        @Override
        public void onFailure(@NonNull Dialog dialog) {
            dialog.show();
        }

        @Override
        public void onFailure(@NonNull AuthenticationException exception) {
            showResult("Failed > " + exception.getDescription());
        }

        @Override
        public void onSuccess(@NonNull Credentials credentials) {
            showResult("OK > " + credentials.getAccessToken());
        }
    };

    private final VoidCallback logoutCallback = new VoidCallback() {
        @Override
        public void onFailure(@NonNull Auth0Exception error) {
            showResult("Log out cancelled");
        }

        @Override
        public void onSuccess(@Nullable Void payload) {
            showResult("Logged out!");
        }
    };
}
