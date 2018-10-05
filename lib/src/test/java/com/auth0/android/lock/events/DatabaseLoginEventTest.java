package com.auth0.android.lock.events;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class DatabaseLoginEventTest {

    private DatabaseLoginEvent event;

    @Before
    public void setUp() throws Exception {
        event = new DatabaseLoginEvent("username", "password");
    }

    @Test
    public void shouldGetUsername() throws Exception {
        assertThat(event.getUsernameOrEmail(), is("username"));
    }

    @Test
    public void shouldGetPassword() throws Exception {
        assertThat(event.getPassword(), is("password"));
    }

    @Test
    public void shouldNotHaveVerificationCode() throws Exception {
        assertThat(event.getVerificationCode(), is(nullValue()));
    }

    @Test
    public void shouldSetVerificationCode() throws Exception {
        event.setVerificationCode("code");
        assertThat(event.getVerificationCode(), is("code"));
    }

    @Test
    public void shouldNotHaveMFAToken() throws Exception {
        assertThat(event.getMFAToken(), is(nullValue()));
    }

    @Test
    public void shouldSetMFAToken() throws Exception {
        event.setMFAToken("mfatoken");
        assertThat(event.getMFAToken(), is("mfatoken"));
    }
}