package com.auth0.android.lock.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.auth0.Auth0;
import com.auth0.Auth0Exception;
import com.auth0.android.lock.AuthenticationCallback;
import com.auth0.android.lock.Lock;
import com.auth0.authentication.result.Authentication;

/**
 * Created by lbalmaceda on 1/21/16.
 */
public class DemoActivity extends AppCompatActivity implements AuthenticationCallback {
    private static final String AUTH0_CLIENT_ID = "Owu62gnGsRYhk1v9SfB3c6IUbIJcRIze";
    private static final String AUTH0_DOMAIN = "http://lbalmaceda.auth0.com";
    private static final int AUTH_REQUEST = 333;
    private Lock lock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create account
        Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);

        // create/configure lock
        lock = Lock.newBuilder()
                .withAccount(auth0)
                .withCallback(this)
                .build();
        lock.onCreate(DemoActivity.this);

        // launch, the results will be received in the callback
        startActivity(lock.newIntent(this));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //should we ask for null lock?
        lock.onDestroy(DemoActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //should we ask for null lock?
        if (requestCode == AUTH_REQUEST) {
            lock.onActivityResult(DemoActivity.this, resultCode, data);
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAuthentication(Authentication authentication) {

    }

    @Override
    public void onCanceled() {

    }

    @Override
    public void onError(Auth0Exception error) {

    }
}
