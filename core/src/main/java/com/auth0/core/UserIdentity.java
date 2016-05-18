package com.auth0.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.HashMap;
import java.util.Map;


/**
 * Class that holds the information from a Identity Provider like Facebook or Twitter.
 */
public class UserIdentity implements Parcelable {

    private static final String USER_ID_KEY = "user_id";
    private static final String CONNECTION_KEY = "connection";
    private static final String PROVIDER_KEY = "provider";
    private static final String IS_SOCIAL_KEY = "isSocial";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";
    private static final String PROFILE_DATA_KEY = "profileData";

    @Json(name=USER_ID_KEY)
    private final String id;
    @Json(name=CONNECTION_KEY)
    private final String connection;
    @Json(name=PROVIDER_KEY)
    private final String provider;
    @Json(name=IS_SOCIAL_KEY)
    private final boolean social;
    @Json(name=ACCESS_TOKEN_KEY)
    private final String accessToken;
    @Json(name=ACCESS_TOKEN_SECRET_KEY)
    private final String accessTokenSecret;
    @Json(name=PROFILE_DATA_KEY)
    private final Map<String, Object> profileInfo;

    @SuppressWarnings("unused") // Moshi uses this!
    private UserIdentity() {
        id=null;
        connection=null;
        provider=null;
        social=false;
        accessToken=null;
        accessTokenSecret=null;
        profileInfo=null;
    }

    @SuppressWarnings("unchecked")
    public UserIdentity(Map<String, Object> values) {
        final Object idValue = values.get(USER_ID_KEY);
        if (idValue != null) {
            this.id = idValue.toString();
        } else {
            this.id = null;
        }
        this.connection = (String) values.get(CONNECTION_KEY);
        this.provider = (String) values.get(PROVIDER_KEY);
        Object isSocialValue = values.get(IS_SOCIAL_KEY);
        if (isSocialValue != null && isSocialValue instanceof Boolean) {
            this.social = (Boolean) isSocialValue;
        } else {
            this.social = false;
        }
        this.accessToken = (String) values.get(ACCESS_TOKEN_KEY);
        this.accessTokenSecret = (String) values.get(ACCESS_TOKEN_SECRET_KEY);
        this.profileInfo = (Map<String, Object>) values.get(PROFILE_DATA_KEY);
        init();
    }
    Map<String,Object> toMap() {
        HashMap<String, Object> res=new HashMap<String,Object>();
        res.put(USER_ID_KEY,id);
        res.put(CONNECTION_KEY,connection);
        res.put(PROVIDER_KEY,provider);
        res.put(IS_SOCIAL_KEY,social);
        res.put(ACCESS_TOKEN_KEY,accessToken);
        res.put(ACCESS_TOKEN_SECRET_KEY,accessTokenSecret);
        res.put(PROFILE_DATA_KEY,profileInfo);
        return res;
    }
    public UserIdentity( String id,
                        String connection,
                        String provider,
                        boolean social,
                        String accessToken,
                        String accessTokenSecret,
                        Map<String, Object> profileInfo) {
        this.id = id;
        this.connection = connection;
        this.provider = provider;
        this.social = social;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.profileInfo = profileInfo;
        init();
    }
    private void init() {
//        checkArgument(id != null, "id must be non-null");
//        checkArgument(connection != null, "connection must be non-null");
//        checkArgument(provider != null, "provider must be non-null");
    }

    /**
     * User identifiuer supplied by the IdP.
     * @return identity identifier for the user.
     */
    public String getId() {
        return id;
    }

    /**
     * Name of the Auth0 connection that where the user was created.
     * @return connection name.
     */
    public String getConnection() {
        return connection;
    }

    /**
     * Name of the IdP used to identify the user.
     * @return name of the IdP
     */
    public String getProvider() {
        return provider;
    }

    /**
     * If the identity is from a social connection.
     * @return true of the identity connection is social, false otherwise
     */
    public boolean isSocial() {
        return social;
    }

    /**
     * IdP access token for the user identity
     * @return an access token
     */
    public String getAccessToken() {
        return accessToken;
    }

    /**
     * IdP access token secret for the user identity (Twitter only)
     * @return an access token secret
     */
    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    /**
     * Profile information returned by the IdP
     * @return a map with the user information
     */
    public Map<String, Object> getProfileInfo() {
        return profileInfo;
    }

    /**
     * User identity identifier with the format '{provider}|{user_id}'
     * @return
     */
    public String getUserIdentityId() {
        return String.format("%s|%s", this.provider, this.id);
    }

    protected UserIdentity(Parcel in) {
        id = in.readString();
        connection = in.readString();
        provider = in.readString();
        social = in.readByte() != 0x00;
        accessToken = in.readString();
        accessTokenSecret = in.readString();
        if (in.readByte() == 0x01) {
            profileInfo = (Map<String, Object>) in.readSerializable();
        } else {
            profileInfo = new HashMap<String, Object>();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(connection);
        dest.writeString(provider);
        dest.writeByte((byte) (social ? 0x01 : 0x00));
        dest.writeString(accessToken);
        dest.writeString(accessTokenSecret);
        if (profileInfo == null) {
            dest.writeByte((byte) 0x00);
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeSerializable(new HashMap<String, Object>(profileInfo));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserIdentity> CREATOR = new Parcelable.Creator<UserIdentity>() {
        @Override
        public UserIdentity createFromParcel(Parcel in) {
            return new UserIdentity(in);
        }

        @Override
        public UserIdentity[] newArray(int size) {
            return new UserIdentity[size];
        }
    };
}
