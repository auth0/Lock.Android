package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.PasswordlessType;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.events.PasswordlessLoginEvent;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.OAuthConnection;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.lock.internal.configuration.PasswordlessConnection;
import com.auth0.android.lock.internal.configuration.PasswordlessMode;
import com.auth0.android.lock.provider.AuthResolver;
import com.auth0.android.lock.views.PasswordlessLockView;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.AuthHandler;
import com.auth0.android.provider.AuthProvider;
import com.auth0.android.request.AuthenticationRequest;
import com.auth0.android.request.ParameterizableRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyMapOf;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class PasswordlessLockActivityTest {

    private static final int REQ_CODE_WEB_PROVIDER = 200;
    private static final int REQ_CODE_CUSTOM_PROVIDER = 201;
    private static final int REQ_CODE_PERMISSIONS = 202;

    @Mock
    Options options;
    @Mock
    AuthenticationAPIClient client;
    @Mock
    WebProvider webProvider;
    @Mock
    ParameterizableRequest codeRequest;
    @Mock
    AuthenticationRequest authRequest;
    @Mock
    Configuration configuration;
    @Mock
    PasswordlessLockView lockView;
    Map<String, String> connectionScope;
    PasswordlessLockActivity activity;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        HashMap basicParameters = new HashMap<>(Collections.singletonMap("extra", "value"));
        connectionScope = new HashMap<>(Collections.singletonMap("my-connection", "the connection scope"));
        when(options.getAccount()).thenReturn(new Auth0("cliendId", "domain"));
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getConnectionsScope()).thenReturn(connectionScope);

        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        when(client.passwordlessWithEmail(anyString(), any(PasswordlessType.class))).thenReturn(codeRequest);
        when(client.passwordlessWithSMS(anyString(), any(PasswordlessType.class))).thenReturn(codeRequest);
        when(client.loginWithEmail(anyString(), anyString())).thenReturn(authRequest);
        when(client.loginWithPhoneNumber(anyString(), anyString())).thenReturn(authRequest);
        when(codeRequest.addParameters(anyMapOf(String.class, Object.class))).thenReturn(codeRequest);
        when(codeRequest.addParameter(anyString(), any(Object.class))).thenReturn(codeRequest);
        when(authRequest.addAuthenticationParameters(anyMapOf(String.class, Object.class))).thenReturn(authRequest);
        when(authRequest.setConnection(anyString())).thenReturn(authRequest);

        PasswordlessConnection connection = mock(PasswordlessConnection.class);
        when(connection.getName()).thenReturn("connection");
        when(configuration.getPasswordlessConnection()).thenReturn(connection);

        activity = new PasswordlessLockActivity(configuration, options, lockView, webProvider, null);
    }

    @Test
    public void shouldFailPasswordlessCodeRequestWithEmailOnNullConnection() throws Exception {
        activity = new PasswordlessLockActivity(configuration, options, lockView, webProvider, "user@domain.com");

        when(configuration.getPasswordlessConnection()).thenReturn(null);
        PasswordlessLoginEvent event = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_CODE, "email@domain.com");
        activity.onPasswordlessAuthenticationRequest(event);

        verify(lockView, never()).showProgress(true);
        verify(options, never()).getAuthenticationAPIClient();
        verify(authRequest, never()).start(any(BaseCallback.class));
        verify(client, never()).passwordlessWithEmail(anyString(), eq(PasswordlessType.CODE));
        verify(configuration, atLeastOnce()).getPasswordlessConnection();
    }

    @Test
    public void shouldCallPasswordlessCodeRequestWithEmail() throws Exception {
        activity = new PasswordlessLockActivity(configuration, options, lockView, webProvider, "user@domain.com");

        PasswordlessLoginEvent event = PasswordlessLoginEvent.requestCode(PasswordlessMode.EMAIL_CODE, "email@domain.com");
        activity.onPasswordlessAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).passwordlessWithEmail(eq("email@domain.com"), eq(PasswordlessType.CODE));
        verify(codeRequest).addParameter(eq("connection"), eq("connection"));
        verify(codeRequest).start(any(BaseCallback.class));
        verify(configuration, atLeastOnce()).getPasswordlessConnection();
    }

    @Test
    public void shouldDoPasswordlessLoginWithEmail() throws Exception {
        activity = new PasswordlessLockActivity(configuration, options, lockView, webProvider, "user@domain.com");

        PasswordlessLoginEvent event = PasswordlessLoginEvent.submitCode(PasswordlessMode.EMAIL_CODE, "1234");
        activity.onPasswordlessAuthenticationRequest(event);

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).loginWithEmail(eq("user@domain.com"), eq("1234"));
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).setConnection(eq("connection"));
        verify(authRequest).setScope("openid user photos");
        verify(authRequest).start(any(BaseCallback.class));
        verify(configuration, atLeastOnce()).getPasswordlessConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldFailPasswordlessCodeRequestWithPhoneOnNullConnection() throws Exception {
        Country country = Mockito.mock(Country.class);
        when(configuration.getPasswordlessConnection()).thenReturn(null);
        PasswordlessLoginEvent event = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_CODE, "1234567890", country);
        activity.onPasswordlessAuthenticationRequest(event);

        verify(lockView, never()).showProgress(true);
        verify(options, never()).getAuthenticationAPIClient();
        verify(authRequest, never()).start(any(BaseCallback.class));
        verify(client, never()).passwordlessWithSMS(anyString(), eq(PasswordlessType.CODE));
        verify(configuration, atLeastOnce()).getPasswordlessConnection();
    }

    @Test
    public void shouldCallPasswordlessCodeRequestWithPhone() throws Exception {
        Country country = Mockito.mock(Country.class);
        when(country.getDialCode()).thenReturn("+54");
        PasswordlessLoginEvent event = PasswordlessLoginEvent.requestCode(PasswordlessMode.SMS_CODE, "1234567890", country);
        activity.onPasswordlessAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).passwordlessWithSMS(eq("+541234567890"), eq(PasswordlessType.CODE));
        verify(codeRequest).addParameter(eq("connection"), eq("connection"));
        verify(codeRequest).start(any(BaseCallback.class));
        verify(configuration, atLeastOnce()).getPasswordlessConnection();
    }

    @Test
    public void shouldDoPasswordlessLoginWithPhone() throws Exception {
        activity = new PasswordlessLockActivity(configuration, options, lockView, webProvider, "+541234567890");

        PasswordlessLoginEvent event = PasswordlessLoginEvent.submitCode(PasswordlessMode.SMS_CODE, "1234");
        activity.onPasswordlessAuthenticationRequest(event);

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).loginWithPhoneNumber(eq("+541234567890"), eq("1234"));
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).setConnection(eq("connection"));
        verify(authRequest).setScope("openid user photos");
        verify(authRequest).start(any(BaseCallback.class));
        verify(configuration, atLeastOnce()).getPasswordlessConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallOAuthAuthenticationWithCustomProvider() throws Exception {
        AuthProvider customProvider = mock(AuthProvider.class);
        AuthHandler handler = mock(AuthHandler.class);
        when(handler.providerFor(anyString(), eq("my-connection"))).thenReturn(customProvider);
        AuthResolver.setAuthHandlers(Collections.singletonList(handler));

        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        verify(lockView, never()).showProgress(true);
        verify(customProvider).setParameters(mapCaptor.capture());
        verify(customProvider).start(eq(activity), any(AuthCallback.class), eq(REQ_CODE_PERMISSIONS), eq(REQ_CODE_CUSTOM_PROVIDER));
        AuthResolver.setAuthHandlers(Collections.emptyList());

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
        assertThat(reqParams, hasEntry("scope", "openid user photos"));
        assertThat(reqParams, hasEntry("connection_scope", "the connection scope"));
        assertThat(reqParams, not(hasKey("audience")));
    }

    @Test
    public void shouldCallOAuthAuthenticationWithCustomProviderAndAudience() throws Exception {
        Auth0 account = new Auth0("cliendId", "domain");
        account.setOIDCConformant(true);
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getAudience()).thenReturn("aud");
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getConnectionsScope()).thenReturn(connectionScope);
        HashMap basicParameters = new HashMap<>(Collections.singletonMap("extra", "value"));
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        PasswordlessLockActivity activity = new PasswordlessLockActivity(configuration, options, lockView, webProvider, null);


        AuthProvider customProvider = mock(AuthProvider.class);
        AuthHandler handler = mock(AuthHandler.class);
        when(handler.providerFor(anyString(), eq("my-connection"))).thenReturn(customProvider);
        AuthResolver.setAuthHandlers(Collections.singletonList(handler));

        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);

        verify(lockView, never()).showProgress(true);
        verify(customProvider).setParameters(mapCaptor.capture());
        verify(customProvider).start(eq(activity), any(AuthCallback.class), eq(REQ_CODE_PERMISSIONS), eq(REQ_CODE_CUSTOM_PROVIDER));
        AuthResolver.setAuthHandlers(Collections.emptyList());

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
        assertThat(reqParams, hasEntry("scope", "openid user photos"));
        assertThat(reqParams, hasEntry("connection_scope", "the connection scope"));
        assertThat(reqParams, hasEntry("audience", "aud"));
    }

    @Test
    public void shouldCallOAuthAuthenticationWithWebProvider() throws Exception {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        when(options.useBrowser()).thenReturn(true);
        activity.onOAuthAuthenticationRequest(event);

        verify(lockView, never()).showProgress(eq(true));

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(webProvider).start(eq(activity), eq("my-connection"), mapCaptor.capture(), any(AuthCallback.class), eq(REQ_CODE_WEB_PROVIDER));

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithWebProviderOnActivityResult() throws Exception {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onActivityResult(REQ_CODE_WEB_PROVIDER, Activity.RESULT_OK, intent);

        verify(lockView).showProgress(false);
        verify(webProvider).resume(eq(REQ_CODE_WEB_PROVIDER), eq(Activity.RESULT_OK), eq(intent));
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithCustomProviderOnActivityResult() throws Exception {
        AuthProvider customProvider = mock(AuthProvider.class);
        AuthHandler handler = mock(AuthHandler.class);
        when(handler.providerFor(anyString(), eq("custom-connection"))).thenReturn(customProvider);
        AuthResolver.setAuthHandlers(Collections.singletonList(handler));

        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("custom-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onActivityResult(REQ_CODE_CUSTOM_PROVIDER, Activity.RESULT_OK, intent);

        verify(lockView).showProgress(false);
        verify(customProvider).authorize(eq(REQ_CODE_CUSTOM_PROVIDER), eq(Activity.RESULT_OK), eq(intent));
        AuthResolver.setAuthHandlers(Collections.emptyList());
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithWebProviderOnNewIntent() throws Exception {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onNewIntent(intent);

        verify(lockView).showProgress(false);
        verify(webProvider).resume(eq(intent));
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithCustomProviderOnNewIntent() throws Exception {
        AuthProvider customProvider = mock(AuthProvider.class);
        AuthHandler handler = mock(AuthHandler.class);
        when(handler.providerFor(anyString(), eq("custom-connection"))).thenReturn(customProvider);
        AuthResolver.setAuthHandlers(Collections.singletonList(handler));

        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("custom-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onNewIntent(intent);

        verify(lockView).showProgress(false);
        verify(customProvider).authorize(eq(intent));
        AuthResolver.setAuthHandlers(Collections.emptyList());
    }
}