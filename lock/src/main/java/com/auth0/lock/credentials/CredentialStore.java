package com.auth0.lock.credentials;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.auth0.core.UserProfile;

/**
 * Interface for objects that can save securely the user's credentials.
 */
public interface CredentialStore {

    void saveFromActivity(Activity activity, String username, String email, String password, String pictureUrl, CredentialStoreCallback callback);

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

}
