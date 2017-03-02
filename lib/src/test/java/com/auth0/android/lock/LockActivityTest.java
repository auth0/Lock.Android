package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.request.DatabaseConnectionRequest;
import com.auth0.android.authentication.request.SignUpRequest;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.events.DatabaseChangePasswordEvent;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.DatabaseConnection;
import com.auth0.android.lock.internal.configuration.OAuthConnection;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.lock.provider.AuthResolver;
import com.auth0.android.lock.views.ClassicLockView;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.AuthHandler;
import com.auth0.android.provider.AuthProvider;
import com.auth0.android.request.AuthenticationRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import edu.emory.mathcs.backport.java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.hasEntry;
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
public class LockActivityTest {

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
    AuthenticationRequest authRequest;
    @Mock
    SignUpRequest signUpRequest;
    @Mock
    DatabaseConnectionRequest dbRequest;
    @Mock
    Configuration configuration;
    @Mock
    ClassicLockView lockView;
    @Captor
    ArgumentCaptor<Map> mapCaptor;
    LockActivity activity;
    HashMap basicParameters;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        basicParameters = new HashMap<>(Collections.singletonMap("extra", "value"));
        when(options.getAccount()).thenReturn(new Auth0("cliendId", "domain"));
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getAudience()).thenReturn("aud");
        when(options.getScope()).thenReturn("openid user photos");

        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        when(client.login(anyString(), anyString(), anyString())).thenReturn(authRequest);
        when(client.createUser(anyString(), anyString(), anyString())).thenReturn(dbRequest);
        when(client.createUser(anyString(), anyString(), anyString(), anyString())).thenReturn(dbRequest);
        when(client.signUp(anyString(), anyString(), anyString())).thenReturn(signUpRequest);
        when(client.signUp(anyString(), anyString(), anyString(), anyString())).thenReturn(signUpRequest);
        when(client.resetPassword(anyString(), anyString())).thenReturn(dbRequest);
        when(authRequest.addAuthenticationParameters(anyMapOf(String.class, Object.class))).thenReturn(authRequest);
        when(signUpRequest.addAuthenticationParameters(anyMapOf(String.class, Object.class))).thenReturn(signUpRequest);
        when(dbRequest.addParameters(anyMapOf(String.class, Object.class))).thenReturn(dbRequest);

        DatabaseConnection connection = mock(DatabaseConnection.class);
        when(connection.getName()).thenReturn("connection");
        when(configuration.getDatabaseConnection()).thenReturn(connection);

        activity = new LockActivity(configuration, options, lockView, webProvider);
    }

    @Test
    public void shouldFailDatabaseLoginOnNullConnection() throws Exception {
        when(configuration.getDatabaseConnection()).thenReturn(null);
        DatabaseLoginEvent event = new DatabaseLoginEvent("username", "password");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView, never()).showProgress(true);
        verify(options, never()).getAuthenticationAPIClient();
        verify(authRequest, never()).addAuthenticationParameters(anyMapOf(String.class, Object.class));
        verify(authRequest, never()).start(any(BaseCallback.class));
        verify(client, never()).login(anyString(), anyString(), anyString());
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallLegacyDatabaseLogin() throws Exception {
        DatabaseLoginEvent event = new DatabaseLoginEvent("username", "password");
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).login(eq("username"), eq("password"), eq("connection"));
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).start(any(BaseCallback.class));
        verify(authRequest).setScope("openid user photos");
        verify(authRequest, never()).setAudience("aud");
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallLegacyDatabaseLoginWithVerificationCode() throws Exception {
        DatabaseLoginEvent event = new DatabaseLoginEvent("username", "password");
        event.setVerificationCode("123456");
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).login(eq("username"), eq("password"), eq("connection"));
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).start(any(BaseCallback.class));
        verify(authRequest).setScope("openid user photos");
        verify(authRequest, never()).setAudience("aud");
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
        assertThat(reqParams, hasEntry("mfa_code", "123456"));
    }

    @Test
    public void shouldCallOIDCDatabaseLoginWithCustomAudience() throws Exception {
        Auth0 account = new Auth0("cliendId", "domain");
        account.setOIDCConformant(true);
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getAudience()).thenReturn("aud");
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        LockActivity activity = new LockActivity(configuration, options, lockView, webProvider);

        DatabaseLoginEvent event = new DatabaseLoginEvent("username", "password");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).login(eq("username"), eq("password"), eq("connection"));
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).setScope("openid user photos");
        verify(authRequest).setAudience("aud");
        verify(authRequest).start(any(BaseCallback.class));
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }


    @Test
    public void shouldFailDatabaseSignUpOnNullConnection() throws Exception {
        when(configuration.getDatabaseConnection()).thenReturn(null);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", "username");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView, never()).showProgress(true);
        verify(options, never()).getAuthenticationAPIClient();
        verify(dbRequest, never()).start(any(BaseCallback.class));
        verify(authRequest, never()).addAuthenticationParameters(anyMapOf(String.class, Object.class));
        verify(client, never()).login(anyString(), anyString(), anyString());
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallDatabaseSignUpWithUsername() throws Exception {
        when(configuration.loginAfterSignUp()).thenReturn(false);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", "username");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(dbRequest).start(any(BaseCallback.class));
        verify(client).createUser(eq("email@domain.com"), eq("password"), eq("username"), eq("connection"));
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallDatabaseSignUp() throws Exception {
        when(configuration.loginAfterSignUp()).thenReturn(false);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", null);
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(dbRequest).start(any(BaseCallback.class));
        verify(client).createUser(eq("email@domain.com"), eq("password"), eq("connection"));
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallOIDCDatabaseSignInWithCustomAudience() throws Exception {
        Auth0 account = new Auth0("cliendId", "domain");
        account.setOIDCConformant(true);
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getAudience()).thenReturn("aud");
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        LockActivity activity = new LockActivity(configuration, options, lockView, webProvider);

        when(configuration.loginAfterSignUp()).thenReturn(true);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", "username");
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(signUpRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(signUpRequest).start(any(BaseCallback.class));
        verify(signUpRequest).setScope("openid user photos");
        verify(signUpRequest).setAudience("aud");
        verify(client).signUp(eq("email@domain.com"), eq("password"), eq("username"), eq("connection"));
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallLegacyDatabaseSignInWithUsername() throws Exception {
        when(configuration.loginAfterSignUp()).thenReturn(true);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", "username");
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(signUpRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(signUpRequest).start(any(BaseCallback.class));
        verify(signUpRequest).setScope("openid user photos");
        verify(signUpRequest, never()).setAudience("aud");
        verify(client).signUp(eq("email@domain.com"), eq("password"), eq("username"), eq("connection"));
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallLegacyDatabaseSignIn() throws Exception {
        when(configuration.loginAfterSignUp()).thenReturn(true);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email", "password", null);
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(signUpRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(signUpRequest).start(any(BaseCallback.class));
        verify(signUpRequest).setScope("openid user photos");
        verify(signUpRequest, never()).setAudience("aud");
        verify(client).signUp(eq("email"), eq("password"), eq("connection"));
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldFailDatabasePasswordResetOnNullConnection() throws Exception {
        when(configuration.getDatabaseConnection()).thenReturn(null);
        DatabaseChangePasswordEvent event = new DatabaseChangePasswordEvent("email@domain.com");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView, never()).showProgress(true);
        verify(options, never()).getAuthenticationAPIClient();
        verify(dbRequest, never()).start(any(BaseCallback.class));
        verify(authRequest, never()).addAuthenticationParameters(anyMapOf(String.class, Object.class));
        verify(client, never()).resetPassword(anyString(), anyString());
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallDatabasePasswordReset() throws Exception {
        DatabaseChangePasswordEvent event = new DatabaseChangePasswordEvent("email@domain.com");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(dbRequest, never()).addParameters(any(Map.class));
        verify(dbRequest).start(any(BaseCallback.class));
        verify(client).resetPassword(eq("email@domain.com"), eq("connection"));
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallOAuthAuthenticationWithActiveFlow() throws Exception {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection, "email@domain.com", "password");
        activity.onOAuthAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).start(any(BaseCallback.class));
        verify(authRequest).setScope("openid user photos");
        verify(authRequest, never()).setAudience("aud");
        verify(client).login(eq("email@domain.com"), eq("password"), eq("my-connection"));

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallOAuthAuthenticationWithCustomProvider() throws Exception {
        AuthProvider customProvider = mock(AuthProvider.class);
        AuthHandler handler = mock(AuthHandler.class);
        when(handler.providerFor(anyString(), eq("custom-connection"))).thenReturn(customProvider);
        AuthResolver.setAuthHandlers(Collections.singletonList(handler));

        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("custom-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);


        verify(lockView, never()).showProgress(true);
        verify(customProvider).setParameters(mapCaptor.capture());
        verify(customProvider).start(eq(activity), any(AuthCallback.class), eq(REQ_CODE_PERMISSIONS), eq(REQ_CODE_CUSTOM_PROVIDER));
        AuthResolver.setAuthHandlers(Collections.emptyList());

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallOAuthAuthenticationWithWebProvider() throws Exception {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        when(options.useBrowser()).thenReturn(true);
        activity.onOAuthAuthenticationRequest(event);

        verify(lockView, never()).showProgress(eq(true));
        verify(webProvider).start(eq(activity), eq("my-connection"), any(AuthCallback.class), eq(REQ_CODE_WEB_PROVIDER));
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