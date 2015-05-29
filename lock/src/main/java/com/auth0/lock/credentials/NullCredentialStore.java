package com.auth0.lock.credentials;

public class NullCredentialStore implements CredentialStore {
    @Override
    public void save(String email, String password, CredentialStoreCallback callback) {
        if (callback != null) {
            callback.onSuccess();
        }
    }
}
