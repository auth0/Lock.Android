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
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.auth0.android.lock.views.ValidatedInputView;
import com.auth0.android.lock.views.ValidatedInputView.DataType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CustomField implements Parcelable {

    public static final int TYPE_NAME = 0;
    public static final int TYPE_DATE = 1;
    public static final int TYPE_EMAIL = 2;
    public static final int TYPE_PASSWORD = 3;
    public static final int TYPE_NUMBER = 4;
    public static final int TYPE_PHONE_NUMBER = 5;

    @IntDef({TYPE_NAME, TYPE_DATE, TYPE_EMAIL, TYPE_PASSWORD, TYPE_NUMBER, TYPE_PHONE_NUMBER})
    @Retention(RetentionPolicy.SOURCE)
    public @interface FieldType {
    }

    private final String hint;
    private final int type;

    public CustomField(@FieldType int type, @NonNull String hint) {
        if (hint.isEmpty()) {
            throw new IllegalArgumentException("The hint cannot be empty!");
        }
        this.hint = hint;
        this.type = type;

    }

    public void configureField(@NonNull ValidatedInputView field) {
        switch (type) {
            case TYPE_PASSWORD:
                field.setDataType(DataType.PASSWORD);
                break;
            case TYPE_NAME:
                field.setDataType(DataType.USERNAME);
                break;
            case TYPE_EMAIL:
                field.setDataType(DataType.EMAIL);
                break;
            case TYPE_DATE:
//                field.setDataType(ValidatedInputView.DataType.DATE);
                break;
            case TYPE_NUMBER:
                field.setDataType(DataType.NUMBER);
                break;
            case TYPE_PHONE_NUMBER:
                field.setDataType(DataType.PHONE_NUMBER);
                break;
        }
        field.setHint(hint);
    }

    protected CustomField(Parcel in) {
        hint = in.readString();
        type = in.readInt();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CustomField that = (CustomField) o;

        if (type != that.type) return false;
        return hint.equals(that.hint);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hint);
        dest.writeInt(type);
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
