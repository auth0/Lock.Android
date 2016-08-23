/*
 * ConfigurationTest.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock;

import com.auth0.android.lock.CustomField.FieldType;
import com.auth0.android.lock.enums.InitialScreen;
import com.auth0.android.lock.enums.PasswordStrength;
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.enums.SocialButtonStyle;
import com.auth0.android.lock.enums.Strategies;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.utils.json.Application;
import com.auth0.android.lock.utils.json.AuthData;
import com.auth0.android.lock.utils.json.GsonBaseTest;
import com.google.gson.stream.JsonReader;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.auth0.android.lock.utils.Strategies.Email;
import static com.auth0.android.lock.utils.Strategies.Facebook;
import static com.auth0.android.lock.utils.Strategies.Instagram;
import static com.auth0.android.lock.utils.Strategies.Linkedin;
import static com.auth0.android.lock.utils.Strategies.SMS;
import static com.auth0.android.lock.utils.Strategies.Yahoo;
import static com.auth0.android.lock.utils.Strategies.Yammer;
import static com.auth0.android.lock.utils.Strategies.Yandex;
import static com.auth0.android.lock.utils.json.ConnectionMatcher.hasName;
import static com.auth0.android.lock.utils.json.ConnectionMatcher.hasStrategy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ConfigurationTest extends GsonBaseTest {

    private static final String RESTRICTIVE_DATABASE = "RestrictiveDatabase";
    private static final String CUSTOM_DATABASE = "CustomDatabase";
    private static final String USERNAME_PASSWORD_AUTHENTICATION = "Username-Password-Authentication";
    private static final String TWITTER = "twitter";
    private static final String EMAIL = "email";
    private static final String MY_AD = "MyAD";
    private static final String MY_SECOND_AD = "mySecondAD";
    private static final String UNKNOWN_AD = "UnknownAD";
    private static final String UNKNOWN_CONNECTION = "UnknownConnection";
    private static final String CUSTOM_PASSWORDLESS_CONNECTION = "my-sms-connection";

    private Configuration configuration;
    private Application application;
    private Options options;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        final FileReader fr = new FileReader("src/test/resources/appinfo.json");
        application = createGson().fromJson(new JsonReader(fr), Application.class);
        options = new Options();
    }

    @Test
    public void shouldKeepApplicationDefaultsIfOptionsAreNotModified() throws Exception {
        configuration = new Configuration(application, options);
        assertThat(configuration.isUsernameRequired(), is(false));
        assertThat(configuration.allowLogIn(), is(true));
        assertThat(configuration.allowSignUp(), is(true));
        assertThat(configuration.allowForgotPassword(), is(true));
        assertThat(configuration.loginAfterSignUp(), is(true));
        assertThat(configuration.getUsernameStyle(), is(equalTo(UsernameStyle.DEFAULT)));
        assertThat(configuration.getInitialScreen(), is(equalTo(InitialScreen.LOG_IN)));
        assertThat(configuration.getSocialButtonStyle(), is(equalTo(SocialButtonStyle.UNSPECIFIED)));
        assertThat(configuration.hasExtraFields(), is(false));
        assertThat(configuration.getPasswordPolicy(), is(PasswordStrength.NONE));
        assertThat(configuration.mustAcceptTerms(), is(false));
    }

    @Test
    public void shouldGetValidStyleForNotOverriddenStrategy() throws Exception {
        configuration = new Configuration(application, options);
        assertThat(configuration.authStyleForConnection("facebook", "facebook-prod"), is(R.style.Lock_Theme_AuthStyle_Facebook));
    }

    @Test
    public void shouldGetStyleForOverriddenStrategy() throws Exception {
        //noinspection ResourceType
        options.withAuthStyle("facebook-prod", 123456);
        configuration = new Configuration(application, options);
        assertThat(configuration.authStyleForConnection("facebook", "facebook-prod"), is(123456));
    }

    @Test
    public void shouldMergeApplicationWithOptionsIfDefaultDatabaseExists() throws Exception {
        options.setConnections(Collections.singletonList(USERNAME_PASSWORD_AUTHENTICATION));
        options.setAllowLogIn(false);
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(false);
        options.setLoginAfterSignUp(false);
        options.setUsernameStyle(UsernameStyle.USERNAME);
        options.setSocialButtonStyle(SocialButtonStyle.BIG);
        configuration = new Configuration(application, options);
        assertThat(configuration.isUsernameRequired(), is(false));
        assertThat(configuration.allowLogIn(), is(false));
        assertThat(configuration.allowSignUp(), is(false));
        assertThat(configuration.allowForgotPassword(), is(false));
        assertThat(configuration.loginAfterSignUp(), is(false));
        assertThat(configuration.getUsernameStyle(), is(equalTo(UsernameStyle.USERNAME)));
        assertThat(configuration.getSocialButtonStyle(), is(equalTo(SocialButtonStyle.BIG)));
        assertThat(configuration.hasExtraFields(), is(false));
    }

    @Test
    public void shouldNotMergeApplicationWithOptionsIfApplicationIsRestrictive() throws Exception {
        options.setConnections(Collections.singletonList(RESTRICTIVE_DATABASE));
        options.setAllowSignUp(true);
        options.setAllowForgotPassword(true);
        configuration = new Configuration(application, options);
        assertThat(configuration.allowSignUp(), is(false));
        assertThat(configuration.allowForgotPassword(), is(false));
    }

    @Test
    public void shouldNotUseClassicLockIfNoConnectionsAreAvailable() throws Exception {
        configuration = filteredConfigBy("");
        assertThat(configuration.isClassicLockAvailable(), is(false));
    }

    @Test
    public void shouldNotUseClassicLockIfAllScreensAreDisabled() throws Exception {
        options.setAllowLogIn(false);
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(false);
        configuration = new Configuration(application, options);
        assertThat(configuration.isClassicLockAvailable(), is(false));
    }

    @Test
    public void shouldUseClassicLockWithEnterpriseConnections() throws Exception {
        configuration = filteredConfigBy(MY_AD);
        assertThat(configuration.isClassicLockAvailable(), is(true));
    }

    @Test
    public void shouldNotUseClassicLockWithEnterpriseConnectionsAndLogInDisabled() throws Exception {
        options.setAllowLogIn(false);
        configuration = filteredConfigBy(MY_AD);
        assertThat(configuration.isClassicLockAvailable(), is(false));
    }

    @Test
    public void shouldUseClassicLockWithSocialConnections() throws Exception {
        configuration = filteredConfigBy(TWITTER);
        assertThat(configuration.isClassicLockAvailable(), is(true));
    }

    @Test
    public void shouldUseClassicLockWithSocialConnectionsInLogInScreen() throws Exception {
        options.setAllowLogIn(true);
        options.setAllowSignUp(false);
        configuration = filteredConfigBy(TWITTER);
        assertThat(configuration.isClassicLockAvailable(), is(true));
    }

    @Test
    public void shouldUseClassicLockWithSocialConnectionsInSignUpScreen() throws Exception {
        options.setAllowLogIn(false);
        options.setAllowSignUp(true);
        configuration = filteredConfigBy(TWITTER);
        assertThat(configuration.isClassicLockAvailable(), is(true));
    }

    @Test
    public void shouldNotUseClassicLockWithSocialConnectionsAndScreensDisabled() throws Exception {
        options.setAllowLogIn(false);
        options.setAllowSignUp(false);
        configuration = filteredConfigBy(TWITTER);
        assertThat(configuration.isClassicLockAvailable(), is(false));
    }

    @Test
    public void shouldUseClassicLockWithDatabaseConnections() throws Exception {
        options.useDatabaseConnection(USERNAME_PASSWORD_AUTHENTICATION);
        configuration = filteredConfigBy(USERNAME_PASSWORD_AUTHENTICATION);
        assertThat(configuration.isClassicLockAvailable(), is(true));
    }

    @Test
    public void shouldNotUsePasswordlessLockWithoutConnections() throws Exception {
        configuration = filteredConfigBy("");
        assertThat(configuration.isPasswordlessLockAvailable(), is(false));
    }

    @Test
    public void shouldUsePasswordlessLockWithSocialConnections() throws Exception {
        configuration = filteredConfigBy(TWITTER);
        assertThat(configuration.isPasswordlessLockAvailable(), is(true));
    }

    @Test
    public void shouldUsePasswordlessLockWithPasswordlessConnections() throws Exception {
        configuration = filteredConfigBy(EMAIL);
        assertThat(configuration.isPasswordlessLockAvailable(), is(true));
    }

    @Test
    public void shouldPasswordlessLockNotBeAffectedByClassicLockScreenFlags() throws Exception {
        configuration = filteredConfigBy(EMAIL, TWITTER);
        options.setAllowLogIn(false);
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(false);
        assertThat(configuration.isPasswordlessLockAvailable(), is(true));
    }

    @Test
    public void shouldSetExtraSignUpFields() throws Exception {
        options.setCustomFields(createCustomFields());
        configuration = new Configuration(application, options);

        assertThat(configuration.hasExtraFields(), is(true));
        assertThat(configuration.getExtraSignUpFields(), contains(options.getCustomFields().toArray()));
    }

    @Test
    public void shouldSetCorrectInitialScreenIfLogInIsDisabled() throws Exception {
        options.setConnections(Collections.singletonList(USERNAME_PASSWORD_AUTHENTICATION));
        options.setInitialScreen(InitialScreen.LOG_IN);
        options.setAllowLogIn(false);
        options.setAllowSignUp(true);
        options.setAllowForgotPassword(true);
        configuration = new Configuration(application, options);

        assertThat(configuration.getInitialScreen(), is(InitialScreen.SIGN_UP));
    }

    @Test
    public void shouldSetCorrectInitialScreenIfSignUpIsDisabled() throws Exception {
        options.setConnections(Collections.singletonList(USERNAME_PASSWORD_AUTHENTICATION));
        options.setInitialScreen(InitialScreen.SIGN_UP);
        options.setAllowLogIn(true);
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(true);
        configuration = new Configuration(application, options);

        assertThat(configuration.getInitialScreen(), is(InitialScreen.LOG_IN));
    }

    @Test
    public void shouldSetCorrectInitialScreenIfForgotPasswordIsDisabled() throws Exception {
        options.setConnections(Collections.singletonList(USERNAME_PASSWORD_AUTHENTICATION));
        options.setInitialScreen(InitialScreen.FORGOT_PASSWORD);
        options.setAllowLogIn(true);
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(false);
        configuration = new Configuration(application, options);

        assertThat(configuration.getInitialScreen(), is(InitialScreen.LOG_IN));

        options.setConnections(Collections.singletonList(USERNAME_PASSWORD_AUTHENTICATION));
        options.setInitialScreen(InitialScreen.FORGOT_PASSWORD);
        options.setAllowLogIn(false);
        options.setAllowSignUp(true);
        options.setAllowForgotPassword(false);
        configuration = new Configuration(application, options);

        assertThat(configuration.getInitialScreen(), is(InitialScreen.SIGN_UP));
    }

    @Test
    public void shouldPreferPasswordlessEmailOverSMSWhenBothAvailable() throws Exception {
        options.setUseCodePasswordless(true);
        options.setConnections(Arrays.asList(SMS.getName(), Email.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.EMAIL_CODE));

        options.setUseCodePasswordless(false);
        options.setConnections(Arrays.asList(SMS.getName(), Email.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.EMAIL_LINK));
    }

    @Test
    public void shouldSetCorrectPasswordlessTypeWhenUsingEmail() throws Exception {
        options.setUseCodePasswordless(true);
        options.setConnections(Arrays.asList(Email.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.EMAIL_CODE));

        options.setUseCodePasswordless(false);
        options.setConnections(Arrays.asList(Email.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.EMAIL_LINK));
    }


    @Test
    public void shouldSetCorrectPasswordlessTypeWhenUsingSMS() throws Exception {
        options.setUseCodePasswordless(true);
        options.setConnections(Arrays.asList(SMS.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.SMS_CODE));

        options.setUseCodePasswordless(false);
        options.setConnections(Arrays.asList(SMS.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.SMS_LINK));
    }

    @Test
    public void shouldNotHavePasswordlessModeOnNoConnections() throws Exception {
        options.setUseCodePasswordless(true);
        options.setConnections(Collections.singletonList(Facebook.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.DISABLED));
    }

    @Test
    public void shouldDefaultToCodePasswordlessWhenTypeMissingFromOptions() throws Exception {
        options.setConnections(Collections.singletonList(SMS.getName()));
        configuration = new Configuration(application, options);

        assertThat(configuration.getPasswordlessMode(), is(PasswordlessMode.SMS_CODE));
    }

    @Test
    public void shouldNotFilterDefaultDBConnection() throws Exception {
        configuration = unfilteredConfig();
        assertThat(configuration.getDefaultDatabaseConnection(), hasName(USERNAME_PASSWORD_AUTHENTICATION));
    }

    @Test
    public void shouldHandleNoDBConnections() throws Exception {
        options.useDatabaseConnection(null);
        application = mock(Application.class);
        configuration = new Configuration(application, options);
        final AuthData connection = configuration.getDefaultDatabaseConnection();
        assertThat(connection, nullValue());
    }

    @Test
    public void shouldFilterDBConnection() throws Exception {
        configuration = filteredConfigBy(CUSTOM_DATABASE);
        assertThat(configuration.getDefaultDatabaseConnection(), hasName(CUSTOM_DATABASE));
    }

    @Test
    public void shouldReturnNullDBConnectionWhenNoneMatch() throws Exception {
        configuration = filteredConfigBy(UNKNOWN_CONNECTION);
        assertThat(configuration.getDefaultDatabaseConnection(), nullValue());
    }

    @Test
    public void shouldReturnSpecifiedDBConnectionWhenMoreThanOneDBConnectionIsAvailable() throws Exception {
        options.setConnections(Arrays.asList(CUSTOM_DATABASE, USERNAME_PASSWORD_AUTHENTICATION, RESTRICTIVE_DATABASE, UNKNOWN_CONNECTION));
        options.useDatabaseConnection(RESTRICTIVE_DATABASE);
        configuration = new Configuration(application, options);
        assertThat(configuration.getDefaultDatabaseConnection(), hasName(RESTRICTIVE_DATABASE));
    }

    @Test
    public void shouldReturnSpecifiedDBConnectionIfAvailable() throws Exception {
        options.setConnections(null);

        options.useDatabaseConnection(CUSTOM_DATABASE);
        configuration = new Configuration(application, options);
        assertThat(configuration.getDefaultDatabaseConnection(), hasName(CUSTOM_DATABASE));
    }

    @Test
    public void shouldIgnoreSpecifiedDBConnectionIfNotAvailable() throws Exception {
        options.setConnections(null);

        options.useDatabaseConnection("non-existing-db-connection");
        configuration = new Configuration(application, options);
        assertThat(configuration.getDefaultDatabaseConnection(), hasName(USERNAME_PASSWORD_AUTHENTICATION));
    }

    @Test
    public void shouldIgnoreSpecifiedDBConnectionIfFiltered() throws Exception {
        options.setConnections(Collections.singletonList(USERNAME_PASSWORD_AUTHENTICATION));

        options.useDatabaseConnection("non-existing-db-connection");
        configuration = new Configuration(application, options);
        assertThat(configuration.getDefaultDatabaseConnection(), hasName(USERNAME_PASSWORD_AUTHENTICATION));
    }

//    @Test
//    public void shouldReturnDefaultUnfilteredADConnection() throws Exception {
//        configuration = unfilteredConfig();
//        assertThat(configuration.getActiveDirectoryConnection(), notNullValue());
//        assertThat(configuration.getDefaultActiveDirectoryConnection(), hasName(MY_AD));
//    }

//    @Test
//    public void shouldReturnNullADConnectionIfNoneMatch() throws Exception {
//        configuration = filteredConfigBy(UNKNOWN_AD);
//        assertThat(configuration.getActiveDirectoryConnection(), nullValue());
//        assertThat(configuration.getDefaultActiveDirectoryConnection(), nullValue());
//    }

//    @Test
//    public void shouldReturnFilteredADConnections() throws Exception {
//        configuration = filteredConfigBy(MY_AD, MY_SECOND_AD);
//        final Strategy strategy = configuration.getActiveDirectoryConnection();
//        assertThat(strategy, notNullValue());
//        assertThat(configuration.getDefaultActiveDirectoryConnection(), hasName(MY_AD));
//        assertThat(strategy.getConnections(), containsInAnyOrder(hasName(MY_AD), hasName(MY_SECOND_AD)));
//    }

    @Test
    public void shouldReturnUnfilteredPasswordlessStrategies() throws Exception {
        configuration = unfilteredConfig();
        List<AuthData> connections = configuration.getPasswordlessConnections();
        assertThat(connections, is(notNullValue()));
        assertThat(connections, containsInAnyOrder(hasStrategy(Strategies.Email), hasStrategy(Strategies.SMS)));
    }

    @Test
    public void shouldReturnFilteredPasswordlessStrategies() throws Exception {
        configuration = filteredConfigBy(CUSTOM_PASSWORDLESS_CONNECTION);
        AuthData strategy = configuration.getDefaultPasswordlessConnection();
        assertThat(strategy, is(notNullValue()));
        assertThat(strategy, hasStrategy(Strategies.SMS));
        assertThat(strategy, hasName(CUSTOM_PASSWORDLESS_CONNECTION));
    }

//    @Test
//    public void shouldReturnFirstConnectionForStrategy() throws Exception {
//        configuration = unfilteredConfig();
//        AuthData smsStrategy = configuration.getPasswordlessConnections().get(0);
//        String name = configuration.getFirstConnectionOfStrategy(smsStrategy);
//        assertThat(name, is(equalTo(SMS.getName())));
//        assertThat(name, is(not(equalTo(CUSTOM_PASSWORDLESS_CONNECTION))));
//    }

    @Test
    public void shouldPreferEmailPasswordlessStrategy() throws Exception {
        configuration = unfilteredConfig();
        AuthData defaultConnection = configuration.getDefaultPasswordlessConnection();
        List<AuthData> connections = configuration.getPasswordlessConnections();
        assertThat(defaultConnection, is(notNullValue()));
        assertThat(defaultConnection.getName(), equalTo(Strategies.Email));
        assertThat(connections, containsInAnyOrder(hasStrategy(Strategies.Email), hasStrategy(Strategies.SMS)));
        assertThat(connections, hasSize(2));
    }

    @Test
    public void shouldReturnEmptyPasswordlessStrategiesIfNoneMatch() throws Exception {
        configuration = filteredConfigBy(Facebook.getName());
        AuthData connection = configuration.getDefaultPasswordlessConnection();
        assertThat(connection, is(nullValue()));
    }

    @Test
    public void shouldIgnoreStrategyNameAndReturnFilteredConnections() throws Exception {
        configuration = filteredConfigBy("twitter", "twitter-dev");
        final List<AuthData> strategies = configuration.getSocialConnections();
        assertThat(strategies, contains(hasStrategy("twitter"), hasStrategy("twitter")));
        assertThat(strategies, contains(hasName("twitter"), hasName("twitter-dev")));
        assertThat(strategies, hasSize(2));
    }

    @Test
    public void shouldNotReturnFilteredSocialStrategiesWithoutConnections() throws Exception {
        configuration = filteredConfigBy(Strategies.Facebook, Strategies.Linkedin);
        final List<AuthData> connections = configuration.getSocialConnections();
        assertThat(connections, hasItem(hasStrategy(Strategies.Facebook)));
        assertThat(connections, not(hasItem(hasStrategy(Strategies.Linkedin))));
    }

    @Test
    public void shouldReturnFilteredSocialStrategies() throws Exception {
        configuration = filteredConfigBy(Facebook.getName(), Instagram.getName());
        assertThat(configuration.getSocialConnections(), containsInAnyOrder(hasStrategy(Strategies.Facebook), hasStrategy(Strategies.Instagram)));
    }

    @Test
    public void shouldReturnEmptySocialStrategiesIfNoneMatch() throws Exception {
        configuration = filteredConfigBy(Yammer.getName(), Yahoo.getName());
        assertThat(configuration.getSocialConnections(), emptyIterable());
    }

    @Test
    public void shouldReturnUnfilteredEnterpriseConnections() throws Exception {
        configuration = unfilteredConfig();
        assertThat(configuration.getEnterpriseConnections(), containsInAnyOrder(hasStrategy(Strategies.ActiveDirectory), hasStrategy(Strategies.GoogleApps)));
    }

    @Test
    public void shouldReturnFilteredEnterpriseStrategies() throws Exception {
        configuration = filteredConfigBy("auth0.com");
        assertThat(configuration.getEnterpriseConnections(), contains(hasStrategy(Strategies.GoogleApps)));
    }

    @Test
    public void shouldReturnEmptyEnterpriseStrategiesIfNoneMatch() throws Exception {
        configuration = filteredConfigBy(Yandex.getName());
        assertThat(configuration.getEnterpriseConnections(), emptyIterable());
    }

//    @Test
//    public void shouldUseNativeAuthentication() throws Exception {
//        configuration = filteredConfigBy(MY_AD, MY_SECOND_AD);
//        final AuthData connection = configuration.getDefaultActiveDirectoryConnection();
//        assertThat(configuration.shouldUseNativeAuthentication(connection, new ArrayList<String>()), is(true));
//    }
//
//    @Test
//    public void shouldNotUseNativeAuthenticationBecauseOverrided() throws Exception {
//        configuration = filteredConfigBy(MY_AD, MY_SECOND_AD);
//        final AuthData connection = configuration.getDefaultActiveDirectoryConnection();
//        assertThat(configuration.shouldUseNativeAuthentication(connection, Arrays.asList(MY_AD, MY_SECOND_AD)), is(false));
//    }
//
//    @Test
//    public void shouldNotUseNativeAuthenticationBecauseIsSocial() throws Exception {
//        configuration = unfilteredConfig();
//        final AuthData connection = getConnectionByName("twitter");
//        assertThat(configuration.shouldUseNativeAuthentication(connection, new ArrayList<String>()), is(false));
//    }

    @Test
    public void shouldHaveDefaultPrivacyPolicyURL() throws Exception {
        configuration = unfilteredConfig();
        assertThat(configuration.getPrivacyURL(), is(notNullValue()));
        assertThat(configuration.getPrivacyURL(), is(equalTo("https://auth0.com/privacy")));
    }

    @Test
    public void shouldHaveCustomPrivacyPolicyURL() throws Exception {
        options.setPrivacyURL("https://google.com/privacy");
        configuration = new Configuration(application, options);
        assertThat(configuration.getPrivacyURL(), is(notNullValue()));
        assertThat(configuration.getPrivacyURL(), is(equalTo("https://google.com/privacy")));
    }

    @Test
    public void shouldHaveDefaultTermsOfServiceURL() throws Exception {
        configuration = unfilteredConfig();
        assertThat(configuration.getTermsURL(), is(notNullValue()));
        assertThat(configuration.getTermsURL(), is(equalTo("https://auth0.com/terms")));
    }

    @Test
    public void shouldHaveCustomTermsOfServiceURL() throws Exception {
        options.setTermsURL("https://google.com/terms");
        configuration = new Configuration(application, options);
        assertThat(configuration.getTermsURL(), is(notNullValue()));
        assertThat(configuration.getTermsURL(), is(equalTo("https://google.com/terms")));
    }

    @Test
    public void shouldHaveMustAcceptTermsEnabled() throws Exception {
        options.setMustAcceptTerms(true);
        configuration = new Configuration(application, options);
        assertThat(configuration.mustAcceptTerms(), is(true));
    }

    private Configuration unfilteredConfig() {
        return new Configuration(application, options);
    }

    private Configuration filteredConfigBy(String... names) {
        options.setConnections(Arrays.asList(names));
        return new Configuration(application, options);
    }

    private AuthData getConnectionByName(String name) {
        for (AuthData connection : application.getConnections()) {
            if (connection.getName().equals(name)) {
                return connection;
            }
        }
        return null;
    }

    private List<CustomField> createCustomFields() {
        CustomField fieldNumber = new CustomField(R.drawable.com_auth0_lock_ic_phone, FieldType.TYPE_PHONE_NUMBER, "number", R.string.com_auth0_lock_hint_phone_number);
        CustomField fieldSurname = new CustomField(R.drawable.com_auth0_lock_ic_username, FieldType.TYPE_NAME, "surname", R.string.com_auth0_lock_hint_username);

        List<CustomField> customFields = new ArrayList<>();
        customFields.add(fieldNumber);
        customFields.add(fieldSurname);
        return customFields;
    }
}