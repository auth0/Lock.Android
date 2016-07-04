package com.auth0.android.lock;

import android.os.Build;
import android.os.Parcel;
import android.support.v7.appcompat.BuildConfig;

import com.auth0.android.auth0.Auth0;
import com.auth0.android.lock.CustomField.FieldType;
import com.auth0.android.lock.enums.InitialScreen;
import com.auth0.android.lock.enums.SocialButtonStyle;
import com.auth0.android.lock.enums.UsernameStyle;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
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
    public void shouldUseBigSocialButtonStyle() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setSocialButtonStyle(SocialButtonStyle.BIG);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.socialButtonStyle(), is(equalTo(parceledOptions.socialButtonStyle())));
    }

    @Test
    public void shouldUseSmallSocialButtonStyle() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setSocialButtonStyle(SocialButtonStyle.SMALL);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.socialButtonStyle(), is(equalTo(parceledOptions.socialButtonStyle())));
    }

    @Test
    public void shouldUsePKCE() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setUsePKCE(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usePKCE(), is(equalTo(parceledOptions.usePKCE())));
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
    public void shouldChangeInitialScreenToLogIn() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setInitialScreen(InitialScreen.LOG_IN);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
    }

    @Test
    public void shouldChangeInitialScreenToSignUp() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setInitialScreen(InitialScreen.SIGN_UP);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
    }

    @Test
    public void shouldChangeInitialScreenToForgotPassword() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setInitialScreen(InitialScreen.FORGOT_PASSWORD);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
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
    public void shouldAllowLogIn() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setAllowLogIn(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.allowLogIn(), is(equalTo(parceledOptions.allowLogIn())));
    }

    @Test
    public void shouldAllowSignUp() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setAllowSignUp(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.allowSignUp(), is(equalTo(parceledOptions.allowSignUp())));
    }

    @Test
    public void shouldAllowForgotPassword() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setAllowForgotPassword(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.allowForgotPassword(), is(equalTo(parceledOptions.allowForgotPassword())));
    }

    @Test
    public void shouldUsePasswordlessCode() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setUseCodePasswordless(false);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.useCodePasswordless(), is(equalTo(parceledOptions.useCodePasswordless())));
    }

    @Test
    public void shouldHavePasswordlessCodeByDefault() {
        Options options = new Options();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.useCodePasswordless(), is(true));
        assertThat(parceledOptions.useCodePasswordless(), is(true));
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
    public void shouldSetDefaultTheme() {
        Options options = new Options();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getTheme(), is(notNullValue()));
        assertThat(parceledOptions.getTheme(), is(notNullValue()));
    }


    @Test
    public void shouldSetCustomTheme() {
        Options options = new Options();
        options.setAccount(auth0);
        Theme theme = Theme.newBuilder()
                .withHeaderTitle(R.string.com_auth0_lock_header_title)
                .withHeaderLogo(R.drawable.com_auth0_lock_header_logo)
                .withHeaderColor(R.color.com_auth0_lock_social_unknown)
                .withHeaderTitleColor(R.color.com_auth0_lock_social_unknown)
                .withPrimaryColor(R.color.com_auth0_lock_social_unknown)
                .withDarkPrimaryColor(R.color.com_auth0_lock_social_unknown)
                .build();
        options.withTheme(theme);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getTheme().getCustomDarkPrimaryColorRes(), is(equalTo(parceledOptions.getTheme().getCustomDarkPrimaryColorRes())));
        assertThat(options.getTheme().getCustomPrimaryColorRes(), is(equalTo(parceledOptions.getTheme().getCustomPrimaryColorRes())));
        assertThat(options.getTheme().getCustomHeaderColorRes(), is(equalTo(parceledOptions.getTheme().getCustomHeaderColorRes())));
        assertThat(options.getTheme().getCustomHeaderLogoRes(), is(equalTo(parceledOptions.getTheme().getCustomHeaderLogoRes())));
        assertThat(options.getTheme().getCustomHeaderTitleRes(), is(equalTo(parceledOptions.getTheme().getCustomHeaderTitleRes())));
        assertThat(options.getTheme().getCustomHeaderTitleColorRes(), is(equalTo(parceledOptions.getTheme().getCustomHeaderTitleColorRes())));
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
    public void shouldSetCustomFields() {
        Options options = new Options();
        options.setAccount(auth0);
        options.setCustomFields(createCustomFields());

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(parceledOptions.getCustomFields(), hasSize(options.getCustomFields().size()));
        for (int i = 0; i < options.getCustomFields().size(); i++) {
            CustomField fieldA = options.getCustomFields().get(i);
            CustomField fieldB = parceledOptions.getCustomFields().get(i);
            assertThat(fieldA.getKey(), is(equalTo(fieldB.getKey())));
        }
    }

    @Test
    public void shouldGetEmptyCustomFieldsIfNotSet() {
        Options options = new Options();
        options.setAccount(auth0);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getCustomFields(), is(notNullValue()));
        assertThat(options.getCustomFields().size(), is(0));
        assertThat(parceledOptions.getCustomFields(), is(notNullValue()));
        assertThat(parceledOptions.getCustomFields().size(), is(0));
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
        assertThat(options.useBrowser(), is(false));
        assertThat(options.usePKCE(), is(false));
        assertThat(options.allowLogIn(), is(true));
        assertThat(options.allowSignUp(), is(true));
        assertThat(options.allowForgotPassword(), is(true));
        assertThat(options.loginAfterSignUp(), is(true));
        assertThat(options.useCodePasswordless(), is(true));
        assertThat(options.usernameStyle(), is(equalTo(UsernameStyle.DEFAULT)));
        assertThat(options.socialButtonStyle(), is(equalTo(SocialButtonStyle.UNSPECIFIED)));
        assertThat(options.getTheme(), is(notNullValue()));
    }


    @Test
    public void shouldSetAllTrueFields() throws Exception {
        Options options = new Options();
        options.setAccount(auth0);

        options.setFullscreen(true);
        options.setUseBrowser(true);
        options.setUsePKCE(true);
        options.setUsernameStyle(UsernameStyle.EMAIL);
        options.setInitialScreen(InitialScreen.LOG_IN);
        options.setAllowLogIn(true);
        options.setAllowSignUp(true);
        options.setAllowForgotPassword(true);
        options.setClosable(true);
        options.setSocialButtonStyle(SocialButtonStyle.BIG);
        options.setLoginAfterSignUp(true);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.usePKCE(), is(equalTo(parceledOptions.usePKCE())));
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
        assertThat(options.allowLogIn(), is(equalTo(parceledOptions.allowLogIn())));
        assertThat(options.allowSignUp(), is(equalTo(parceledOptions.allowSignUp())));
        assertThat(options.allowForgotPassword(), is(equalTo(parceledOptions.allowForgotPassword())));
        assertThat(options.socialButtonStyle(), is(equalTo(parceledOptions.socialButtonStyle())));
        assertThat(options.loginAfterSignUp(), is(equalTo(parceledOptions.loginAfterSignUp())));
    }

    @Test
    public void shouldSetAllFalseFields() throws Exception {
        Options options = new Options();
        options.setAccount(auth0);

        options.setClosable(false);
        options.setFullscreen(false);
        options.setUseBrowser(false);
        options.setUsePKCE(false);
        options.setUsernameStyle(UsernameStyle.USERNAME);
        options.setInitialScreen(InitialScreen.SIGN_UP);
        options.setAllowLogIn(false);
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(false);
        options.setSocialButtonStyle(SocialButtonStyle.SMALL);
        options.setLoginAfterSignUp(false);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.isFullscreen(), is(equalTo(parceledOptions.isFullscreen())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.usePKCE(), is(equalTo(parceledOptions.usePKCE())));
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
        assertThat(options.allowLogIn(), is(equalTo(parceledOptions.allowLogIn())));
        assertThat(options.allowSignUp(), is(equalTo(parceledOptions.allowSignUp())));
        assertThat(options.allowForgotPassword(), is(equalTo(parceledOptions.allowForgotPassword())));
        assertThat(options.socialButtonStyle(), is(equalTo(parceledOptions.socialButtonStyle())));
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

    private List<CustomField> createCustomFields() {
        CustomField fieldNumber = new CustomField(R.drawable.com_auth0_lock_ic_phone, FieldType.TYPE_PHONE_NUMBER, "number", R.string.com_auth0_lock_hint_phone_number);
        CustomField fieldSurname = new CustomField(R.drawable.com_auth0_lock_ic_username, FieldType.TYPE_TEXT_NAME, "surname", R.string.com_auth0_lock_hint_username);

        List<CustomField> customFields = new ArrayList<>();
        customFields.add(fieldNumber);
        customFields.add(fieldSurname);
        return customFields;
    }

    private List<String> createConnections(String... connections) {
        return Arrays.asList(connections);
    }

    private List<String> createEnterpriseConnectionsUsingWebForm(String... connections) {
        return Arrays.asList(connections);
    }
}