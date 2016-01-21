package com.auth0.android.lock.app;

import android.support.v7.app.AppCompatActivity;

/**
 * Created by lbalmaceda on 1/21/16.
 */
public class DemoActivity extends AppCompatActivity implements AuthenticationCallback {
  private static final AUTH0_CLIENT_ID = "";
  private static final AUTH0_DOMAIN = "";
  private static final int AUTH_REQUEST = 333;
  private Lock lock;

  @Override
  public void onCreate(){
    // create account
    Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);

    // create/configure lock
    lock = Lock.Builder.newBuilder()
      .withAccount(auth0)
      .withCallback(this);
    lock.onCreate(DemoActivity.this);

    // launch, the results will be received in the callback
    startActivity(lock.newIntent(this));
  }

  @Override
  public void onDestroy(){
    //should we ask for null lock?
    lock.onDestroy(DemoActivity.this);
  }

  @Override
  public void onActivityResult(Intent data, int requestCode, int resultCode) {
    //should we ask for null lock?
    if (requestCode == AUTH_REQUEST){
      lock.onActivityResult(DemoActivity.this, resultCode, data);
      return;
    }
  }

  @Override
  public void onAuthentication(Authentication authentication){

  }

  @Override
  public void onCancelled(){

  }
}
