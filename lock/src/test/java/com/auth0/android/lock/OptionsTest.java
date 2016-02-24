package com.auth0.android.lock;

import android.os.Build;
import android.os.Parcel;
import android.support.v7.appcompat.BuildConfig;

import com.auth0.Auth0;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.enums.UsernameStyle;

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
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class OptionsTest {

    private static final String CLIENT_ID = "CLIENT_ID";
    private static final String DOMAIN = "https://my-domain.auth0.com";
    private static final String CONFIG_DOMAIN = "https://my-cdn.auth0.com";
    private static final String SCOPE_KEY = "scope";
    private static final String DEVICE_KEY = "device";
    private static final String SCOPE_OPENID_OFFLINE_ACCESS = "openid offline_access";

    private Auth0 auth0;

    @Before
    public void setUp() throws Exception {
        auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
    }

    @Test
    public void shouldSetAccount() {
        Options options = new Options();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getAccount().getClientId(), is(equalTo(parceledOptions.getAccount().getClientId())));
        assertThat(options.getAccount().getConfigurationUrl(), is(equalTo(parceledOptions.getAccount().getConfigurationUrl())));
        assertThat(options.getAccount().getDomainUrl(), is(equalTo(parceledOptions.getAccount().getDomainUrl())));
    }

    @Test
    public void shouldUseBrowser() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setUseBrowser(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
    }

    @Test
    public void shouldBeClosable() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setClosable(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
    }

    @Test
    public void shouldBeFullscreen() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setFullscreen(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
    }

    @Test
    public void shouldSendSDKInfo() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setSendSDKInfo(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.sendSDKInfo(), is(equalTo(parceledOptions.sendSDKInfo())));
    }

    @Test
    public void shouldUseEmailUsernameStyle() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setUsernameStyle(UsernameStyle.EMAIL);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
    }

    @Test
    public void shouldNotLoginAfterSignUp() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setLoginAfterSignUp(false);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.loginAfterSignUp(), is(equalTo(parceledOptions.loginAfterSignUp())));
    }

    @Test
    public void shouldUseUsernameUsernameStyle() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setUsernameStyle(UsernameStyle.USERNAME);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
    }

    @Test
    public void shouldUseDefaultUsernameStyle() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setUsernameStyle(UsernameStyle.DEFAULT);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
    }

    @Test
    public void shouldBeSignUpEnabled() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setSignUpEnabled(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isSignUpEnabled(), is(equalTo(parceledOptions.isSignUpEnabled())));
    }

    @Test
    public void shouldBeChangePasswordEnabled() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setChangePasswordEnabled(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isChangePasswordEnabled(), is(equalTo(parceledOptions.isChangePasswordEnabled())));
    }

    @Test
    public void shouldUsePasswordlessMode() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setPasswordlessMode(PasswordlessMode.EMAIL_CODE);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.passwordlessMode(), is(equalTo(parceledOptions.passwordlessMode())));
    }

    @Test
    public void shouldNotHavePasswordlessModeByDefault() {
        Options options = new Options();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.passwordlessMode(), is(nullValue()));
        assertThat(parceledOptions.passwordlessMode(), is(nullValue()));
    }

    @Test
    public void shouldSetDefaultDatabaseConnection() {
        Options options = new Options();
        options.setAccount(auth0);
        options.useDatabaseConnection("default_db_connection");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getDefaultDatabaseConnection(), is(equalTo(parceledOptions.getDefaultDatabaseConnection())));
    }

    @Test
    public void shouldSetConnections() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setConnections(createConnections("twitter", "facebook"));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getConnections(), is(equalTo(parceledOptions.getConnections())));
    }


    @Test
    public void shouldSetEnterpriseConnectionsUsingWebForm() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setEnterpriseConnectionsUsingWebForm(createEnterpriseConnectionsUsingWebForm("myAD"));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getEnterpriseConnectionsUsingWebForm(), is(equalTo(parceledOptions.getEnterpriseConnectionsUsingWebForm())));
    }

    @Test
    public void shouldSetAuthenticationParameters() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setAuthenticationParameters(createAuthenticationParameters(654123));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getAuthenticationParameters(), is(equalTo(parceledOptions.getAuthenticationParameters())));
    }

    @Test
    public void shouldSetDeviceParameterIfUsingOfflineAccessScope() {
        Options options = new Options();
        options.setAccount(auth0);
        HashMap<String, Object> params = new HashMap<>();
        params.put(SCOPE_KEY, SCOPE_OPENID_OFFLINE_ACCESS);
        options.setAuthenticationParameters(params);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getAuthenticationParameters().get(DEVICE_KEY), is(notNullValue()));
        assertThat((String) options.getAuthenticationParameters().get(DEVICE_KEY), is(Build.MODEL));
        assertThat(parceledOptions.getAuthenticationParameters().get(DEVICE_KEY), is(notNullValue()));
        assertThat((String) parceledOptions.getAuthenticationParameters().get(DEVICE_KEY), is(Build.MODEL));
    }

    @Test
    public void shouldNotOverrideDeviceParameterIfAlreadySet() {
        Options options = new Options();
        options.setAccount(auth0);
        HashMap<String, Object> params = new HashMap<>();
        params.put(SCOPE_KEY, SCOPE_OPENID_OFFLINE_ACCESS);
        params.put(DEVICE_KEY, "my_device 2016");
        options.setAuthenticationParameters(params);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getAuthenticationParameters().get(DEVICE_KEY), is(notNullValue()));
        assertThat((String) options.getAuthenticationParameters().get(DEVICE_KEY), is(not(Build.MODEL)));
        assertThat(parceledOptions.getAuthenticationParameters().get(DEVICE_KEY), is(notNullValue()));
        assertThat((String) parceledOptions.getAuthenticationParameters().get(DEVICE_KEY), is(not(Build.MODEL)));
    }

    @Test
    public void shouldSetDefaultValues() {
        Options options = new Options();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertTrue(options != parceledOptions); //assure correct Parcelable object testing
        assertThat(options.sendSDKInfo(), is(true));
        assertThat(options.useBrowser(), is(false));
        assertThat(options.isSignUpEnabled(), is(true));
        assertThat(options.isChangePasswordEnabled(), is(true));
        assertThat(options.loginAfterSignUp(), is(true));
        assertThat(options.passwordlessMode(), is(nullValue()));
    }


    @Test
    public void shouldSetAllTrueFields() throws Exception {
        Options options = new Options();
        options.setAccount(auth0);

        options.setChangePasswordEnabled(true);
        options.setClosable(true);
        options.setFullscreen(true);
        options.setSendSDKInfo(true);
        options.setUseBrowser(true);
        options.setUsernameStyle(UsernameStyle.EMAIL);
        options.setSignUpEnabled(true);
        options.setLoginAfterSignUp(true);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isChangePasswordEnabled(), is(equalTo(parceledOptions.isChangePasswordEnabled())));
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
        assertThat(options.sendSDKInfo(), is(equalTo(parceledOptions.sendSDKInfo())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.isSignUpEnabled(), is(equalTo(parceledOptions.isSignUpEnabled())));
        assertThat(options.loginAfterSignUp(), is(equalTo(parceledOptions.loginAfterSignUp())));
    }

    @Test
    public void shouldSetAllFalseFields() throws Exception {
        Options options = new Options();
        options.setAccount(auth0);

        options.setChangePasswordEnabled(false);
        options.setClosable(false);
        options.setFullscreen(false);
        options.setSendSDKInfo(false);
        options.setUseBrowser(false);
        options.setUsernameStyle(UsernameStyle.USERNAME);
        options.setSignUpEnabled(false);
        options.setLoginAfterSignUp(false);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isChangePasswordEnabled(), is(equalTo(parceledOptions.isChangePasswordEnabled())));
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
        assertThat(options.sendSDKInfo(), is(equalTo(parceledOptions.sendSDKInfo())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.isSignUpEnabled(), is(equalTo(parceledOptions.isSignUpEnabled())));
        assertThat(options.loginAfterSignUp(), is(equalTo(parceledOptions.loginAfterSignUp())));
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