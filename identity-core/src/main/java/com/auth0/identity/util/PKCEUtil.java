package com.auth0.identity.util;

import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.identity.IdentityProviderCallback;
import com.auth0.identity.R;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PKCEUtil {
    private static final String TAG = PKCEUtil.class.getSimpleName();

    private final AuthenticationAPIClient apiClient;
    private final String codeVerifier;
    private final String redirectUri;

    public PKCEUtil(AuthenticationAPIClient apiClient, String redirectUri) {
        this.apiClient = apiClient;
        this.redirectUri = redirectUri;
        this.codeVerifier = generateCodeVerifier();
        Log.v(TAG, "The code verifier is: " + codeVerifier);
    }

    public String generateCodeChallenge() throws NoSuchAlgorithmException {
        byte[] input;
        try {
            input = codeVerifier.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "String to ASCII error: " + e.getMessage());
            throw new NoSuchAlgorithmException("Code challenge can't be generated on this device.");
        }

        MessageDigest md;
        byte[] signature;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(input, 0, input.length);
            signature = md.digest();
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Digest SHA256 error: " + e.getMessage());
            throw new NoSuchAlgorithmException("Code challenge can't be generated on this device.");
        }
        String challenge = Base64.encodeToString(signature, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        Log.d(TAG, "The code challenge is: " + challenge);
        return challenge;
    }

    public void getToken(String authorizationCode, @NonNull final IdentityProviderCallback callback) {
        apiClient.tokenRequest(authorizationCode, codeVerifier, redirectUri).start(new AuthenticationCallback() {
            @Override
            public void onSuccess(UserProfile profile, Token token) {
                Log.e(TAG, "OnSuccess called after PKCE");
                callback.onSuccess(token);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "OnFailure called with error " + error.getMessage());
                callback.onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_access_denied_message, error);
            }
        });
    }

    public static boolean isAvailable() {
        try {
            //noinspection ResultOfMethodCallIgnored
            "".getBytes("US-ASCII");
            MessageDigest.getInstance("SHA-256");
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }

    private String generateCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }
}
