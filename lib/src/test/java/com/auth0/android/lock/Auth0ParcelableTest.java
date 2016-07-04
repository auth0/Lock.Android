package com.auth0.android.lock;

import android.os.Bundle;

import com.auth0.android.auth0.Auth0;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class Auth0ParcelableTest {

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String DOMAIN = "https://my-domain.auth0.com";
    private static final String CONFIG_DOMAIN = "https://my-cdn.auth0.com";

    private static final String AUTH0_KEY = "AUTH0_KEY";

    @Test
    public void testParcelable() throws Exception {
        Auth0 auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
        Bundle bundle = new Bundle();
        bundle.putParcelable(AUTH0_KEY, new Auth0Parcelable(auth0));

        Auth0Parcelable auth0Parcelable = bundle.getParcelable(AUTH0_KEY);
        Auth0 auth0bundle = auth0Parcelable.getAuth0();

        assertThat(auth0bundle.getClientId(), is(equalTo(CLIENT_ID)));
        assertThat(auth0bundle.getDomainUrl(), is(equalTo(DOMAIN)));
        assertThat(auth0bundle.getConfigurationUrl(), is(equalTo(CONFIG_DOMAIN)));
    }
}