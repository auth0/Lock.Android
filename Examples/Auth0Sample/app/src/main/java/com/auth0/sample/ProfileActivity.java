/*
 * ProfileActivity.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.sample;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.core.UserProfile;
import com.auth0.lock.Lock;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;


public class ProfileActivity extends ActionBarActivity {

    private static final String SAMPLE_API_URL = "http://localhost:3001/secured/ping";
    private static final String TAG = ProfileActivity.class.getName();

    AsyncHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        UserProfile profile = getIntent().getParcelableExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER);
        TextView greetingTextView = (TextView) findViewById(R.id.welcome_message);
        greetingTextView.setText("Welcome " + profile.getName());
        ImageView profileImageView = (ImageView) findViewById(R.id.profile_image);
        if (profile.getPictureURL() != null) {
            ImageLoader.getInstance().displayImage(profile.getPictureURL(), profileImageView);
        }

        client = new AsyncHttpClient();
        Button callAPIButton = (Button) findViewById(R.id.call_api_button);
        callAPIButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callAPI();
            }
        });
    }

    private void callAPI() {
        client.get(this, SAMPLE_API_URL, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.v(TAG, "We got the secured data successfully");
                showAlertDialog(ProfileActivity.this, "We got the secured data successfully");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e(TAG, "Failed to contact API", error);
                showAlertDialog(ProfileActivity.this, "Please download the API seed so that you can call it.");
            }
        });
    }

    public static AlertDialog showAlertDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder
                .setMessage(message)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        return builder.show();
    }
}
