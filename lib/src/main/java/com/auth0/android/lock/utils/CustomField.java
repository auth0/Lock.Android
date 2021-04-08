/*
 * CustomField.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.utils;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.DrawableRes;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.lock.views.ValidatedInputView;
import com.auth0.android.lock.views.ValidatedInputView.DataType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.utils.CustomField.FieldType.TYPE_EMAIL;
import static com.auth0.android.lock.utils.CustomField.FieldType.TYPE_NAME;
import static com.auth0.android.lock.utils.CustomField.FieldType.TYPE_NUMBER;
import static com.auth0.android.lock.utils.CustomField.FieldType.TYPE_PHONE_NUMBER;
import static com.auth0.android.lock.utils.CustomField.Storage.PROFILE_ROOT;
import static com.auth0.android.lock.utils.CustomField.Storage.USER_METADATA;

public class CustomField extends SignUpField {

    @IntDef({TYPE_NAME, TYPE_NUMBER, TYPE_PHONE_NUMBER, TYPE_EMAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FieldType {
        int TYPE_NAME = 0;
        int TYPE_NUMBER = 1;
        int TYPE_PHONE_NUMBER = 2;
        int TYPE_EMAIL = 3;
    }

    /**
     * Location to store the field into.
     */
    @IntDef({PROFILE_ROOT, USER_METADATA})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Storage {
        /**
         * Store the field into the user profile root.
         * The list of attributes that can be added to your root profile is here https://auth0.com/docs/api/authentication#signup.
         */
        int PROFILE_ROOT = 0;

        /**
         * Store the field into the user_metadata object.
         */
        int USER_METADATA = 1;
    }

    @DrawableRes
    private final int icon;
    @FieldType
    private final int type;
    @StringRes
    private final int hint;

    /**
     * Constructor for CustomField instance
     * When this constructor is used the field will be stored under the {@link Storage#USER_METADATA} attribute.
     * If you want to change the storage location use the constructor that accepts a {@link Storage} value.
     *
     * @param icon the icon resource to display next to the field.
     * @param type the type of input this field will receive. Used to determine the applied validation.
     * @param key  the key to store this field as.
     * @param hint the placeholder to display when this field is empty.
     */
    public CustomField(@DrawableRes int icon, @FieldType int type, @NonNull String key, @StringRes int hint) {
        this(icon, type, key, hint, USER_METADATA);
    }

    /**
     * Constructor for CustomField instance
     *
     * @param icon    the icon resource to display next to the field.
     * @param type    the type of input this field will receive. Used to determine the applied validation.
     * @param key     the key to store this field as.
     * @param hint    the placeholder to display when this field is empty.
     * @param storage the location to store this value into.
     */
    public CustomField(@DrawableRes int icon, @FieldType int type, @NonNull String key, @StringRes int hint, @Storage int storage) {
        super(key, storage);
        this.icon = icon;
        this.type = type;
        this.hint = hint;
    }

    public void configureField(@NonNull ValidatedInputView field) {
        switch (type) {
            case TYPE_NAME:
                field.setDataType(DataType.TEXT_NAME);
                break;
            case TYPE_NUMBER:
                field.setDataType(DataType.NUMBER);
                break;
            case TYPE_PHONE_NUMBER:
                field.setDataType(DataType.PHONE_NUMBER);
                break;
            case TYPE_EMAIL:
                field.setDataType(DataType.EMAIL);
                break;
        }
        field.setHint(hint);
        field.setIcon(icon);
        field.setTag(getKey());
    }

    @Nullable
    public String findValue(@NonNull ViewGroup container) {
        String value = null;
        View view = container.findViewWithTag(getKey());
        if (view != null) {
            value = ((ValidatedInputView) view).getText();
        }
        return value;
    }

    @StringRes
    int getHint() {
        return hint;
    }

    @DrawableRes
    int getIcon() {
        return icon;
    }

    @FieldType
    int getType() {
        return type;
    }

    protected CustomField(@NonNull Parcel in) {
        super(in);
        icon = in.readInt();
        type = in.readInt();
        hint = in.readInt();
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(icon);
        dest.writeInt(type);
        dest.writeInt(hint);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CustomField> CREATOR = new Parcelable.Creator<CustomField>() {
        @Override
        public CustomField createFromParcel(Parcel in) {
            return new CustomField(in);
        }

        @Override
        public CustomField[] newArray(int size) {
            return new CustomField[size];
        }
    };

}
