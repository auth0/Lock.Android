package com.auth0.android.lock.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import static com.auth0.android.lock.utils.CustomField.Storage.PROFILE_ROOT;

public abstract class SignUpField implements Parcelable {

    private final String key;
    private final int storage;

    SignUpField(@NonNull String key, @CustomField.Storage int storage) {
        if (key.isEmpty()) {
            throw new IllegalArgumentException("The key cannot be empty.");
        }
        if (key.equalsIgnoreCase("user_metadata") && storage == PROFILE_ROOT) {
            throw new IllegalArgumentException("Update the user_metadata root profile attributes by using Storage.USER_METADATA as storage location.");
        }
        this.key = key;
        this.storage = storage;
    }

    @CustomField.Storage
    public int getStorage() {
        return storage;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    SignUpField(Parcel in) {
        key = in.readString();
        storage = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    @CallSuper
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(key);
        dest.writeInt(storage);
    }
}
