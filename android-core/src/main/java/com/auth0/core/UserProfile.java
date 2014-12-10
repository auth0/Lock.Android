package com.auth0.core;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by hernan on 12/1/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Parcelable {

    private String id;
    private String name;
    private String nickname;
    private String email;
    private String pictureURL;
    private Date createdAt;
    private Map<String, Object> extraInfo;
    private List<UserIdentity> identities;

    @JsonCreator
    @SuppressWarnings("unchecked")
    public UserProfile(@NonNull Map<String, Object> values) {
        checkArgument(values != null, "must supply non-null values");
        HashMap<String, Object> info = new HashMap<String, Object>(values);
        String id = (String) info.remove("user_id");
        checkArgument(id != null, "profile must have a user id");
        this.id = id;
        this.name = (String) info.remove("name");
        this.nickname = (String) info.remove("nickname");
        this.email = (String) info.remove("email");
        this.pictureURL = (String) info.remove("picture");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            String created_at = (String) info.remove("created_at");
            this.createdAt = created_at != null ? sdf.parse(created_at) : null;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid created_at value", e);
        }
        this.identities = (List<UserIdentity>) info.remove("identities");
        this.extraInfo = info;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getPictureURL() {
        return pictureURL;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Map<String, Object> getExtraInfo() {
        return new HashMap<String, Object>(extraInfo);
    }

    public List<UserIdentity> getIdentities() {
        return identities;
    }

    @SuppressWarnings("unchecked")
    protected UserProfile(Parcel in) {
        id = in.readString();
        name = in.readString();
        nickname = in.readString();
        email = in.readString();
        pictureURL = in.readString();
        long tmpCreatedAt = in.readLong();
        createdAt = tmpCreatedAt != -1 ? new Date(tmpCreatedAt) : null;
        if (in.readByte() == 0x01) {
            identities = new ArrayList<UserIdentity>();
            in.readList(identities, UserIdentity.class.getClassLoader());
        } else {
            identities = null;
        }
        if (in.readByte() == 0x01) {
            extraInfo = (Map<String, Object>) in.readSerializable();
        } else {
            extraInfo = new HashMap<String, Object>();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(nickname);
        dest.writeString(email);
        dest.writeString(pictureURL);
        dest.writeLong(createdAt != null ? createdAt.getTime() : -1L);
        if (identities == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(identities);
        }
        if (extraInfo == null) {
            dest.writeByte((byte) 0x00);
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeSerializable(new HashMap<String, Object>(extraInfo));
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserProfile> CREATOR = new Parcelable.Creator<UserProfile>() {
        @Override
        public UserProfile createFromParcel(Parcel in) {
            return new UserProfile(in);
        }

        @Override
        public UserProfile[] newArray(int size) {
            return new UserProfile[size];
        }
    };
}
