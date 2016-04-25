/*
 * ParcelableUtils.java
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

import java.util.HashMap;
import java.util.Map;

public class ParcelableUtils {

    public static <K extends String, V extends Parcelable> void writeStringParcelableMap(
            Parcel parcel, int flags, Map<K, V> map) {
        parcel.writeInt(map.size());
        for (Map.Entry<K, V> e : map.entrySet()) {
            parcel.writeString(e.getKey());
            parcel.writeParcelable(e.getValue(), flags);
        }
    }

    public static <K extends String, V extends Parcelable> Map<K, V> readStringParcelableMap(
            Parcel parcel, Class<K> kClass, Class<V> vClass) {
        int size = parcel.readInt();
        Map<K, V> map = new HashMap<K, V>(size);
        for (int i = 0; i < size; i++) {
            map.put(kClass.cast(parcel.readString()),
                    vClass.cast(parcel.readParcelable(vClass.getClassLoader())));
        }
        return map;
    }

    public static <K extends Parcelable, V extends Parcelable> void writeParcelableMap(
            Parcel parcel, int flags, Map<K, V> map) {
        parcel.writeInt(map.size());
        for (Map.Entry<K, V> e : map.entrySet()) {
            parcel.writeParcelable(e.getKey(), flags);
            parcel.writeParcelable(e.getValue(), flags);
        }
    }

    public static <K extends Parcelable, V extends Parcelable> Map<K, V> readParcelableMap(
            Parcel parcel, Class<K> kClass, Class<V> vClass) {
        int size = parcel.readInt();
        Map<K, V> map = new HashMap<K, V>(size);
        for (int i = 0; i < size; i++) {
            map.put(kClass.cast(parcel.readParcelable(kClass.getClassLoader())),
                    vClass.cast(parcel.readParcelable(vClass.getClassLoader())));
        }
        return map;
    }
}
