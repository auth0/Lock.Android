package com.auth0.android.lock.internal.configuration;

import android.os.Build;
import android.os.Parcel;
import android.support.v7.appcompat.BuildConfig;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.lock.AuthButtonSize;
import com.auth0.android.lock.InitialScreen;
import com.auth0.android.lock.R;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.lock.utils.CustomField;
import com.auth0.android.lock.utils.CustomField.FieldType;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
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

    private Options options;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        options = new Options();
        options.setAccount(new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN));
    }

    @Test
    public void shouldSetAccount() throws Exception {
        Auth0 auth0 = new Auth0(CLIENT_ID, DOMAIN, CONFIG_DOMAIN);
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
    public void shouldSetMustAcceptTerms() throws Exception {
        options.setMustAcceptTerms(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.mustAcceptTerms(), is(true));
        assertThat(options.mustAcceptTerms(), is(equalTo(parceledOptions.mustAcceptTerms())));
    }

    @Test
    public void shouldSetShowTerms() throws Exception {
        options.setShowTerms(false);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.showTerms(), is(false));
        assertThat(options.showTerms(), is(equalTo(parceledOptions.showTerms())));
    }

    @Test
    public void shouldSetPrivacyPolicyURL() throws Exception {
        options.setPrivacyURL("https://valid.url/privacy");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getPrivacyURL(), is("https://valid.url/privacy"));
        assertThat(options.getPrivacyURL(), is(equalTo(parceledOptions.getPrivacyURL())));
    }

    @Test
    public void shouldThrowWhenSettingPrivacyPolicyURLWithInvalidURL() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The given Policy Privacy URL doesn't have a valid URL format: an-invalid/url");
        options.setPrivacyURL("an-invalid/url");
    }

    @Test
    public void shouldSetTermsOfServiceURL() throws Exception {
        options.setTermsURL("https://valid.url/terms");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getTermsURL(), is("https://valid.url/terms"));
        assertThat(options.getTermsURL(), is(equalTo(parceledOptions.getTermsURL())));
    }

    @Test
    public void shouldThrowWhenSettingTermsOfServiceURLWithInvalidURL() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The given Terms of Service URL doesn't have a valid URL format: an-invalid/url");
        options.setTermsURL("an-invalid/url");
    }

    @Test
    public void shouldSetSupportURL() throws Exception {
        options.setSupportURL("https://valid.url/support");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getSupportURL(), is("https://valid.url/support"));
        assertThat(options.getSupportURL(), is(equalTo(parceledOptions.getSupportURL())));
    }

    @Test
    public void shouldThrowWhenSettingSupportURLWithInvalidURL() throws Exception {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("The given Support URL doesn't have a valid URL format: an-invalid/url");
        options.setSupportURL("an-invalid/url");
    }

    @Test
    public void shouldUseWebView() throws Exception {
        options.setUseBrowser(false);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.useBrowser(), is(false));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
    }

    @Test
    public void shouldUseLabeledSubmitButton() throws Exception {
        options.setUseLabeledSubmitButton(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.useLabeledSubmitButton(), is(true));
        assertThat(options.useLabeledSubmitButton(), is(equalTo(parceledOptions.useLabeledSubmitButton())));
    }

    @Test
    public void shouldHideMainScreenTitle() throws Exception {
        options.setHideMainScreenTitle(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.hideMainScreenTitle(), is(true));
        assertThat(options.hideMainScreenTitle(), is(equalTo(parceledOptions.hideMainScreenTitle())));
    }


    @Test
    public void shouldSetPasswordlessAutoSubmit() throws Exception {
        options.setRememberLastPasswordlessLogin(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.rememberLastPasswordlessAccount(), is(true));
        assertThat(options.rememberLastPasswordlessAccount(), is(equalTo(parceledOptions.rememberLastPasswordlessAccount())));
    }

    @Test
    public void shouldUseBigSocialButtonStyle() throws Exception {
        options.setAuthButtonSize(AuthButtonSize.BIG);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.authButtonSize(), is(AuthButtonSize.BIG));
        assertThat(options.authButtonSize(), is(equalTo(parceledOptions.authButtonSize())));
    }

    @Test
    public void shouldUseSmallSocialButtonStyle() throws Exception {
        options.setAuthButtonSize(AuthButtonSize.SMALL);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.authButtonSize(), is(equalTo(AuthButtonSize.SMALL)));
        assertThat(options.authButtonSize(), is(equalTo(parceledOptions.authButtonSize())));
    }

    @Test
    public void shouldHavePKCEEnabledByDefault() throws Exception {
        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usePKCE(), is(parceledOptions.usePKCE()));
        assertThat(options.usePKCE(), is(true));
        assertThat(parceledOptions.usePKCE(), is(true));
    }

    @Test
    public void shouldEnablePKCE() throws Exception {
        options.setUsePKCE(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usePKCE(), is(parceledOptions.usePKCE()));
        assertThat(options.usePKCE(), is(true));
        assertThat(parceledOptions.usePKCE(), is(true));
    }

    @Test
    public void shouldDisablePKCE() throws Exception {
        options.setUsePKCE(false);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usePKCE(), is(parceledOptions.usePKCE()));
        assertThat(options.usePKCE(), is(false));
        assertThat(parceledOptions.usePKCE(), is(false));
    }

    @Test
    public void shouldBeClosable() throws Exception {
        options.setClosable(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.isClosable(), is(true));
    }

    @Test
    public void shouldNotLoginAfterSignUp() throws Exception {
        options.setLoginAfterSignUp(false);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.loginAfterSignUp(), is(equalTo(parceledOptions.loginAfterSignUp())));
        assertThat(options.loginAfterSignUp(), is(false));
    }

    @Test
    public void shouldChangeInitialScreenToLogIn() throws Exception {
        options.setInitialScreen(InitialScreen.LOG_IN);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
        assertThat(options.initialScreen(), is(InitialScreen.LOG_IN));
    }

    @Test
    public void shouldChangeInitialScreenToSignUp() throws Exception {
        options.setInitialScreen(InitialScreen.SIGN_UP);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
        assertThat(options.initialScreen(), is(InitialScreen.SIGN_UP));
    }

    @Test
    public void shouldChangeInitialScreenToForgotPassword() throws Exception {
        options.setInitialScreen(InitialScreen.FORGOT_PASSWORD);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
        assertThat(options.initialScreen(), is(InitialScreen.FORGOT_PASSWORD));
    }

    @Test
    public void shouldUseEmailUsernameStyle() throws Exception {
        options.setUsernameStyle(UsernameStyle.EMAIL);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.usernameStyle(), is(UsernameStyle.EMAIL));
    }

    @Test
    public void shouldUseUsernameUsernameStyle() throws Exception {
        options.setUsernameStyle(UsernameStyle.USERNAME);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.usernameStyle(), is(UsernameStyle.USERNAME));
    }

    @Test
    public void shouldUseDefaultUsernameStyle() throws Exception {
        options.setUsernameStyle(UsernameStyle.DEFAULT);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.usernameStyle(), is(UsernameStyle.DEFAULT));
    }

    @Test
    public void shouldAllowLogIn() throws Exception {
        options.setAllowLogIn(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.allowLogIn(), is(equalTo(parceledOptions.allowLogIn())));
        assertThat(options.allowLogIn(), is(true));
    }

    @Test
    public void shouldAllowSignUp() throws Exception {
        options.setAllowSignUp(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.allowSignUp(), is(equalTo(parceledOptions.allowSignUp())));
        assertThat(options.allowSignUp(), is(true));
    }

    @Test
    public void shouldAllowForgotPassword() throws Exception {
        options.setAllowForgotPassword(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.allowForgotPassword(), is(equalTo(parceledOptions.allowForgotPassword())));
        assertThat(options.allowForgotPassword(), is(true));
    }

    @Test
    public void shouldAllowShowPassword() throws Exception {
        options.setAllowShowPassword(true);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.allowShowPassword(), is(equalTo(parceledOptions.allowShowPassword())));
        assertThat(options.allowShowPassword(), is(true));
    }

    @Test
    public void shouldUsePasswordlessCode() throws Exception {
        options.setUseCodePasswordless(false);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.useCodePasswordless(), is(equalTo(parceledOptions.useCodePasswordless())));
        assertThat(options.useCodePasswordless(), is(false));
    }

    @Test
    public void shouldHavePasswordlessCodeByDefault() throws Exception {
        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.useCodePasswordless(), is(equalTo(parceledOptions.useCodePasswordless())));
        assertThat(options.useCodePasswordless(), is(true));
        assertThat(parceledOptions.useCodePasswordless(), is(true));
    }

    @Test
    public void shouldSetDefaultDatabaseConnection() throws Exception {
        options.useDatabaseConnection("default_db_connection");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getDefaultDatabaseConnection(), is(equalTo(parceledOptions.getDefaultDatabaseConnection())));
        assertThat(options.getDefaultDatabaseConnection(), is("default_db_connection"));
    }

    @Test
    public void shouldSetDefaultTheme() {
        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getTheme(), is(notNullValue()));
        assertThat(parceledOptions.getTheme(), is(notNullValue()));
    }


    @Test
    public void shouldSetCustomTheme() throws Exception {
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
    public void shouldSetConnections() throws Exception {
        options.setConnections(createConnections("twitter", "facebook"));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getConnections(), is(containsInAnyOrder("twitter", "facebook")));
        assertThat(options.getConnections(), is(equalTo(parceledOptions.getConnections())));
    }


    @Test
    public void shouldSetEnterpriseConnectionsUsingWebForm() throws Exception {
        options.setEnterpriseConnectionsUsingWebForm(createEnterpriseConnectionsUsingWebForm("myAD"));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getEnterpriseConnectionsUsingWebForm(), containsInAnyOrder("myAD"));
        assertThat(options.getEnterpriseConnectionsUsingWebForm(), is(equalTo(parceledOptions.getEnterpriseConnectionsUsingWebForm())));
    }

    @Test
    public void shouldSetAuthenticationParameters() throws Exception {
        options.setAuthenticationParameters(createAuthenticationParameters(654123));

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getAuthenticationParameters(), is(equalTo(parceledOptions.getAuthenticationParameters())));
    }

    @Test
    public void shouldSetConnectionScope() throws Exception {
        options.withConnectionScope("some_connection", "scope for some connection");
        options.withConnectionScope("other_connection", "scope for other connection");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getConnectionsScope(), is(equalTo(parceledOptions.getConnectionsScope())));
        assertThat(options.getConnectionsScope().size(), is(2));
        assertThat(options.getConnectionsScope(), hasEntry("some_connection", "scope for some connection"));
        assertThat(options.getConnectionsScope(), hasEntry("other_connection", "scope for other connection"));
        assertThat(parceledOptions.getConnectionsScope().size(), is(2));
        assertThat(parceledOptions.getConnectionsScope(), hasEntry("some_connection", "scope for some connection"));
        assertThat(parceledOptions.getConnectionsScope(), hasEntry("other_connection", "scope for other connection"));
    }

    @Test
    public void shouldSetScope() throws Exception {
        options.withScope("some connection scope");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getScope(), is(equalTo("some connection scope")));
        assertThat(parceledOptions.getScope(), is("some connection scope"));
    }

    @Test
    public void shouldSetAudience() throws Exception {
        options.withAudience("https://domain.auth0.com/users");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getAudience(), is(equalTo("https://domain.auth0.com/users")));
        assertThat(parceledOptions.getAudience(), is("https://domain.auth0.com/users"));
    }

    @Test
    public void shouldSetScheme() throws Exception {
        options.withScheme("auth0");

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getScheme(), is(equalTo("auth0")));
        assertThat(parceledOptions.getScheme(), is("auth0"));
    }

    @SuppressWarnings("ResourceType")
    @Test
    public void shouldAddAuthStyles() throws Exception {
        options.withAuthStyle("firstConnection", 1);
        options.withAuthStyle("secondConnection", 2);
        options.withAuthStyle("thirdConnection", 3);

        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.getAuthStyles().size(), is(3));
        assertThat(options.getAuthStyles(), is(hasEntry("firstConnection", 1)));
        assertThat(options.getAuthStyles(), is(hasEntry("secondConnection", 2)));
        assertThat(options.getAuthStyles(), is(hasEntry("thirdConnection", 3)));
        assertThat(parceledOptions.getAuthStyles().size(), is(3));
        assertThat(parceledOptions.getAuthStyles(), is(hasEntry("firstConnection", 1)));
        assertThat(parceledOptions.getAuthStyles(), is(hasEntry("secondConnection", 2)));
        assertThat(parceledOptions.getAuthStyles(), is(hasEntry("thirdConnection", 3)));
    }

    @Test
    public void shouldSetCustomFields() throws Exception {
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
    public void shouldGetEmptyCustomFieldsIfNotSet() throws Exception {
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
    public void shouldSetDeviceParameterIfUsingOfflineAccessScope() throws Exception {
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
    public void shouldNotOverrideDeviceParameterIfAlreadySet() throws Exception {
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
    public void shouldSetDefaultValues() throws Exception {
        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertTrue(options != parceledOptions); //assure correct Parcelable object testing
        assertThat(options.useBrowser(), is(true));
        assertThat(options.usePKCE(), is(true));
        assertThat(options.allowLogIn(), is(true));
        assertThat(options.allowSignUp(), is(true));
        assertThat(options.allowForgotPassword(), is(true));
        assertThat(options.allowShowPassword(), is(true));
        assertThat(options.loginAfterSignUp(), is(true));
        assertThat(options.useCodePasswordless(), is(true));
        assertThat(options.mustAcceptTerms(), is(false));
        assertThat(options.showTerms(), is(true));
        assertThat(options.useLabeledSubmitButton(), is(true));
        assertThat(options.hideMainScreenTitle(), is(false));
        assertThat(options.rememberLastPasswordlessAccount(), is(false));
        assertThat(options.getScope(), is(nullValue()));
        assertThat(options.getAudience(), is(nullValue()));
        assertThat(options.getScheme(), is(nullValue()));
        assertThat(options.usernameStyle(), is(equalTo(UsernameStyle.DEFAULT)));
        assertThat(options.authButtonSize(), is(equalTo(AuthButtonSize.UNSPECIFIED)));
        assertThat(options.getTheme(), is(notNullValue()));
        assertThat(options.getAuthenticationParameters(), is(notNullValue()));
        assertThat(options.getAuthStyles(), is(notNullValue()));
    }


    @Test
    public void shouldSetAllTrueFields() throws Exception {
        options.setUseBrowser(true);
        options.setUsePKCE(true);
        options.setUsernameStyle(UsernameStyle.EMAIL);
        options.setInitialScreen(InitialScreen.LOG_IN);
        options.setAllowLogIn(true);
        options.setAllowSignUp(true);
        options.setAllowForgotPassword(true);
        options.setAllowShowPassword(true);
        options.setClosable(true);
        options.setMustAcceptTerms(true);
        options.setShowTerms(true);
        options.setAuthButtonSize(AuthButtonSize.BIG);
        options.setLoginAfterSignUp(true);
        options.setUseLabeledSubmitButton(true);
        options.setHideMainScreenTitle(true);
        options.setRememberLastPasswordlessLogin(true);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.mustAcceptTerms(), is(equalTo(parceledOptions.mustAcceptTerms())));
        assertThat(options.showTerms(), is(equalTo(parceledOptions.showTerms())));
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.usePKCE(), is(equalTo(parceledOptions.usePKCE())));
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
        assertThat(options.allowLogIn(), is(equalTo(parceledOptions.allowLogIn())));
        assertThat(options.allowSignUp(), is(equalTo(parceledOptions.allowSignUp())));
        assertThat(options.allowForgotPassword(), is(equalTo(parceledOptions.allowForgotPassword())));
        assertThat(options.allowShowPassword(), is(equalTo(parceledOptions.allowShowPassword())));
        assertThat(options.authButtonSize(), is(equalTo(parceledOptions.authButtonSize())));
        assertThat(options.loginAfterSignUp(), is(equalTo(parceledOptions.loginAfterSignUp())));
        assertThat(options.useLabeledSubmitButton(), is(equalTo(parceledOptions.useLabeledSubmitButton())));
        assertThat(options.hideMainScreenTitle(), is(equalTo(parceledOptions.hideMainScreenTitle())));
        assertThat(options.rememberLastPasswordlessAccount(), is(equalTo(parceledOptions.rememberLastPasswordlessAccount())));
    }

    @Test
    public void shouldSetAllFalseFields() throws Exception {
        options.setClosable(false);
        options.setUseBrowser(false);
        options.setUsePKCE(false);
        options.setUsernameStyle(UsernameStyle.USERNAME);
        options.setInitialScreen(InitialScreen.SIGN_UP);
        options.setAllowLogIn(false);
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(false);
        options.setAllowShowPassword(false);
        options.setMustAcceptTerms(false);
        options.setShowTerms(false);
        options.setAuthButtonSize(AuthButtonSize.SMALL);
        options.setLoginAfterSignUp(false);
        options.setUseLabeledSubmitButton(false);
        options.setHideMainScreenTitle(false);
        options.setRememberLastPasswordlessLogin(false);


        Parcel parcel = Parcel.obtain();
        options.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);

        Options parceledOptions = Options.CREATOR.createFromParcel(parcel);
        assertThat(options.mustAcceptTerms(), is(equalTo(parceledOptions.mustAcceptTerms())));
        assertThat(options.showTerms(), is(equalTo(parceledOptions.showTerms())));
        assertThat(options.isClosable(), is(equalTo(parceledOptions.isClosable())));
        assertThat(options.useBrowser(), is(equalTo(parceledOptions.useBrowser())));
        assertThat(options.usePKCE(), is(equalTo(parceledOptions.usePKCE())));
        assertThat(options.usernameStyle(), is(equalTo(parceledOptions.usernameStyle())));
        assertThat(options.initialScreen(), is(equalTo(parceledOptions.initialScreen())));
        assertThat(options.allowLogIn(), is(equalTo(parceledOptions.allowLogIn())));
        assertThat(options.allowSignUp(), is(equalTo(parceledOptions.allowSignUp())));
        assertThat(options.allowForgotPassword(), is(equalTo(parceledOptions.allowForgotPassword())));
        assertThat(options.allowShowPassword(), is(equalTo(parceledOptions.allowShowPassword())));
        assertThat(options.authButtonSize(), is(equalTo(parceledOptions.authButtonSize())));
        assertThat(options.loginAfterSignUp(), is(equalTo(parceledOptions.loginAfterSignUp())));
        assertThat(options.useLabeledSubmitButton(), is(equalTo(parceledOptions.useLabeledSubmitButton())));
        assertThat(options.hideMainScreenTitle(), is(equalTo(parceledOptions.hideMainScreenTitle())));
        assertThat(options.rememberLastPasswordlessAccount(), is(equalTo(parceledOptions.rememberLastPasswordlessAccount())));
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

    @Test
    public void shouldCreateAuthenticationAPIClientInstance() throws Exception {
        AuthenticationAPIClient client = options.getAuthenticationAPIClient();

        assertThat(client, is(notNullValue()));
    }

    private List<CustomField> createCustomFields() {
        CustomField fieldNumber = new CustomField(R.drawable.com_auth0_lock_ic_phone, FieldType.TYPE_PHONE_NUMBER, "number", R.string.com_auth0_lock_hint_phone_number);
        CustomField fieldSurname = new CustomField(R.drawable.com_auth0_lock_ic_username, FieldType.TYPE_NAME, "surname", R.string.com_auth0_lock_hint_username);

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