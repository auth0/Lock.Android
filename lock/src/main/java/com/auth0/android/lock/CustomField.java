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

package com.auth0.android.lock;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.lock.views.ValidatedInputView;
import com.auth0.android.lock.views.ValidatedInputView.DataType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.CustomField.FieldType.TYPE_DATE;
import static com.auth0.android.lock.CustomField.FieldType.TYPE_EMAIL;
import static com.auth0.android.lock.CustomField.FieldType.TYPE_NUMBER;
import static com.auth0.android.lock.CustomField.FieldType.TYPE_PHONE_NUMBER;
import static com.auth0.android.lock.CustomField.FieldType.TYPE_TEXT_NAME;

public class CustomField implements Parcelable {

    @IntDef({TYPE_TEXT_NAME, TYPE_NUMBER, TYPE_PHONE_NUMBER, TYPE_DATE, TYPE_EMAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FieldType {
        int TYPE_TEXT_NAME = 0;
        int TYPE_NUMBER = 1;
        int TYPE_PHONE_NUMBER = 2;
        int TYPE_DATE = 3;
        int TYPE_EMAIL = 4;
    }

    @DrawableRes
    private int icon;
    @FieldType
    private final int type;
    private final String key;
    private final String hint;

    public CustomField(@DrawableRes int icon, @FieldType int type, @NonNull String key, @NonNull String hint) {
        if (hint.isEmpty()) {
            throw new IllegalArgumentException("The hint cannot be empty!");
        }
        if (key.isEmpty()) {
            throw new IllegalArgumentException("The key cannot be empty!");
        }
        this.icon = icon;
        this.type = type;
        this.key = key;
        this.hint = hint;
    }

    public void configureField(@NonNull ValidatedInputView field) {
        switch (type) {
            case TYPE_TEXT_NAME:
                field.setDataType(DataType.USERNAME);
                break;
            case TYPE_NUMBER:
                field.setDataType(DataType.NUMBER);
                break;
            case TYPE_PHONE_NUMBER:
                field.setDataType(DataType.PHONE_NUMBER);
                break;
            case TYPE_DATE:
                field.setDataType(DataType.DATE);
                break;
            case TYPE_EMAIL:
                field.setDataType(DataType.EMAIL);
                break;
        }
        field.setHint(hint);
        field.setIcon(icon);
        field.setTag(key);
    }

    @Nullable
    public String findValue(@NonNull ViewGroup container) {
        String value = null;
        View view = container.findViewWithTag(key);
        if (view != null) {
            value = ((ValidatedInputView) view).getText();
        }
        return value;
    }

    public String getKey() {
        return key;
    }

    String getHint() {
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

    protected CustomField(Parcel in) {
        icon = in.readInt();
        //noinspection WrongConstant
        type = in.readInt();
        key = in.readString();
        hint = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(icon);
        dest.writeInt(type);
        dest.writeString(key);
        dest.writeString(hint);
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
