package com.auth0.android.lock.events;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

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
    public void shouldNotHaveOTP() {
        assertThat(event.getMultifactorOTP(), is(nullValue()));
    }

    @Test
    public void shouldSetOTP() {
        event.setMultifactorOTP("otp");
        assertThat(event.getMultifactorOTP(), is("otp"));
    }

    @Test
    public void shouldNotHaveOOBCode() {
        assertThat(event.getMultifactorOOBCode(), is(nullValue()));
    }

    @Test
    public void shouldSetOOBCode() {
        event.setMultifactorOTP("oob");
        assertThat(event.getMultifactorOOBCode(), is("oob"));
    }

    @Test
    public void shouldNotHaveMFAToken() {
        assertThat(event.getMultifactorToken(), is(nullValue()));
    }

    @Test
    public void shouldSetMFAToken() {
        event.setMultifactorToken("mfa-challenge");
        assertThat(event.getMultifactorToken(), is("mfa-challenge"));
    }

    @Test
    public void shouldNotHaveMFAChallengeType() {
        assertThat(event.getMultifactorChallengeType(), is(nullValue()));
    }

    @Test
    public void shouldSetMFAChallengeType() {
        event.setMultifactorChallengeType("mfa-challenge");
        assertThat(event.getMultifactorChallengeType(), is("mfa-challenge"));
    }
}