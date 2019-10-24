package com.auth0.android.lock.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

public class HiddenField extends SignUpField {

    private final String value;

    /**
     * Constructor for HiddenField instance
     *
     * @param key     the key to store this field as.
     * @param value   the fixed value to set for this field
     * @param storage the location to store this value into.
     */
    public HiddenField(@NonNull String key, @NonNull String value, @CustomField.Storage int storage) {
        super(key, storage);
        this.value = value;
    }

    @NonNull
    public String getValue() {
        return value;
    }

    protected HiddenField(Parcel in) {
        super(in);
        value = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(value);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<HiddenField> CREATOR = new Parcelable.Creator<HiddenField>() {
        @Override
        public HiddenField createFromParcel(Parcel in) {
            return new HiddenField(in);
        }

        @Override
        public HiddenField[] newArray(int size) {
            return new HiddenField[size];
        }
    };
}
