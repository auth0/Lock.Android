package com.auth0.android.lock.views.next;

import android.app.Dialog;
import android.support.annotation.NonNull;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.result.Credentials;

/**
 * Created by lbalmaceda on 04/12/2017.
 */

//Wrapper to use for authentication in WebAuthProvider calls and API Client calls.
public abstract class CredentialsCallback implements BaseCallback<Credentials, AuthenticationException>, AuthCallback {
}
