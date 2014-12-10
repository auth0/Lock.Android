package com.auth0.core;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by hernan on 12/2/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserIdentity implements Parcelable {

    private String id;
    private String connection;
    private String provider;
    private boolean social;
    private String accessToken;
    private String accessTokenSecret;
    private Map<String, Object> profileInfo;

    @JsonCreator
    public UserIdentity(@JsonProperty(value = "user_id") String id,
                        @JsonProperty(value = "connection") String connection,
                        @JsonProperty(value = "provider") String provider,
                        @JsonProperty(value = "isSocial") boolean social,
                        @JsonProperty(value = "access_token") String accessToken,
                        @JsonProperty(value = "access_token_secret") String accessTokenSecret,
                        @JsonProperty(value = "profileData") Map<String, Object> profileInfo) {
        this.id = id;
        this.connection = connection;
        this.provider = provider;
        this.social = social;
        this.accessToken = accessToken;
        this.accessTokenSecret = accessTokenSecret;
        this.profileInfo = profileInfo;
    }

    public String getId() {
        return id;
    }

    public String getConnection() {
        return connection;
    }

    public String getProvider() {
        return provider;
    }

    public boolean isSocial() {
        return social;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getAccessTokenSecret() {
        return accessTokenSecret;
    }

    public Map<String, Object> getProfileInfo() {
        return profileInfo;
    }

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
