package com.auth0.lock.credentials;

import android.app.Activity;
import android.content.Intent;

public class NullCredentialStore implements CredentialStore {
    @Override
    public void saveFromActivity(Activity activity, String email, String password, CredentialStoreCallback callback) {
        if (callback != null) {
            callback.onSuccess();
        }
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {}
}
