package com.auth0.android.lock.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class DatabaseEventTest {

    @Test
    public void shouldSetEmailIdentity() throws Exception {
        final DatabaseEvent event = new DatabaseEvent("email@me.com");
        assertThat(event.getEmail(), is("email@me.com"));
        assertThat(event.getUsername(), is(nullValue()));
    }

    @Test
    public void shouldSetUsernameIdentity() throws Exception {
        final DatabaseEvent event = new DatabaseEvent("this_is_me");
        assertThat(event.getEmail(), is(nullValue()));
        assertThat(event.getUsername(), is("this_is_me"));
    }

    @Test
    public void shouldSetUsername() throws Exception {
        final DatabaseEvent event = new DatabaseEvent("email@me.com");
        event.setUsername("this_is_me");
        assertThat(event.getUsername(), is("this_is_me"));
    }

    @Test
    public void shouldSetBothEmailAndUsername() throws Exception {
        final DatabaseEvent event = new DatabaseEvent("email@me.com", "this_is_me");
        assertThat(event.getEmail(), is("email@me.com"));
        assertThat(event.getUsername(), is("this_is_me"));
    }

}