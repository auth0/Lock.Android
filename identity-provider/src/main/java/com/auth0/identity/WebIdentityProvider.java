/*
 * WebIdentityProvider.java
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

package com.auth0.identity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.auth0.core.Application;
import com.auth0.core.Token;
import com.auth0.identity.web.CallbackParser;
import com.auth0.identity.web.WebViewActivity;

import java.util.Map;

public class WebIdentityProvider implements IdentityProvider {

    private boolean useWebView;
    private IdentityProviderCallback callback;
    private CallbackParser parser;

    public WebIdentityProvider(CallbackParser parser) {
        this.parser = parser;
        this.useWebView = false;
    }

    public void setUseWebView(boolean useWebView) {
        this.useWebView = useWebView;
    }

    public void setCallback(IdentityProviderCallback callback) {
        this.callback = callback;
    }

    public void start(Activity activity, IdentityProviderRequest request, Application application) {
        final Uri url = request.getAuthenticationUri(application);
        final Intent intent;
        if (this.useWebView) {
            intent = new Intent(activity, WebViewActivity.class);
            intent.setData(url);
            intent.putExtra(WebViewActivity.SERVICE_NAME_EXTRA, request.getServiceName());
            activity.startActivityForResult(intent, WEBVIEW_AUTH_REQUEST_CODE);
        } else {
            intent = new Intent(Intent.ACTION_VIEW, url);
            activity.startActivity(intent);
        }
    }

    @Override
    public void stop() {}

    @Override
    public boolean authorize(Activity activity, int requestCode, int resultCode, Intent data) {
        Uri uri = data != null ? data.getData() : null;
        Log.v(WebIdentityProvider.class.getName(), "Authenticating with webflow with data " + uri);
        boolean isValid = requestCode == WEBVIEW_AUTH_REQUEST_CODE && resultCode == Activity.RESULT_OK && uri != null;
        if (isValid) {
            final Map<String, String> values = parser.getValuesFromUri(uri);
            if (values.containsKey("error")) {
                final int message = "access_denied".equalsIgnoreCase(values.get("error")) ? R.string.social_access_denied_message : R.string.social_error_message;
                callback.onFailure(R.string.social_error_title, message, null);
            } else if(values.size() > 0) {
                Log.d(WebIdentityProvider.class.getName(), "Authenticated using web flow");
                callback.onSuccess(new Token(values.get("id_token"), values.get("access_token"), values.get("token_type"), values.get("refresh_token")));
            }
        }
        return isValid;
    }

    @Override
    public void clearSession() {}
}
