package com.auth0.android.lock;

import androidx.annotation.NonNull;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.Callback;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.result.Credentials;

import org.jetbrains.annotations.NotNull;

public class WebCallbackWrapper implements Callback<Credentials, AuthenticationException> {

    private final AuthCallback baseCallback;

    public WebCallbackWrapper(@NonNull AuthCallback callback) {
        this.baseCallback = callback;
    }

    @Override
    public void onFailure(@NotNull AuthenticationException error) {
        baseCallback.onFailure(error);
    }

    @Override
    public void onSuccess(@NotNull Credentials credentials) {
        baseCallback.onSuccess(credentials);
    }
}
