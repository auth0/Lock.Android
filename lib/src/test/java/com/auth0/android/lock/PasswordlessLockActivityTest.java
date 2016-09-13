package com.auth0.android.lock;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.internal.Configuration;
import com.auth0.android.lock.internal.Options;
import com.auth0.android.lock.internal.json.Connection;
import com.auth0.android.lock.views.PasswordlessLockView;
import com.auth0.android.request.ParameterizableRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class PasswordlessLockActivityTest {

    @Mock
    Options options;
    @Mock
    Configuration configuration;
    @Mock
    PasswordlessLockView lockView;
    private PasswordlessLockActivity activity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldFailPasswordlessAuthenticationOnNullConnection() throws Exception {
        final PasswordlessLockView lockView = mock(PasswordlessLockView.class);
        activity = new PasswordlessLockActivity(configuration, options, lockView, "email");
        when(configuration.getPasswordlessConnection()).thenReturn(null);
        final PasswordlessLoginEvent event = mock(PasswordlessLoginEvent.class);
        activity.onPasswordlessAuthenticationRequest(event);
        verify(configuration, VerificationModeFactory.times(1)).getPasswordlessConnection();
    }

    @Test
    public void shouldCallPasswordlessRequestCodeOnValidConnection() throws Exception {
        when(options.getAccount()).thenReturn(new Auth0("cliendId", "domain"));
        activity = new PasswordlessLockActivity(configuration, options, lockView, null);

        Connection connection = mock(Connection.class);
        when(connection.getName()).thenReturn("connection");

        ParameterizableRequest request = mock(ParameterizableRequest.class);
        when(configuration.getPasswordlessConnection()).thenReturn(connection);

        final PasswordlessLoginEvent event = mock(PasswordlessLoginEvent.class);
        when(event.getCode()).thenReturn(null);
        ArgumentCaptor<AuthenticationAPIClient> clientCaptor = ArgumentCaptor.forClass(AuthenticationAPIClient.class);
        when(event.getCodeRequest(clientCaptor.capture(), eq("connection"))).thenReturn(request);

        activity.onPasswordlessAuthenticationRequest(event);
        verify(lockView).showProgress(true);
        verify(request).start(any(com.auth0.android.callback.AuthenticationCallback.class));
        verify(configuration, VerificationModeFactory.times(2)).getPasswordlessConnection();
    }

    @Test
    public void shouldCallPasswordlessAuthenticationOnValidConnection() throws Exception {
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("key", "value");
        when(options.getAccount()).thenReturn(new Auth0("cliendId", "domain"));
        when(options.getAuthenticationParameters()).thenReturn(parameters);
        activity = new PasswordlessLockActivity(configuration, options, lockView, "email");

        Connection connection = mock(Connection.class);
        when(connection.getName()).thenReturn("connection");

        when(configuration.getPasswordlessConnection()).thenReturn(connection);

        MockAuthenticationRequest request = new MockAuthenticationRequest();
        final PasswordlessLoginEvent event = mock(PasswordlessLoginEvent.class);
        when(event.getCode()).thenReturn("123456");
        when(event.getLoginRequest(any(AuthenticationAPIClient.class), eq("email"))).thenReturn(request);


        activity.onPasswordlessAuthenticationRequest(event);
        verify(lockView).showProgress(true);
        Assert.assertThat(request.parameters, is(notNullValue()));
        Assert.assertTrue(request.parameters.containsKey("key"));
        Assert.assertTrue(request.parameters.containsValue("value"));
        Assert.assertTrue(request.started);
        Assert.assertThat(request.connection, is("connection"));
        verify(configuration, VerificationModeFactory.times(2)).getPasswordlessConnection();
    }

}