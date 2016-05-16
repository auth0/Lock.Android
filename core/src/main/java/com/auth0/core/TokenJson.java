package com.auth0.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class that holds a user's token information.
 */
public class TokenJson {
    @Json(name = "id_token")
    protected String idToken;
    @Json(name = "access_token")
    protected String accessToken;
    @Json(name = "token_type")
    protected String type;
    @Json(name = "refresh_token")
    protected String refreshToken;

    @SuppressWarnings("unused") // Moshi uses this!
    protected TokenJson() {

    }

    public boolean isEmpty() {
        return idToken==null&&accessToken==null&&type==null&&refreshToken==null;
    }
}
