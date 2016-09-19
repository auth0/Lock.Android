package com.auth0.android.lock.events;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class SocialConnectionEventTest {

    private SocialConnectionEvent event;

    @Before
    public void setUp() throws Exception {
        event = new SocialConnectionEvent("strategy", "connection");
    }

    @Test
    public void shouldGetStrategyName() throws Exception {
        assertThat(event.getStrategyName(), is("strategy"));
    }

    @Test
    public void shouldGetConnectionName() throws Exception {
        assertThat(event.getConnectionName(), is("connection"));
    }
}