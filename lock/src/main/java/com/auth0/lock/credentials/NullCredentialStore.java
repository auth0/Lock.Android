package com.auth0.lock.credentials;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.auth0.core.UserProfile;

public class NullCredentialStore implements CredentialStore {

    @Override
    public void saveFromActivity(Activity activity, String username, String email, String password, String pictureUrl, CredentialStoreCallback callback) {
        callback.onSuccess();
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {}
}
