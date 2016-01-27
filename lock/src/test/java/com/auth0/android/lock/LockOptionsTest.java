package com.auth0.android.lock;

import android.os.Parcel;

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
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class LockOptionsTest {

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String DOMAIN = "https://my-domain.auth0.com";
    private static final String CONFIG_DOMAIN = "https://my-cdn.auth0.com";

    private Auth0 auth0;

    @Before
    public void setUp() throws Exception {
        auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
    }

    @Test
    public void shouldSetAccount() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.getAccount().getClientId(), is(equalTo(parceledOptions.getAccount().getClientId())));
        assertThat(options.getAccount().getConfigurationUrl(), is(equalTo(parceledOptions.getAccount().getConfigurationUrl())));
        assertThat(options.getAccount().getDomainUrl(), is(equalTo(parceledOptions.getAccount().getDomainUrl())));
    }

    @Test
    public void shouldUseBrowser() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setUseBrowser(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
    }

    @Test
    public void shouldBeClosable() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setClosable(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
    }

    @Test
    public void shouldBeFullscreen() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setFullscreen(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
    }

    @Test
    public void shouldSendSDKInfo() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setSendSDKInfo(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.sendSDKInfo(), is(equalTo(parceledOptions.sendSDKInfo())));
    }

    @Test
    public void shouldUseEmail() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setUseEmail(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.useEmail(), is(equalTo(parceledOptions.useEmail())));
    }

    @Test
    public void shouldBeSignUpEnabled() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setSignUpEnabled(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.isSignUpEnabled(), is(equalTo(parceledOptions.isSignUpEnabled())));
    }

    @Test
    public void shouldBeChangePasswordEnabled() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setChangePasswordEnabled(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.isChangePasswordEnabled(), is(equalTo(parceledOptions.isChangePasswordEnabled())));
    }


    @Test
    public void shouldSetDefaultDatabaseConnection() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setDefaultDatabaseConnection("default_db_connection");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.getDefaultDatabaseConnection(), is(equalTo(parceledOptions.getDefaultDatabaseConnection())));
    }

    @Test
    public void shouldSetConnections() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setConnections(createConnections("twitter", "facebook"));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.getConnections(), is(equalTo(parceledOptions.getConnections())));
    }


    @Test
    public void shouldSetEnterpriseConnectionsUsingWebForm() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setEnterpriseConnectionsUsingWebForm(createEnterpriseConnectionsUsingWebForm("myAD"));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.getEnterpriseConnectionsUsingWebForm(), is(equalTo(parceledOptions.getEnterpriseConnectionsUsingWebForm())));
    }

    @Test
    public void shouldSetAuthenticationParameters() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);
        options.setAuthenticationParameters(createAuthenticationParameters(654123));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.getAuthenticationParameters(), is(equalTo(parceledOptions.getAuthenticationParameters())));
    }

    @Test
    public void shouldSetDefaultValues() {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertTrue(options != parceledOptions); //assure correct Parcelable object testing
        assertThat(options.sendSDKInfo(), is(true));
        assertThat(options.useBrowser(), is(false));
        assertThat(options.isSignUpEnabled(), is(true));
        assertThat(options.isChangePasswordEnabled(), is(true));
    }


    @Test
    public void shouldSetAllTrueFields() throws Exception {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);

        options.setChangePasswordEnabled(true);
        options.setClosable(true);
        options.setFullscreen(true);
        options.setSendSDKInfo(true);
        options.setUseBrowser(true);
        options.setUseEmail(true);
        options.setSignUpEnabled(true);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.isChangePasswordEnabled(), is(equalTo(parceledOptions.isChangePasswordEnabled())));
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
        assertThat(options.sendSDKInfo(), is(equalTo(parceledOptions.sendSDKInfo())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.useEmail(), is(equalTo(parceledOptions.useEmail())));
        assertThat(options.isSignUpEnabled(), is(equalTo(parceledOptions.isSignUpEnabled())));
    }

    @Test
    public void shouldSetAllFalseFields() throws Exception {
        LockOptions options = new LockOptions();
        options.setAccount(auth0);

        options.setChangePasswordEnabled(false);
        options.setClosable(false);
        options.setFullscreen(false);
        options.setSendSDKInfo(false);
        options.setUseBrowser(false);
        options.setUseEmail(false);
        options.setSignUpEnabled(false);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        LockOptions parceledOptions = LockOptions.CREATOR.createFromParcel(parcel);
        assertThat(options.isChangePasswordEnabled(), is(equalTo(parceledOptions.isChangePasswordEnabled())));
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
        assertThat(options.sendSDKInfo(), is(equalTo(parceledOptions.sendSDKInfo())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.useEmail(), is(equalTo(parceledOptions.useEmail())));
        assertThat(options.isSignUpEnabled(), is(equalTo(parceledOptions.isSignUpEnabled())));
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