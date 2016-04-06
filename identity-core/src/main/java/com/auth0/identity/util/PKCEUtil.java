package com.auth0.identity.util;

import android.util.Base64;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PKCEUtil {
    private static final String TAG = PKCEUtil.class.getSimpleName();

    private final String codeVerifier;

    public PKCEUtil() {
        this.codeVerifier = generateCodeVerifier();
        Log.v(TAG, "CodeVerifier: " + codeVerifier);
    }

    public String generateCodeChallenge() {
        byte[] code = new byte[0];
        try {
            code = codeVerifier.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            Log.w(TAG, "String to ASCII error: " + e.getMessage());
        }

        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-256");
            md.update(code);
        } catch (NoSuchAlgorithmException e) {
            Log.w(TAG, "Digest SHA256 error: " + e.getMessage());
        }
        String challenge = Base64.encodeToString(code, Base64.URL_SAFE | Base64.NO_WRAP | Base64.NO_PADDING);
        Log.d(TAG, "CodeChallenge: " + challenge);
        return challenge;
    }

    public boolean isAvailable() {
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

    public String getCodeVerifier() {
        return codeVerifier;
    }
}
