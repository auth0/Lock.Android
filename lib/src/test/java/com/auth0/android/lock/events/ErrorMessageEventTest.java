package com.auth0.android.lock.events;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ErrorMessageEventTest {

    @Test
    public void shouldSetMessageResource() throws Exception {
        //noinspection ResourceType
        final LockMessageEvent event = new LockMessageEvent(23);
        assertThat(event.getMessageRes(), is(23));
    }
}