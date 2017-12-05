package com.auth0.android.lock.events;

<<<<<<< Updated upstream
import com.auth0.android.lock.internal.configuration.OAuthConnection;
=======
import com.auth0.android.lock.views.next.configuration.internal.OAuthConnection;
>>>>>>> Stashed changes

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class OAuthLoginEventTest {

    private OAuthConnection connection;

    @Before
    public void setUp() throws Exception {
        connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("connectionName");
        when(connection.getStrategy()).thenReturn("strategyName");
    }

    @Test
    public void shouldGetStrategyName() throws Exception {
        OAuthLoginEvent roEvent = new OAuthLoginEvent(connection, "username", "password");
        assertThat(roEvent.getStrategy(), is("strategyName"));

        OAuthLoginEvent webEvent = new OAuthLoginEvent(connection);
        assertThat(webEvent.getStrategy(), is("strategyName"));
    }

    @Test
    public void shouldGetConnectionName() throws Exception {
        OAuthLoginEvent roEvent = new OAuthLoginEvent(connection, "username", "password");
        assertThat(roEvent.getConnection(), is("connectionName"));

        OAuthLoginEvent webEvent = new OAuthLoginEvent(connection);
        assertThat(webEvent.getConnection(), is("connectionName"));
    }

    @Test
    public void shouldUseActiveFlow() throws Exception {
        OAuthLoginEvent roEvent = new OAuthLoginEvent(connection, "username", "password");
        assertTrue(roEvent.useActiveFlow());
    }

    @Test
    public void shouldHaveUsernameOnActiveFlow() throws Exception {
        OAuthLoginEvent roEvent = new OAuthLoginEvent(connection, "username", "password");
        assertThat(roEvent.getUsername(), is("username"));
    }

    @Test
    public void shouldHavePasswordOnActiveFlow() throws Exception {
        OAuthLoginEvent roEvent = new OAuthLoginEvent(connection, "username", "password");
        assertThat(roEvent.getPassword(), is("password"));
    }

    @Test
    public void shouldUseWebAuth() throws Exception {
        OAuthLoginEvent webEvent = new OAuthLoginEvent(connection);
        assertFalse(webEvent.useActiveFlow());
    }

    @Test
    public void shouldNotHaveUsernameOnWebAuth() throws Exception {
        OAuthLoginEvent webEvent = new OAuthLoginEvent(connection);
        assertThat(webEvent.getUsername(), is(nullValue()));
    }

    @Test
    public void shouldNotHavePasswordOnWebAuth() throws Exception {
        OAuthLoginEvent webEvent = new OAuthLoginEvent(connection);
        assertThat(webEvent.getPassword(), is(nullValue()));
    }
}