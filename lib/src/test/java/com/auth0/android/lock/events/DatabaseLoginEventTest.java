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
@Config(sdk = 21)
public class DatabaseLoginEventTest {

    private DatabaseLoginEvent event;

    @Before
    public void setUp() {
        event = new DatabaseLoginEvent("username", "password");
    }

    @Test
    public void shouldGetUsername() {
        assertThat(event.getUsernameOrEmail(), is("username"));
    }

    @Test
    public void shouldGetPassword() {
        assertThat(event.getPassword(), is("password"));
    }

    @Test
    public void shouldNotHaveVerificationCode() {
        assertThat(event.getVerificationCode(), is(nullValue()));
    }

    @Test
    public void shouldSetVerificationCode() {
        event.setVerificationCode("code");
        assertThat(event.getVerificationCode(), is("code"));
    }

    @Test
    public void shouldNotHaveMFAToken() {
        assertThat(event.getMFAToken(), is(nullValue()));
    }

    @Test
    public void shouldSetMFAToken() {
        event.setMFAToken("mfatoken");
        assertThat(event.getMFAToken(), is("mfatoken"));
    }
}