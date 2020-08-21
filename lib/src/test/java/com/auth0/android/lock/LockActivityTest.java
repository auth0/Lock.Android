package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.authentication.request.DatabaseConnectionRequest;
import com.auth0.android.authentication.request.SignUpRequest;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.lock.events.DatabaseChangePasswordEvent;
import com.auth0.android.lock.events.DatabaseLoginEvent;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.internal.configuration.Connection;
import com.auth0.android.lock.internal.configuration.DatabaseConnection;
import com.auth0.android.lock.internal.configuration.OAuthConnection;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.lock.provider.AuthResolver;
import com.auth0.android.lock.views.ClassicLockView;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.AuthHandler;
import com.auth0.android.provider.AuthProvider;
import com.auth0.android.request.AuthRequest;
import com.auth0.android.result.Credentials;

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
import static org.hamcrest.CoreMatchers.nullValue;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.atLeastOnce;

@SuppressWarnings("ResultOfMethodCallIgnored")
@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
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
    AuthRequest authRequest;
    @Mock
    SignUpRequest signUpRequest;
    @Mock
    DatabaseConnectionRequest dbRequest;
    @Mock
    ClassicLockView lockView;
    @Captor
    ArgumentCaptor<Map> mapCaptor;
    @Captor
    ArgumentCaptor<BaseCallback<Credentials, AuthenticationException>> callbackCaptor;
    Configuration configuration;
    LockActivity activity;
    HashMap basicParameters;
    HashMap connectionScope;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        basicParameters = new HashMap<>(Collections.singletonMap("extra", "value"));
        connectionScope = new HashMap<>(Collections.singletonMap("custom-connection", "the connection scope"));
        when(options.getAccount()).thenReturn(new Auth0("cliendId", "domain"));
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getAudience()).thenReturn("aud");
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getConnectionsScope()).thenReturn(connectionScope);

        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        when(client.login(anyString(), anyString(), anyString())).thenReturn(authRequest);
        when(client.loginWithOTP(anyString(), anyString())).thenReturn(authRequest);
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
        configuration = spy(new Configuration(java.util.Collections.<Connection>emptyList(), options));
        when(configuration.getDatabaseConnection()).thenReturn(connection);

        activity = new LockActivity(configuration, options, lockView, webProvider);
    }

    @Test
    public void shouldFailDatabaseLoginOnNullConnection() {
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
    public void shouldCallLegacyDatabaseLogin() {
        DatabaseLoginEvent event = new DatabaseLoginEvent("username", "password");
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).login("username", "password", "connection");
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
    public void shouldCallLegacyDatabaseLoginWithVerificationCode() {
        DatabaseLoginEvent event = new DatabaseLoginEvent("username", "password");
        event.setVerificationCode("123456");
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).login("username", "password", "connection");
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
    public void shouldCallOIDCDatabaseLoginWithOTPCodeAndMFAToken() {
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
        event.setVerificationCode("123456");
        event.setMFAToken("mfaToken");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).loginWithOTP("mfaToken", "123456");
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).start(any(BaseCallback.class));
        verify(authRequest).setScope("openid user photos");
        verify(authRequest).setAudience("aud");
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
        assertThat(reqParams, not(hasKey("mfa_code")));
    }

    @Test
    public void shouldCallDatabaseLoginThatWillRequireVerification() {
        Auth0 account = new Auth0("cliendId", "domain");
        account.setOIDCConformant(true);
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getAudience()).thenReturn("aud");
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);

        LockActivity activity = new LockActivity(configuration, options, lockView, webProvider);
        DatabaseLoginEvent event = new DatabaseLoginEvent("john@doe.com", "123456");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).login("john@doe.com", "123456", "connection");
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).start(callbackCaptor.capture());
        verify(authRequest).setScope("openid user photos");
        verify(authRequest).setAudience("aud");
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        BaseCallback<Credentials, AuthenticationException> callback = callbackCaptor.getValue();
        AuthenticationException flaggedRequestErr = mock(AuthenticationException.class);
        when(flaggedRequestErr.isVerificationRequired()).thenReturn(true);
        callback.onFailure(flaggedRequestErr);

        Map<String, String> firstAuthValues = mapCaptor.getValue();
        assertThat(firstAuthValues, hasEntry("extra", "value"));

        verify(webProvider).start(eq(activity), eq("connection"), mapCaptor.capture(), any(AuthCallback.class), eq(REQ_CODE_WEB_PROVIDER));

        Map<String, String> secondAuthValues = mapCaptor.getValue();
        assertThat(secondAuthValues, hasEntry("login_hint", "john@doe.com"));
        assertThat(secondAuthValues, hasEntry("screen_hint", "login"));
    }

    @Test
    public void shouldCallDatabaseSignUpThatWillRequireVerification() {
        Auth0 account = new Auth0("cliendId", "domain");
        account.setOIDCConformant(true);
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getAudience()).thenReturn("aud");
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        //set sign in behavior
        when(options.loginAfterSignUp()).thenReturn(true);
        when(configuration.loginAfterSignUp()).thenReturn(true);

        LockActivity activity = new LockActivity(configuration, options, lockView, webProvider);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent("john@doe.com", "123456", "johncito");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).signUp("john@doe.com", "123456", "johncito", "connection");
        verify(signUpRequest).setScope("openid user photos");
        verify(signUpRequest).setAudience("aud");
        verify(signUpRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(signUpRequest).start(callbackCaptor.capture());
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        BaseCallback<Credentials, AuthenticationException> callback = callbackCaptor.getValue();
        AuthenticationException flaggedRequestErr = mock(AuthenticationException.class);
        when(flaggedRequestErr.isVerificationRequired()).thenReturn(true);
        callback.onFailure(flaggedRequestErr);

        Map<String, String> firstAuthValues = mapCaptor.getValue();
        assertThat(firstAuthValues, hasEntry("extra", "value"));

        verify(webProvider).start(eq(activity), eq("connection"), mapCaptor.capture(), any(AuthCallback.class), eq(REQ_CODE_WEB_PROVIDER));

        Map<String, String> secondAuthValues = mapCaptor.getValue();
        assertThat(secondAuthValues, hasEntry("login_hint", "john@doe.com"));
        assertThat(secondAuthValues, hasEntry("screen_hint", "signup"));
    }

    @Test
    public void shouldCallDatabaseCreateUserThatWillRequireVerification() {
        Auth0 account = new Auth0("cliendId", "domain");
        account.setOIDCConformant(true);
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getAudience()).thenReturn("aud");
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        //set create user behavior
        when(options.loginAfterSignUp()).thenReturn(false);
        when(configuration.loginAfterSignUp()).thenReturn(false);

        LockActivity activity = new LockActivity(configuration, options, lockView, webProvider);
        DatabaseSignUpEvent event = new DatabaseSignUpEvent("john@doe.com", "123456", "johncito");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(client).createUser("john@doe.com", "123456", "johncito", "connection");
        verifyZeroInteractions(authRequest);
        verify(dbRequest).start(callbackCaptor.capture());
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        BaseCallback<Credentials, AuthenticationException> callback = callbackCaptor.getValue();
        AuthenticationException flaggedRequestErr = mock(AuthenticationException.class);
        when(flaggedRequestErr.isVerificationRequired()).thenReturn(true);
        callback.onFailure(flaggedRequestErr);

        verify(webProvider).start(eq(activity), eq("connection"), mapCaptor.capture(), any(AuthCallback.class), eq(REQ_CODE_WEB_PROVIDER));

        Map<String, String> extraAuthValues = mapCaptor.getValue();
        assertThat(extraAuthValues, hasEntry("login_hint", "john@doe.com"));
        assertThat(extraAuthValues, hasEntry("screen_hint", "signup"));
    }

    @Test
    public void shouldCallOIDCDatabaseLoginWithCustomAudience() {
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
        verify(client).login("username", "password", "connection");
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
    public void shouldFailDatabaseSignUpOnNullConnection() {
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
    public void shouldCallDatabaseSignUpWithUsername() {
        when(configuration.loginAfterSignUp()).thenReturn(false);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", "username");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(dbRequest).start(any(BaseCallback.class));
        verify(client).createUser("email@domain.com", "password", "username", "connection");
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallDatabaseSignUp() {
        when(configuration.loginAfterSignUp()).thenReturn(false);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", null);
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(dbRequest).start(any(BaseCallback.class));
        verify(client).createUser("email@domain.com", "password", "connection");
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallOIDCDatabaseSignInWithCustomAudience() {
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
        verify(client).signUp("email@domain.com", "password", "username", "connection");
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallLegacyDatabaseSignInWithUsername() {
        when(configuration.loginAfterSignUp()).thenReturn(true);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email@domain.com", "password", "username");
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(signUpRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(signUpRequest).start(any(BaseCallback.class));
        verify(signUpRequest).setScope("openid user photos");
        verify(signUpRequest, never()).setAudience("aud");
        verify(client).signUp("email@domain.com", "password", "username", "connection");
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallLegacyDatabaseSignIn() {
        when(configuration.loginAfterSignUp()).thenReturn(true);

        DatabaseSignUpEvent event = new DatabaseSignUpEvent("email", "password", null);
        activity.onDatabaseAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(signUpRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(signUpRequest).start(any(BaseCallback.class));
        verify(signUpRequest).setScope("openid user photos");
        verify(signUpRequest, never()).setAudience("aud");
        verify(client).signUp("email", "password", "connection");
        verify(configuration, atLeastOnce()).getDatabaseConnection();

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldFailDatabasePasswordResetOnNullConnection() {
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
    public void shouldCallDatabasePasswordReset() {
        DatabaseChangePasswordEvent event = new DatabaseChangePasswordEvent("email@domain.com");
        activity.onDatabaseAuthenticationRequest(event);

        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(dbRequest, never()).addParameters(any(Map.class));
        verify(dbRequest).start(any(BaseCallback.class));
        verify(client).resetPassword("email@domain.com", "connection");
        verify(configuration, atLeastOnce()).getDatabaseConnection();
    }

    @Test
    public void shouldCallEnterpriseOAuthAuthenticationWithActiveFlow() {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        when(connection.isActiveFlowEnabled()).thenReturn(true);
        OAuthLoginEvent event = new OAuthLoginEvent(connection, "email@domain.com", "password");
        activity.onOAuthAuthenticationRequest(event);


        verify(lockView).showProgress(true);
        verify(options).getAuthenticationAPIClient();
        verify(authRequest).addAuthenticationParameters(mapCaptor.capture());
        verify(authRequest).start(any(BaseCallback.class));
        verify(authRequest).setScope("openid user photos");
        verify(authRequest, never()).setAudience("aud");
        verify(client).login("email@domain.com", "password", "my-connection");

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
    }

    @Test
    public void shouldCallOAuthAuthenticationWithCustomProvider() {
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
        assertThat(reqParams, hasEntry("scope", "openid user photos"));
        assertThat(reqParams, hasEntry("connection_scope", "the connection scope"));
        assertThat(reqParams, not(hasKey("audience")));
    }

    @Test
    public void shouldCallOAuthAuthenticationWithCustomProviderAndAudience() {
        Auth0 account = new Auth0("cliendId", "domain");
        account.setOIDCConformant(true);
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getAudience()).thenReturn("aud");
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getConnectionsScope()).thenReturn(connectionScope);
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        LockActivity activity = new LockActivity(configuration, options, lockView, webProvider);


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
        assertThat(reqParams, hasEntry("scope", "openid user photos"));
        assertThat(reqParams, hasEntry("connection_scope", "the connection scope"));
        assertThat(reqParams, hasEntry("audience", "aud"));
    }

    @Test
    public void shouldCallEnterpriseOAuthAuthenticationWithCustomProvider() {
        Auth0 account = new Auth0("cliendId", "domain");
        Options options = mock(Options.class);
        when(options.getAccount()).thenReturn(account);
        when(options.getAuthenticationAPIClient()).thenReturn(client);
        when(options.getScope()).thenReturn("openid user photos");
        when(options.getConnectionsScope()).thenReturn(connectionScope);
        when(options.getAuthenticationParameters()).thenReturn(basicParameters);
        LockActivity activity = new LockActivity(configuration, options, lockView, webProvider);


        AuthProvider customProvider = mock(AuthProvider.class);
        AuthHandler handler = mock(AuthHandler.class);
        when(handler.providerFor(anyString(), eq("custom-connection"))).thenReturn(customProvider);
        AuthResolver.setAuthHandlers(Collections.singletonList(handler));

        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("custom-connection");
        when(connection.isActiveFlowEnabled()).thenReturn(false);
        OAuthLoginEvent event = new OAuthLoginEvent(connection, "user@domain.com", null);
        activity.onOAuthAuthenticationRequest(event);


        verify(lockView, never()).showProgress(true);
        verify(customProvider).setParameters(mapCaptor.capture());
        verify(customProvider).start(eq(activity), any(AuthCallback.class), eq(REQ_CODE_PERMISSIONS), eq(REQ_CODE_CUSTOM_PROVIDER));
        AuthResolver.setAuthHandlers(Collections.emptyList());

        Map<String, String> reqParams = mapCaptor.getValue();
        assertThat(reqParams, is(notNullValue()));
        assertThat(reqParams, hasEntry("extra", "value"));
        assertThat(reqParams, hasEntry("scope", "openid user photos"));
        assertThat(reqParams, hasEntry("connection_scope", "the connection scope"));
        assertThat(reqParams, hasEntry("login_hint", "user@domain.com"));
    }

    @Test
    public void shouldCallEnterpriseOAuthAuthenticationWithWebProvider() {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        when(connection.isActiveFlowEnabled()).thenReturn(false);
        OAuthLoginEvent event = new OAuthLoginEvent(connection, "user@domain.com", null);
        when(options.useBrowser()).thenReturn(true);
        activity.onOAuthAuthenticationRequest(event);

        verify(lockView, never()).showProgress(true);
        verify(webProvider).start(eq(activity), eq("my-connection"), mapCaptor.capture(), any(AuthCallback.class), eq(REQ_CODE_WEB_PROVIDER));

        Map<String, String> extraParams = mapCaptor.getValue();
        assertThat(extraParams, is(notNullValue()));
        assertThat(extraParams.size(), is(1));
        assertThat(extraParams, hasEntry("login_hint", "user@domain.com"));
    }

    @Test
    public void shouldResumeEnterpriseOAuthAuthenticationWithWebProviderOnActivityResult() {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        when(connection.isActiveFlowEnabled()).thenReturn(false);
        OAuthLoginEvent event = new OAuthLoginEvent(connection, "user@domain.com", null);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onActivityResult(REQ_CODE_WEB_PROVIDER, Activity.RESULT_OK, intent);

        verify(lockView).showProgress(false);
        verify(webProvider).resume(REQ_CODE_WEB_PROVIDER, Activity.RESULT_OK, intent);
    }

    @Test
    public void shouldCallOAuthAuthenticationWithWebProvider() {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        when(options.useBrowser()).thenReturn(true);
        activity.onOAuthAuthenticationRequest(event);

        verify(lockView, never()).showProgress(true);
        verify(webProvider).start(eq(activity), eq("my-connection"), mapCaptor.capture(), any(AuthCallback.class), eq(REQ_CODE_WEB_PROVIDER));

        Map<String, String> extraParams = mapCaptor.getValue();
        assertThat(extraParams, is(nullValue()));
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithWebProviderOnActivityResult() {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onActivityResult(REQ_CODE_WEB_PROVIDER, Activity.RESULT_OK, intent);

        verify(lockView).showProgress(false);
        verify(webProvider).resume(REQ_CODE_WEB_PROVIDER, Activity.RESULT_OK, intent);
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithCustomProviderOnActivityResult() {
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
        verify(customProvider).authorize(REQ_CODE_CUSTOM_PROVIDER, Activity.RESULT_OK, intent);
        AuthResolver.setAuthHandlers(Collections.emptyList());
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithWebProviderOnNewIntent() {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onNewIntent(intent);

        verify(lockView).showProgress(false);
        verify(webProvider).resume(intent);
    }

    @Test
    public void shouldResumeOAuthAuthenticationWithCustomProviderOnNewIntent() {
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
        verify(customProvider).authorize(intent);
        AuthResolver.setAuthHandlers(Collections.emptyList());
    }

    @Test
    public void shouldResumeEnterpriseOAuthAuthenticationWithWebProviderOnNewIntent() {
        OAuthConnection connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("my-connection");
        OAuthLoginEvent event = new OAuthLoginEvent(connection, "user@domain.com", null);
        activity.onOAuthAuthenticationRequest(event);

        Intent intent = mock(Intent.class);
        activity.onNewIntent(intent);

        verify(lockView).showProgress(false);
        verify(webProvider).resume(intent);
    }
}