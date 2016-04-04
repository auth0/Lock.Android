package com.auth0.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Class that holds the information from a Identity Provider like Facebook or Twitter.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdentity implements Parcelable {

    private static final String USER_ID_KEY = "user_id";
    private static final String CONNECTION_KEY = "connection";
    private static final String PROVIDER_KEY = "provider";
    private static final String IS_SOCIAL_KEY = "isSocial";
    private static final String ACCESS_TOKEN_KEY = "access_token";
    private static final String ACCESS_TOKEN_SECRET_KEY = "access_token_secret";
    private static final String PROFILE_DATA_KEY = "profileData";
    private final String id;
    private final String connection;
    private final String provider;
    private final boolean social;
    private final String accessToken;
    private final String accessTokenSecret;
    private final Map<String, Object> profileInfo;

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
    }

    @JsonCreator
    public UserIdentity(@JsonProperty(value = USER_ID_KEY) String id,
                        @JsonProperty(value = CONNECTION_KEY) String connection,
                        @JsonProperty(value = PROVIDER_KEY) String provider,
                        @JsonProperty(value = IS_SOCIAL_KEY, required = false) boolean social,
                        @JsonProperty(value = ACCESS_TOKEN_KEY, required = false) String accessToken,
                        @JsonProperty(value = ACCESS_TOKEN_SECRET_KEY, required = false) String accessTokenSecret,
                        @JsonProperty(value = PROFILE_DATA_KEY, required = false) Map<String, Object> profileInfo) {
        this.id = id;
        this.connection = connection;
        this.provider = provider;
        this.social = social;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.profileInfo = profileInfo;
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
