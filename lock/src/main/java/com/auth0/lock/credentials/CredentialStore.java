package com.auth0.lock.credentials;

import android.app.Activity;
import android.content.Intent;

/**
 * Interface for objects that can save securely the user's credentials.
 */
public interface CredentialStore {

    void saveFromActivity(Activity activity, String email, String password, CredentialStoreCallback callback);

    void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data);

}
