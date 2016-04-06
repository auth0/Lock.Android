package com.auth0.lock.event;

import com.auth0.identity.IdentityProviderCallback;

public class AuthorizationCodeEvent {
    private final String authorizationCode;
    private final String codeVerifier;
    private final String redirectUri;
    private final IdentityProviderCallback callback;

    public AuthorizationCodeEvent(String authorizationCode, String codeVerifier, String redirectUri, IdentityProviderCallback callback) {
        this.authorizationCode = authorizationCode;
        this.codeVerifier = codeVerifier;
        this.redirectUri = redirectUri;
        this.callback = callback;
    }

    public String getAuthorizationCode() {
        return authorizationCode;
    }

    public String getCodeVerifier() {
        return codeVerifier;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public IdentityProviderCallback getCallback() {
        return callback;
    }
}
