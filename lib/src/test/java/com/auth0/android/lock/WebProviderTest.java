package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;

import com.auth0.android.Auth0;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class WebProviderTest {
    @Test
    public void shouldInit() throws Exception {
        Auth0 account = mock(Auth0.class);
        WebProvider webProvider = new WebProvider(account);
        assertThat(webProvider.init(), is(notNullValue()));
    }

    @Test
    public void shouldResumeWithIntent() throws Exception {
        Intent intent = mock(Intent.class);
        Auth0 account = mock(Auth0.class);
        WebProvider webProvider = new WebProvider(account);
        assertThat(webProvider.resume(intent), is(false));
    }

    @Test
    public void shouldResumeWithCodesAndIntent() throws Exception {
        Intent intent = mock(Intent.class);
        Auth0 account = mock(Auth0.class);
        WebProvider webProvider = new WebProvider(account);
        assertThat(webProvider.resume(1, Activity.RESULT_CANCELED, intent), is(false));
    }

}