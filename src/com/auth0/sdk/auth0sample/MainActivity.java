package com.auth0.sdk.auth0sample;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity {

	static final String ClientID = "YOUR_CLIENT_ID";
	static final String Tenant = "YOUR_TENANT";
	static final String Callback = "https://localhost/client";
    static final String Connection = "google-oauth2"; //any other connection

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent authActivity = new Intent(MainActivity.this,
                        com.auth0.sdk.auth0sample.AuthenticationActivity.class);

                AuthenticationActivitySetup setup;
                setup = new AuthenticationActivitySetup(Tenant, ClientID, Callback, Connection);

                authActivity.putExtra(AuthenticationActivity.AUTHENTICATION_SETUP, setup);

                startActivityForResult(authActivity, AuthenticationActivity.AUTH_REQUEST_COMPLETE);
            }
        });

        Button login_widget = (Button) findViewById(R.id.login_widget);
        login_widget.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent authActivity = new Intent(MainActivity.this,
                        com.auth0.sdk.auth0sample.AuthenticationActivity.class);

                AuthenticationActivitySetup setup;
                setup = new AuthenticationActivitySetup(Tenant, ClientID, Callback);

                authActivity.putExtra(AuthenticationActivity.AUTHENTICATION_SETUP, setup);

                startActivityForResult(authActivity, AuthenticationActivity.AUTH_REQUEST_COMPLETE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent authActivityResult) {
        super.onActivityResult(requestCode, resultCode, authActivityResult);

        switch(requestCode)
        {
            case AuthenticationActivity.AUTH_REQUEST_COMPLETE:
                if(resultCode==RESULT_OK)
                {
                    AuthenticationActivityResult result;
                    result = (AuthenticationActivityResult) authActivityResult.getSerializableExtra(AuthenticationActivity.AUTHENTICATION_RESULT);

                    //((TextView) findViewById(R.id.access_token)).setText(result.accessToken);
                    //((TextView) findViewById(R.id.jwt)).setText(result.JsonWebToken);
                    
                    String userInfoUrl = String.format("https://api.auth0.com/userinfo?access_token=%s", result.accessToken);
                    new AsyncTask<String, Void, JSONObject>() {
                    	@Override
            	    	protected JSONObject doInBackground(String... url) {
            	          JSONObject json = RestJsonClient.connect(url[0]);
            	          
            	          return json;
            	        }
            	        
            	        @Override
            	        protected void onPostExecute(JSONObject user) {
            	        	 try {
								((TextView) findViewById(R.id.user)).setText(user.toString(2));
							} catch (JSONException e) {
								e.printStackTrace();
							}
            	        }
                    }.execute(userInfoUrl);
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
