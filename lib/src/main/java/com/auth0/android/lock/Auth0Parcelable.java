/*
 * Auth0Parcelable.java
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

import androidx.annotation.NonNull;

import com.auth0.android.Auth0;
import com.auth0.android.util.Auth0UserAgent;


/**
 * This class wraps a {@link Auth0} to make it Parcelable
 */
public class Auth0Parcelable implements Parcelable {

    private static final double WITHOUT_DATA = 0x00;
    private static final double WITH_DATA = 0x01;
    private final Auth0 auth0;

    public Auth0Parcelable(@NonNull Auth0 auth0) {
        this.auth0 = auth0;
    }

    @NonNull
    public Auth0 getAuth0() {
        return auth0;
    }

    // PARCELABLE
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(auth0.getClientId());
        dest.writeString(auth0.getDomainUrl());
        dest.writeString(auth0.getConfigurationUrl());
        //FIXME: Find a way to pass the NetworkingClient implementation
        dest.writeString(auth0.getAuth0UserAgent().getName());
        dest.writeString(auth0.getAuth0UserAgent().getVersion());
        dest.writeString(auth0.getAuth0UserAgent().getLibraryVersion());
    }

    public static final Parcelable.Creator<Auth0Parcelable> CREATOR
            = new Parcelable.Creator<Auth0Parcelable>() {
        public Auth0Parcelable createFromParcel(Parcel in) {
            return new Auth0Parcelable(in);
        }

        public Auth0Parcelable[] newArray(int size) {
            return new Auth0Parcelable[size];
        }
    };

    @SuppressWarnings("ConstantConditions")
    private Auth0Parcelable(@NonNull Parcel in) {
        String clientId = in.readString();
        String domain = in.readString();
        String configurationDomain = in.readString();
        String telemetryName = in.readString();
        String telemetryVersion = in.readString();
        String telemetryLibraryVersion = in.readString();

        auth0 = new Auth0(clientId, domain, configurationDomain);
        Auth0UserAgent userAgent = new Auth0UserAgent(telemetryName, telemetryVersion, telemetryLibraryVersion);
        auth0.setAuth0UserAgent(userAgent);
    }
}
