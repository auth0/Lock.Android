package com.auth0.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class that holds a user's token information.
 */
public class Token extends TokenJson implements Parcelable {

    @SuppressWarnings("unused") // Moshi uses this!
    private Token() {

    }
    public Token( String idToken,
                 String accessToken,
                 String type,
                 String refreshToken) {
        this.idToken = idToken;
        this.accessToken = accessToken;
        this.type = type;
        this.refreshToken = refreshToken;
		init();
    }

    private void init()
    {
        checkArgument(idToken != null, "idToken must be non-null");
    }


    public String getIdToken() {
        return idToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getType() {
        return type;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    protected Token(Parcel in) {
        idToken = in.readString();
        accessToken = in.readString();
        type = in.readString();
        refreshToken = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(idToken);
        dest.writeString(accessToken);
        dest.writeString(type);
        dest.writeString(refreshToken);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Token> CREATOR = new Parcelable.Creator<Token>() {
        @Override
        public Token createFromParcel(Parcel in) {
            return new Token(in);
        }

        @Override
        public Token[] newArray(int size) {
            return new Token[size];
        }
    };
}
