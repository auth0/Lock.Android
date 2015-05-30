package com.auth0.lock.util;

import android.util.Log;

import com.auth0.lock.credentials.CredentialStoreCallback;
import com.auth0.lock.fragment.SignUpFormFragment;

public abstract class LockCredentialStoreCallback implements CredentialStoreCallback {
    @Override
    public void onSuccess() {
        postEvent();
    }

    @Override
    public void onError(int errorCode, Throwable e) {
        Log.w(LockCredentialStoreCallback.class.getName(), "Failed to save credentials with code " + errorCode, e);
        postEvent();
    }

    protected abstract void postEvent();
}
