package com.auth0.sdk.auth0sample;

import java.io.Serializable;

import org.json.JSONObject;

public class AuthenticationActivityResult implements Serializable {
    public String accessToken;
    public String JsonWebToken;
    public JSONObject User;
}