package com.auth0.android.lock;

import android.os.Parcel;

import com.auth0.android.Auth0;
import com.auth0.android.util.Auth0UserAgent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import okhttp3.HttpUrl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class Auth0ParcelableTest {

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String DOMAIN = "https://my-domain.auth0.com";
    private static final String CONFIG_DOMAIN = "https://my-cdn.auth0.com";

    @Test
    public void shouldSaveClientId() {
        Auth0 auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
        Auth0Parcelable auth0Parcelable = new Auth0Parcelable(auth0);
        Parcel parcel = Parcel.obtain();
        auth0Parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Auth0Parcelable parceledAuth0 = Auth0Parcelable.CREATOR.createFromParcel(parcel);
        assertThat(auth0.getClientId(), is(equalTo(CLIENT_ID)));
        assertThat(parceledAuth0.getAuth0().getClientId(), is(equalTo(CLIENT_ID)));
    }

    @Test
    public void shouldSaveDomainUrl() {
        Auth0 auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
        Auth0Parcelable auth0Parcelable = new Auth0Parcelable(auth0);
        Parcel parcel = Parcel.obtain();
        auth0Parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Auth0Parcelable parceledAuth0 = Auth0Parcelable.CREATOR.createFromParcel(parcel);
        assertThat(HttpUrl.parse(auth0.getDomainUrl()), is(equalTo(HttpUrl.parse(DOMAIN))));
        assertThat(HttpUrl.parse(parceledAuth0.getAuth0().getDomainUrl()), is(equalTo(HttpUrl.parse(DOMAIN))));
    }

    @Test
    public void shouldSaveConfigurationUrl() {
        Auth0 auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
        Auth0Parcelable auth0Parcelable = new Auth0Parcelable(auth0);
        Parcel parcel = Parcel.obtain();
        auth0Parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Auth0Parcelable parceledAuth0 = Auth0Parcelable.CREATOR.createFromParcel(parcel);
        assertThat(HttpUrl.parse(auth0.getConfigurationUrl()), is(equalTo(HttpUrl.parse(CONFIG_DOMAIN))));
        assertThat(HttpUrl.parse(parceledAuth0.getAuth0().getConfigurationUrl()), is(equalTo(HttpUrl.parse(CONFIG_DOMAIN))));
    }

    @Test
    public void shouldSaveUserAgent() {
        Auth0UserAgent userAgent = new Auth0UserAgent("name", "version", "libraryVersion");
        Auth0 auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
        auth0.setAuth0UserAgent(userAgent);
        Auth0Parcelable auth0Parcelable = new Auth0Parcelable(auth0);
        Parcel parcel = Parcel.obtain();
        auth0Parcelable.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Auth0Parcelable parceledAuth0 = Auth0Parcelable.CREATOR.createFromParcel(parcel);
        assertThat(userAgent.getValue(), is(notNullValue()));
        assertThat(auth0.getAuth0UserAgent().getValue(), is(equalTo(userAgent.getValue())));
        assertThat(parceledAuth0.getAuth0().getAuth0UserAgent().getValue(), is(equalTo(userAgent.getValue())));
    }

}