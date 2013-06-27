package com.auth0.sdk.auth0sample;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public class AuthenticationActivity
        extends Activity {

    protected static final String TAG = AuthenticationActivity.class.getSimpleName();
    protected static final String BASE_URL = "https://%s.auth0.com";
    
     protected static final String BASE_AUTH_URL =  BASE_URL + "/login/?client=%s&response_type=token&redirect_uri=%s&scope=openid";
    protected static final String BASE_AUTHORIZE_URL = BASE_URL + "/authorize/?client_id=%s&response_type=token&redirect_uri=%s&connection=%s&scope=openid";

    public static final int AUTH_REQUEST_COMPLETE = 100;
    public static final int ACCESS_DENIED = 1000;
    public static final int WEB_NETWORK_ERROR = 1001;

    public static final String  AUTHENTICATION_RESULT = "AUTHENTICATION_RESULT";
    public static final String  AUTHENTICATION_SETUP = "AUTHENTICATION_SETUP";

    private WebView webView;
    private ProgressDialog progressDialog;
    private String callback;
    
    private class AuthenticationWebViewClient
            extends WebViewClient {
    	
    	AuthenticationActivitySetup setup;
    	public AuthenticationWebViewClient(AuthenticationActivitySetup setup) {
    		this.setup = setup;
    	}
    	
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            //Check for final redirect to callback URL
            if (url.startsWith(callback)) {

                Uri uri = Uri.parse(url);

                if( uri.getQueryParameter("error") != null )
                    AuthenticationActivity.this.setResult(ACCESS_DENIED);
                else
                {
                    //Quick and dirty parsing
                    String[] r = url.split("#");
                    String[] tokens = r[1].split("&");

                    String jwt = tokens[1];
                    String access_token = tokens[0];

                    Intent data = new Intent();

                    AuthenticationActivityResult result = new AuthenticationActivityResult();
                    result.accessToken = access_token.split("=")[1];
                    result.JsonWebToken = jwt.split("=")[1];
                    
                    data.putExtra(AUTHENTICATION_RESULT, result);

                    AuthenticationActivity.this.setResult(RESULT_OK, data);
                }
                AuthenticationActivity.this.finish();
                return true;
            }
            return false;
        }
        
       
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            progressDialog.dismiss();
            String errMsg = failingUrl + ":" + errorCode + ":" + description;
            Log.e(TAG, "Error during authentication: " + errMsg);
            AuthenticationActivity.this.setResult(WEB_NETWORK_ERROR);
            AuthenticationActivity.this.finish();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressDialog.dismiss();
            webView.setVisibility(View.VISIBLE);
        }
    }

    private class AuthenticationWebChromeClient
            extends WebChromeClient {
        public void onProgressChanged(WebView view, int progress) {
            progressDialog.setProgress(progress);
        }
    }

    WebViewClient getWebViewClient(AuthenticationActivitySetup setup) {
        return new AuthenticationWebViewClient(setup);
    }

    WebChromeClient getWebChromeClient() {
        return new AuthenticationWebChromeClient();
    }
    
    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        Context context = this;
        Intent intent = getIntent();
        AuthenticationActivitySetup setup = (AuthenticationActivitySetup)intent.getSerializableExtra(AUTHENTICATION_SETUP);

        if( setup == null )
        {
            throw new IllegalArgumentException("No setup object");
        }

        //Check parameters
        if(setup.callback==null || setup.callback ==null || setup.clientId ==null)
        {
            throw new IllegalArgumentException("Invalid setup");
        }

        this.callback = setup.callback;

        String authUrl;

        if(setup.connection != null)
        {
            authUrl = String.format(BASE_AUTHORIZE_URL, setup.tenant, setup.clientId, this.callback, setup.connection);
        }
        else
        {
            authUrl = String.format(BASE_AUTH_URL, setup.tenant, setup.clientId, this.callback);
        }

        Log.v(TAG, "Authentication URL" + authUrl);

        // no window title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout mainLayout = new LinearLayout(context);
        mainLayout.setPadding(0, 0, 0, 0);

        FrameLayout.LayoutParams frame = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading");
        progressDialog.setCancelable(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setProgress(0); // set percentage completed to 0%

        webView = new WebView(context);
        webView.setVisibility(View.INVISIBLE);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setWebViewClient(getWebViewClient(setup));
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setLayoutParams(frame);
        webView.getSettings().setSavePassword(false);
        webView.loadUrl(authUrl);

        webView.setWebChromeClient(getWebChromeClient());

        mainLayout.addView(webView);

        setContentView(mainLayout, new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT));

        progressDialog.show();
    }
}
