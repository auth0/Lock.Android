/*
 * WebViewActivity.java
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

package com.auth0.lock.web;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.auth0.lock.R;
import com.auth0.lock.util.SocialResources;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;

public class WebViewActivity extends RoboActivity {

    public static final String SERVICE_NAME = "serviceName";
    @InjectView(tag = "lock_webview") WebView webView;
    @InjectView(tag = "lock_progressbar") SmoothProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        final ActionBar bar = getActionBar();
        if (bar != null) {
            String serviceName = getIntent().getStringExtra(SERVICE_NAME);

            bar.setIcon(android.R.color.transparent);
            bar.setDisplayShowTitleEnabled(false);
            bar.setDisplayUseLogoEnabled(false);
            bar.setDisplayHomeAsUpEnabled(false);
            bar.setDisplayShowCustomEnabled(true);
            View view = LayoutInflater.from(this).inflate(R.layout.webview_action_bar, null);
            final TextView iconLabel = (TextView) view.findViewById(R.id.social_icon_label);
            iconLabel.setTypeface(SocialResources.socialFont(this));
            iconLabel.setText(SocialResources.iconForSocialService(this, serviceName));
            iconLabel.setTextColor(SocialResources.textColorForSocialService(this, serviceName));
            bar.setBackgroundDrawable(new ColorDrawable(SocialResources.colorForSocialService(this, serviceName)));
            bar.setCustomView(view);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        final Intent intent = getIntent();
        final Uri uri = intent.getData();
        final String redirectUrl = uri.getQueryParameter("redirect_uri");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(redirectUrl)) {
                    final Intent intent = new Intent();
                    intent.setData(Uri.parse(url));
                    setResult(RESULT_OK, intent);
                    finish();
                    return true;
                }
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.loadUrl(uri.toString());
    }
}
