package com.auth0.core;

import android.os.Parcel;
import android.os.Parcelable;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class that holds the information of a user's profile
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Parcelable {
    private static final String USER_ID_KEY = "user_id";
    private static final String NAME_KEY = "name";
    private static final String NICKNAME_KEY = "nickname";
    private static final String EMAIL_KEY = "email";
    private static final String PICTURE_URL_KEY = "picture";
    private static final String CREATED_AT_KEY = "created_at";
    private static final String IDENTITIES_KEY = "identities";

    private String id;
    private String name;
    private String nickname;
    private String email;
    private String pictureURL;
    private Date createdAt;
    private Map<String, Object> extraInfo;
    private List<UserIdentity> identities;

    @SuppressWarnings("unused") // Moshi uses this!
    public UserProfile() {

    }
    @SuppressWarnings("unchecked")
    public UserProfile(Map<String, Object> values) {
        checkArgument(values != null, "must supply non-null values");
        HashMap<String, Object> info = new HashMap<String, Object>(values);
        String id = (String) info.remove(USER_ID_KEY);
        checkArgument(id != null, "profile must have a user id");
        this.id = id;
        this.name = (String) info.remove(NAME_KEY);
        this.nickname = (String) info.remove(NICKNAME_KEY);
        this.email = (String) info.remove(EMAIL_KEY);
        this.pictureURL = (String) info.remove(PICTURE_URL_KEY);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            String created_at = (String) info.remove(CREATED_AT_KEY);
            this.createdAt = created_at != null ? sdf.parse(created_at) : null;
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid created_at value", e);
        }
        this.identities = buildIdentities((List<Map<String, Object>>) info.remove(IDENTITIES_KEY));
        this.extraInfo = info;
    }
    public Map<String,Object> toMap() {
//        throw new Exception("Not Implemented");
        return null;
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

    /**
     * Returns extra information of the profile.
     * @return
     */
    public Map<String, Object> getExtraInfo() {
        return new HashMap<>(extraInfo);
    }

    /**
     * List of the identities from a Identity Provider associated to the user.
     * @return
     */
    public List<UserIdentity> getIdentities() {
        return identities;
    }

    private List<UserIdentity> buildIdentities(List<Map<String, Object>> values) {
        if (values == null) {
            return Collections.emptyList();
        }
        List<UserIdentity> identities = new ArrayList<>(values.size());
        for (Map<String, Object> value: values) {
            identities.add(new UserIdentity(value));
        }
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
