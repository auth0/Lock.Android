package com.auth0.android.lock;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by lbalmaceda on 1/21/16.
 */
public class LockOptions implements Parcelable {
    public Auth0 account;
    public boolean useBrowser;
    public boolean closable;
    public boolean fullscreen;
    public boolean sendSDKInfo = true;
    public boolean useEmail = false;
    public boolean signUpEnabled = true;
    public boolean changePasswordEnabled = true;
    public String defaultDatabaseConnection;
    public List<String> connections;
    public List<String> enterpriseConnectionsUsingWebForm;
    public HashMap<String, Object> authenticationParameters;

    public LockOptions() {
    }

    protected LockOptions(Parcel in) {
        account = (Auth0) in.readValue(Auth0.class.getClassLoader());
        useBrowser = in.readByte() != 0x00;
        closable = in.readByte() != 0x00;
        fullscreen = in.readByte() != 0x00;
        sendSDKInfo = in.readByte() != 0x00;
        useEmail = in.readByte() != 0x00;
        signUpEnabled = in.readByte() != 0x00;
        changePasswordEnabled = in.readByte() != 0x00;
        defaultDatabaseConnection = in.readString();
        if (in.readByte() == 0x01) {
            connections = new ArrayList<String>();
            in.readList(connections, String.class.getClassLoader());
        } else {
            connections = null;
        }
        if (in.readByte() == 0x01) {
            enterpriseConnectionsUsingWebForm = new ArrayList<String>();
            in.readList(enterpriseConnectionsUsingWebForm, String.class.getClassLoader());
        } else {
            enterpriseConnectionsUsingWebForm = null;
        }
        if (in.readByte() == 0x01) {
            // FIXME this is something to improve
            Bundle mapBundle = in.readBundle();
            authenticationParameters = (HashMap<String, Object>) mapBundle.getSerializable("authenticationParameters");
        } else {
            authenticationParameters = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(account);
        dest.writeByte((byte) (useBrowser ? 0x01 : 0x00));
        dest.writeByte((byte) (closable ? 0x01 : 0x00));
        dest.writeByte((byte) (fullscreen ? 0x01 : 0x00));
        dest.writeByte((byte) (sendSDKInfo ? 0x01 : 0x00));
        dest.writeByte((byte) (useEmail ? 0x01 : 0x00));
        dest.writeByte((byte) (signUpEnabled ? 0x01 : 0x00));
        dest.writeByte((byte) (changePasswordEnabled ? 0x01 : 0x00));
        dest.writeString(defaultDatabaseConnection);
        if (connections == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(connections);
        }
        if (enterpriseConnectionsUsingWebForm == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(enterpriseConnectionsUsingWebForm);
        }
        if (authenticationParameters == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            // FIXME this is something to improve
            Bundle mapBundle = new Bundle();
            mapBundle.putSerializable("authenticationParameters", authenticationParameters);
            dest.writeBundle(mapBundle);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<LockOptions> CREATOR = new Parcelable.Creator<LockOptions>() {
        @Override
        public LockOptions createFromParcel(Parcel in) {
            return new LockOptions(in);
        }

        @Override
        public LockOptions[] newArray(int size) {
            return new LockOptions[size];
        }
    };
}