package com.auth0.android.lock;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.auth0.android.Auth0;
import com.auth0.android.Auth0Exception;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.lock.views.next.BusActivity;
import com.auth0.android.lock.views.next.CredentialsCallback;
import com.auth0.android.lock.views.next.OAuthListActivity;
import com.auth0.android.lock.views.next.configuration.internal.ClientInfo;
import com.auth0.android.lock.views.next.configuration.internal.ClientInfoCallback;
import com.auth0.android.lock.views.next.configuration.internal.Configuration;
import com.auth0.android.lock.views.next.configuration.internal.Connection;
import com.auth0.android.lock.views.next.configuration.internal.DefaultConnectionResolver;
import com.auth0.android.lock.views.next.configuration.internal.OAuthConnection;
import com.auth0.android.lock.views.next.events.AuthenticationEvent;
import com.auth0.android.lock.views.next.events.NavigationEvent;
import com.auth0.android.lock.views.next.forms.LogInScreen;
import com.auth0.android.provider.WebAuthProvider;
import com.auth0.android.result.Credentials;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lbalmaceda on 29/11/2017.
 */

public class NextActivity extends BusActivity {

    private AuthenticationAPIClient api;
    private DefaultConnectionResolver connectionResolver;
    private Auth0 account;
    private Configuration configuration;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final LogInScreen screen = new LogInScreen(this);

        account = new Auth0("Owu62gnGsRYhk1v9SfB3c6IUbIJcRIze", "lbalmaceda.auth0.com");
        ServiceLocator.setAccount(account);
        api = new AuthenticationAPIClient(account);
        ServiceLocator.setAPIClient(api);
        connectionResolver = new DefaultConnectionResolver();

        new ClientInfo(account, new ClientInfoCallback() {
            @Override
            public void onClientInfoReceived(@NonNull List<Connection> connections) {
                final Configuration configuration = new Configuration(connections, new Options());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        screen.setConfiguration(configuration);
                        NextActivity.this.configuration = configuration;
                    }
                });
            }

            @Override
            public void onClientInfoError(Auth0Exception exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        screen.setConfiguration(null);
                    }
                });
            }
        });
        Toast.makeText(this, "Loading client info..", Toast.LENGTH_SHORT).show();

        setContentView(R.layout.a0_activity_test);
        FrameLayout parent = (FrameLayout) findViewById(R.id.a0_content);
        ScrollView scrollView = new ScrollView(this);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int px24 = getResources().getDimensionPixelSize(R.dimen.a0_size_24);
        int px32 = getResources().getDimensionPixelSize(R.dimen.a0_size_32);
        int px40 = getResources().getDimensionPixelSize(R.dimen.a0_size_40);
        params.setMargins(px40, 0, px40, 0);
        screen.setPadding(0, px24, 0, px24);
        scrollView.addView(screen, params);
        parent.addView(scrollView);

    }

    @Subscribe
    public void onAuthenticationEvent(AuthenticationEvent event) {
        Toast.makeText(this, String.format("Authentication: %d", event.getAction()), Toast.LENGTH_SHORT).show();
        switch (event.getAction()) {
            case AuthenticationEvent.USERNAME_PASSWORD_LOGIN:
                api.login(event.getIdentity(), event.getPassword(), connectionResolver.resolveFor(event.getIdentity()))
                        .start(authenticationCallback);
                break;
            case AuthenticationEvent.WEBAUTH_LOGIN:
                WebAuthProvider.init(account)
                        .withScheme("demo") //Fixme: Use a setting to set this value
                        .withConnection(event.getConnection())
                        .start(this, authenticationCallback);
                break;
            case AuthenticationEvent.FORGOT_PASSWORD:
                break;
        }
    }

    @Subscribe
    public void onNavigationEvent(NavigationEvent event) {
        Toast.makeText(this, String.format("Navigation: %d", event.getAction()), Toast.LENGTH_SHORT).show();
        switch (event.getAction()) {
            case NavigationEvent.NAVIGATE_TO_LOGIN:
                break;
            case NavigationEvent.NAVIGATE_TO_SIGN_UP:
                break;
            case NavigationEvent.NAVIGATE_TO_FORGOT_PASSWORD:
                //TODO:
                break;
            case NavigationEvent.NAVIGATE_TO_SOCIAL_LIST:
                if (configuration != null) {
                    OAuthListActivity.startFor(this, new ArrayList<OAuthConnection>(configuration.getSocialConnections()));
                }
                break;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        outState.putBoolean();
    }

    private CredentialsCallback authenticationCallback = new CredentialsCallback() {
        @Override
        public void onSuccess(final Credentials payload) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NextActivity.this, "Success: " + payload.getAccessToken(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        @Override
        public void onFailure(@NonNull final Dialog dialog) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                }
            });
        }

        @Override
        public void onFailure(final AuthenticationException error) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NextActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
}
