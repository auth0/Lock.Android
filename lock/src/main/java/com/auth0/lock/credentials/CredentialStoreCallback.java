package com.auth0.lock.credentials;

/**
 * Callback for {@link CredentialStore} actions
 */
public interface CredentialStoreCallback {

    int CREDENTIAL_STORE_SAVE_FAILED = 0;
    int CREDENTIAL_STORE_SAVE_CANCELLED = 1;

    void onSuccess();

    void onError(int errorCode, Throwable e);
}
