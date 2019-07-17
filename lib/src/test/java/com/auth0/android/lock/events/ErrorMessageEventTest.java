package com.auth0.android.lock.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class ErrorMessageEventTest {

    @Test
    public void shouldSetMessageResource() {
        //noinspection ResourceType
        final LockMessageEvent event = new LockMessageEvent(23);
        assertThat(event.getMessageRes(), is(23));
    }
}