package com.auth0.lock.credentials;

/**
 * Interface for objects that can save securely the user's credentials.
 */
public interface CredentialStore {

    void save(String email, String password, CredentialStoreCallback callback);

}
