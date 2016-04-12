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

/**
 * Class that implements the OAuth Proof Key for Code Exchange (PKCE) authorization flow.
 */
public class PKCEUtil {
    private static final String TAG = PKCEUtil.class.getSimpleName();

    private final AuthenticationAPIClient apiClient;
    private final String codeVerifier;
    private final String redirectUri;

    /**
     * Creates a new instance of this class with the given AuthenticationAPIClient.
     * The instance should be disposed after a call to getToken().
     *
     * @param apiClient   to get the OAuth Token.
     * @param redirectUri going to be used in the OAuth code request.
     */
    public PKCEUtil(@NonNull AuthenticationAPIClient apiClient, String redirectUri) {
        this(apiClient, redirectUri, generateCodeVerifier());
    }

    PKCEUtil(@NonNull AuthenticationAPIClient apiClient, @NonNull String redirectUri, @NonNull String codeVerifier) {
        this.apiClient = apiClient;
        this.redirectUri = redirectUri;
        this.codeVerifier = codeVerifier;
        Log.v(TAG, "The code verifier is: " + codeVerifier);
    }

    /**
     * Generates the code challenge to be used in the call to /authorize.
     * Before calling this method you should test if the device is capable of using the PKCE
     * flow, by calling isAvailable().
     *
     * @throws NoSuchAlgorithmException if the algorithms needed for PKCE to work aren't available
     *                                  on this device.
     */
    public String generateCodeChallenge() throws NoSuchAlgorithmException {
        byte[] input;
        try {
            input = codeVerifier.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "String to ASCII error: " + e.getMessage());
            throw new NoSuchAlgorithmException("Code challenge can't be generated on this device.");
        }

        byte[] signature;
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
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

    /**
     * Performs a request to the Auth0 API to get the OAuth Token and end the PKCE flow.
     * The instance of this class must be disposed after this method is called.
     *
     * @param authorizationCode received in the call to /authorize with a "grant_type=code"
     * @param callback          to notify the result of this call to.
     */
    public void getToken(String authorizationCode, @NonNull final IdentityProviderCallback callback) {
        apiClient.tokenRequest(authorizationCode, codeVerifier, redirectUri).start(new AuthenticationCallback() {
            @Override
            public void onSuccess(UserProfile profile, Token token) {
                Log.i(TAG, "OnSuccess called after PKCE");
                callback.onSuccess(token);
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e(TAG, "OnFailure called with error " + error.getMessage());
                callback.onFailure(R.string.com_auth0_social_error_title, R.string.com_auth0_social_access_denied_message, error);
            }
        });
    }

    /**
     * Checks if this device is capable of using the PKCE flow when performing calls to the
     * /authorize endpoint.
     *
     * @return if this device can use PKCE flow or not.
     */
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

    private static String generateCodeVerifier() {
        SecureRandom sr = new SecureRandom();
        byte[] code = new byte[32];
        sr.nextBytes(code);
        return Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
    }
}
