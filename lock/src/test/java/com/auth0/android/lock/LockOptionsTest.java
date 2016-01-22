package com.auth0.android.lock;

import android.os.Bundle;

import com.auth0.Auth0;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class LockOptionsTest {

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String DOMAIN = "https://my-domain.auth0.com";
    private static final String CONFIG_DOMAIN = "https://my-cdn.auth0.com";

    private static final String OPTIONS_KEY = "OPTIONS_KEY";

    Auth0 auth0;

    @Before
    public void setUp() throws Exception {
        auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
    }

    @Test
    public void testParcelableAllTrue() throws Exception {
        HashMap<String, Object> authenticationParameters = createAuthenticationParameters(654321);
        boolean changePasswordEnabled = true;
        boolean closable = true;
        List<String> connections = createConnections("twitter", "facebook");
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = createEnterpriseConnectionsUsingWebForm("myAD");
        boolean fullscreen = true;
        boolean sendSdkInfo = true;
        boolean signUpEnabled = true;
        boolean useBrowser = true;
        boolean useEmail = true;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testParcelableAllFalse() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = false;
        boolean closable = false;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = false;
        boolean sendSdkInfo = false;
        boolean signUpEnabled = false;
        boolean useBrowser = false;
        boolean useEmail = false;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testChangePasswordEnabled() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = true;
        boolean closable = false;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = false;
        boolean sendSdkInfo = false;
        boolean signUpEnabled = false;
        boolean useBrowser = false;
        boolean useEmail = false;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testClosable() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = false;
        boolean closable = true;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = false;
        boolean sendSdkInfo = false;
        boolean signUpEnabled = false;
        boolean useBrowser = false;
        boolean useEmail = false;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testFullscreen() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = false;
        boolean closable = false;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = true;
        boolean sendSdkInfo = false;
        boolean signUpEnabled = false;
        boolean useBrowser = false;
        boolean useEmail = false;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testSendSdkInfo() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = false;
        boolean closable = false;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = false;
        boolean sendSdkInfo = true;
        boolean signUpEnabled = false;
        boolean useBrowser = false;
        boolean useEmail = false;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testSignUpEnabled() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = false;
        boolean closable = false;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = false;
        boolean sendSdkInfo = false;
        boolean signUpEnabled = true;
        boolean useBrowser = false;
        boolean useEmail = false;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testUseBrowser() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = false;
        boolean closable = false;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = false;
        boolean sendSdkInfo = false;
        boolean signUpEnabled = false;
        boolean useBrowser = true;
        boolean useEmail = false;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    @Test
    public void testUseEmail() throws Exception {
        HashMap<String, Object> authenticationParameters = null;
        boolean changePasswordEnabled = false;
        boolean closable = false;
        List<String> connections = null;
        String defaultDatabaseConnection = "default_db_connection";
        List<String> enterpriseConnectionsUsingWebForm = null;
        boolean fullscreen = false;
        boolean sendSdkInfo = false;
        boolean signUpEnabled = false;
        boolean useBrowser = false;
        boolean useEmail = true;

        LockOptions options = createOptions(
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);

        Bundle bundle = new Bundle();
        bundle.putParcelable(OPTIONS_KEY, options);
        LockOptions optionsBundle = bundle.getParcelable(OPTIONS_KEY);

        check(optionsBundle,
                auth0, authenticationParameters, changePasswordEnabled, closable, connections,
                defaultDatabaseConnection, enterpriseConnectionsUsingWebForm, fullscreen,
                sendSdkInfo, signUpEnabled, useBrowser, useEmail);
    }

    private LockOptions createOptions(
            Auth0 account,
            HashMap<String, Object> authenticationParameters,
            boolean changePasswordEnabled,
            boolean closable,
            List<String> connections,
            String defaultDatabaseConnection,
            List<String> enterpriseConnectionsUsingWebForm,
            boolean fullscreen,
            boolean sendSdkInfo,
            boolean signUpEnabled,
            boolean useBrowser,
            boolean useEmail) {
        LockOptions options = new LockOptions();
        options.account = account;
        options.authenticationParameters = authenticationParameters;
        options.changePasswordEnabled = changePasswordEnabled;
        options.closable = closable;
        options.connections = connections;
        options.defaultDatabaseConnection = defaultDatabaseConnection;
        options.enterpriseConnectionsUsingWebForm = enterpriseConnectionsUsingWebForm;
        options.fullscreen = fullscreen;
        options.sendSDKInfo = sendSdkInfo;
        options.signUpEnabled = signUpEnabled;
        options.useBrowser = useBrowser;
        options.useEmail = useEmail;
        return options;
    }

    private void check(
            LockOptions optionsBundle,
            Auth0 account,
            HashMap<String, Object> authenticationParameters,
            boolean changePasswordEnabled,
            boolean closable,
            List<String> connections,
            String defaultDatabaseConnection,
            List<String> enterpriseConnectionsUsingWebForm,
            boolean fullscreen,
            boolean sendSdkInfo,
            boolean signUpEnabled,
            boolean useBrowser,
            boolean useEmail) throws Exception {

        Auth0 auth0bundle = optionsBundle.account;
        assertThat(auth0bundle.getClientId(), is(equalTo(account.getClientId())));
        assertThat(auth0bundle.getDomainUrl(), is(equalTo(account.getDomainUrl())));
        assertThat(auth0bundle.getConfigurationUrl(), is(equalTo(account.getConfigurationUrl())));
        assertThat(optionsBundle.authenticationParameters, is(equalTo(authenticationParameters)));
        assertThat(optionsBundle.changePasswordEnabled, is(equalTo(changePasswordEnabled)));
        assertThat(optionsBundle.closable, is(equalTo(closable)));
        assertThat(optionsBundle.connections, is(equalTo(connections)));
        assertThat(optionsBundle.defaultDatabaseConnection, is(equalTo(defaultDatabaseConnection)));
        assertThat(optionsBundle.enterpriseConnectionsUsingWebForm, is(equalTo(enterpriseConnectionsUsingWebForm)));
        assertThat(optionsBundle.fullscreen, is(equalTo(fullscreen)));
        assertThat(optionsBundle.sendSDKInfo, is(equalTo(sendSdkInfo)));
        assertThat(optionsBundle.signUpEnabled, is(equalTo(signUpEnabled)));
        assertThat(optionsBundle.useBrowser, is(equalTo(useBrowser)));
        assertThat(optionsBundle.useEmail, is(equalTo(useEmail)));
    }

    private HashMap<String, Object> createAuthenticationParameters(int innerIntParam) {
        HashMap<String, Object> authenticationParameters = new HashMap<>();
        authenticationParameters.put("key_param_string", "value_param_string");
        authenticationParameters.put("key_param_int", 123456);
        HashMap<String, Object> otherParameters = new HashMap<>();
        otherParameters.put("key_other_param_string", "value_other_param_string");
        otherParameters.put("key_other_param_int", innerIntParam);
        authenticationParameters.put("key_param_map", otherParameters);
        return authenticationParameters;
    }

    private List<String> createConnections(String... connections) {
        return Arrays.asList(connections);
    }

    private List<String> createEnterpriseConnectionsUsingWebForm(String... connections) {
        return Arrays.asList(connections);
    }
}