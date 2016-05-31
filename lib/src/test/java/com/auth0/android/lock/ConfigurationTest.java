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
import com.auth0.android.lock.enums.PasswordlessMode;
import com.auth0.android.lock.enums.UsernameStyle;
import com.auth0.android.lock.utils.Application;
import com.auth0.android.lock.utils.Connection;
import com.auth0.android.lock.utils.Strategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.auth0.android.lock.utils.ConnectionMatcher.isConnection;
import static com.auth0.android.lock.utils.Strategies.ActiveDirectory;
import static com.auth0.android.lock.utils.Strategies.Email;
import static com.auth0.android.lock.utils.Strategies.Facebook;
import static com.auth0.android.lock.utils.Strategies.GoogleApps;
import static com.auth0.android.lock.utils.Strategies.GooglePlus;
import static com.auth0.android.lock.utils.Strategies.Instagram;
import static com.auth0.android.lock.utils.Strategies.SMS;
import static com.auth0.android.lock.utils.Strategies.Twitter;
import static com.auth0.android.lock.utils.Strategies.Yahoo;
import static com.auth0.android.lock.utils.Strategies.Yammer;
import static com.auth0.android.lock.utils.Strategies.Yandex;
import static com.auth0.android.lock.utils.StrategyMatcher.isStrategy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ConfigurationTest {

    private static final String RESTRICTIVE_DATABASE = "RestrictiveDatabase";
    private static final String CUSTOM_DATABASE = "CustomDatabase";
    private static final String USERNAME_PASSWORD_AUTHENTICATION = "Username-Password-Authentication";
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
        ObjectMapper mapper = new ObjectMapper();
        application = mapper.readValue(new File("src/test/resources/appinfo.json"), Application.class);
        options = new Options();
    }

    @Test
    public void shouldKeepApplicationDefaultsIfOptionsAreNotModified() throws Exception {
        configuration = new Configuration(application, options);
        assertThat(configuration.isUsernameRequired(), is(false));
        assertThat(configuration.isSignUpEnabled(), is(true));
        assertThat(configuration.isChangePasswordEnabled(), is(true));
        assertThat(configuration.loginAfterSignUp(), is(true));
        assertThat(configuration.getUsernameStyle(), is(equalTo(UsernameStyle.DEFAULT)));
        assertThat(configuration.hasExtraFields(), is(false));
    }

    @Test
    public void shouldMergeApplicationWithOptionsIfDefaultDatabaseExists() throws Exception {
        options.setConnections(Collections.singletonList(USERNAME_PASSWORD_AUTHENTICATION));
        options.setAllowSignUp(false);
        options.setAllowForgotPassword(false);
        options.setLoginAfterSignUp(false);
        options.setUsernameStyle(UsernameStyle.USERNAME);
        configuration = new Configuration(application, options);
        assertThat(configuration.isUsernameRequired(), is(false));
        assertThat(configuration.isSignUpEnabled(), is(false));
        assertThat(configuration.isChangePasswordEnabled(), is(false));
        assertThat(configuration.loginAfterSignUp(), is(false));
        assertThat(configuration.getUsernameStyle(), is(equalTo(UsernameStyle.USERNAME)));
        assertThat(configuration.hasExtraFields(), is(false));
    }

    @Test
    public void shouldNotMergeApplicationWithOptionsIfApplicationIsRestrictive() throws Exception {
        options.setConnections(Collections.singletonList(RESTRICTIVE_DATABASE));
        options.setAllowSignUp(true);
        options.setAllowForgotPassword(true);
        configuration = new Configuration(application, options);
        assertThat(configuration.isSignUpEnabled(), is(false));
        assertThat(configuration.isChangePasswordEnabled(), is(false));
    }

    @Test
    public void shouldSetExtraSignUpFields() throws Exception {
        options.setCustomFields(createCustomFields());
        configuration = new Configuration(application, options);

        assertThat(configuration.hasExtraFields(), is(true));
        assertThat(configuration.getExtraSignUpFields(), contains(options.getCustomFields().toArray()));
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
        assertThat(configuration.getDefaultDatabaseConnection(), isConnection(USERNAME_PASSWORD_AUTHENTICATION));
    }

    @Test
    public void shouldHandleNoDBConnections() throws Exception {
        application = mock(Application.class);
        when(application.getDatabaseStrategy()).thenReturn(null);
        configuration = new Configuration(application, options);
        final Connection connection = configuration.getDefaultDatabaseConnection();
        assertThat(connection, nullValue());
    }

    @Test
    public void shouldFilterDBConnection() throws Exception {
        configuration = filteredConfigBy(CUSTOM_DATABASE);
        assertThat(configuration.getDefaultDatabaseConnection(), isConnection(CUSTOM_DATABASE));
    }

    @Test
    public void shouldReturnNullDBConnectionWhenNoneMatch() throws Exception {
        configuration = filteredConfigBy(UNKNOWN_CONNECTION);
        assertThat(configuration.getDefaultDatabaseConnection(), nullValue());
    }

    @Test
    public void shouldReturnSpecifiedDBConnection() throws Exception {
        options.setConnections(Arrays.asList(CUSTOM_DATABASE, USERNAME_PASSWORD_AUTHENTICATION));
        options.useDatabaseConnection(USERNAME_PASSWORD_AUTHENTICATION);
        configuration = new Configuration(application, options);
        assertThat(configuration.getDefaultDatabaseConnection(), isConnection(USERNAME_PASSWORD_AUTHENTICATION));

        options.setConnections(null);
        options.useDatabaseConnection(CUSTOM_DATABASE);
        configuration = new Configuration(application, options);
        assertThat(configuration.getDefaultDatabaseConnection(), isConnection(CUSTOM_DATABASE));
    }

    @Test
    public void shouldReturnDefaultUnfilteredADConnection() throws Exception {
        configuration = unfilteredConfig();
        assertThat(configuration.getActiveDirectoryStrategy(), notNullValue());
        assertThat(configuration.getDefaultActiveDirectoryConnection(), isConnection(MY_AD));
    }

    @Test
    public void shouldReturnNullADConnectionIfNoneMatch() throws Exception {
        configuration = filteredConfigBy(UNKNOWN_AD);
        assertThat(configuration.getActiveDirectoryStrategy(), nullValue());
        assertThat(configuration.getDefaultActiveDirectoryConnection(), nullValue());
    }

    @Test
    public void shouldReturnFilteredADConnections() throws Exception {
        configuration = filteredConfigBy(MY_AD, MY_SECOND_AD);
        final Strategy strategy = configuration.getActiveDirectoryStrategy();
        assertThat(strategy, notNullValue());
        assertThat(configuration.getDefaultActiveDirectoryConnection(), isConnection(MY_AD));
        assertThat(strategy.getConnections(), containsInAnyOrder(isConnection(MY_AD), isConnection(MY_SECOND_AD)));
    }

    @Test
    public void shouldReturnUnfilteredPasswordlessStrategies() throws Exception {
        configuration = unfilteredConfig();
        List<Strategy> strategies = configuration.getPasswordlessStrategies();
        assertThat(strategies, is(notNullValue()));
        assertThat(strategies, containsInAnyOrder(isStrategy(Email), isStrategy(SMS)));
    }

    @Test
    public void shouldReturnFilteredPasswordlessStrategies() throws Exception {
        configuration = filteredConfigBy(CUSTOM_PASSWORDLESS_CONNECTION);
        Strategy strategy = configuration.getDefaultPasswordlessStrategy();
        assertThat(strategy, is(notNullValue()));
        assertThat(strategy, isStrategy(SMS));
        assertThat(strategy.getConnections(), hasSize(1));
        assertThat(strategy.getConnections().get(0), isConnection(CUSTOM_PASSWORDLESS_CONNECTION));
    }

    @Test
    public void shouldReturnFirstConnectionForStrategy() throws Exception {
        configuration = unfilteredConfig();
        Strategy smsStrategy = configuration.getPasswordlessStrategies().get(0);
        String name = configuration.getFirstConnectionOfStrategy(smsStrategy);
        assertThat(name, is(equalTo(SMS.getName())));
        assertThat(name, is(not(equalTo(CUSTOM_PASSWORDLESS_CONNECTION))));
    }

    @Test
    public void shouldPreferEmailPasswordlessStrategy() throws Exception {
        configuration = unfilteredConfig();
        Strategy strategy = configuration.getDefaultPasswordlessStrategy();
        List<Strategy> strategies = configuration.getPasswordlessStrategies();
        assertThat(strategy, is(notNullValue()));
        assertThat(strategy.getName(), equalTo(Email.getName()));
        assertThat(strategies, containsInAnyOrder(isStrategy(Email), isStrategy(SMS)));
        assertThat(strategies, hasSize(2));
    }

    @Test
    public void shouldReturnEmptyPasswordlessStrategiesIfNoneMatch() throws Exception {
        configuration = filteredConfigBy(Facebook.getName());
        Strategy strategy = configuration.getDefaultPasswordlessStrategy();
        assertThat(strategy, is(nullValue()));
    }

    @Test
    public void shouldReturnUnfilteredSocialStrategies() throws Exception {
        configuration = unfilteredConfig();
        final List<Strategy> strategies = configuration.getSocialStrategies();
        assertThat(strategies, containsInAnyOrder(isStrategy(Facebook), isStrategy(Twitter), isStrategy(Instagram), isStrategy(GooglePlus)));
    }

    @Test
    public void shouldReturnFilteredSocialStrategies() throws Exception {
        configuration = filteredConfigBy(Facebook.getName(), Instagram.getName());
        assertThat(configuration.getSocialStrategies(), containsInAnyOrder(isStrategy(Facebook), isStrategy(Instagram)));
    }

    @Test
    public void shouldReturnEmptySocialStrategiesIfNoneMatch() throws Exception {
        configuration = filteredConfigBy(Yammer.getName(), Yahoo.getName());
        assertThat(configuration.getSocialStrategies(), emptyIterable());
    }

    @Test
    public void shouldReturnUnfilteredEnterpriseConnections() throws Exception {
        configuration = unfilteredConfig();
        assertThat(configuration.getEnterpriseStrategies(), containsInAnyOrder(isStrategy(ActiveDirectory), isStrategy(GoogleApps)));
    }

    @Test
    public void shouldReturnFilteredEnterpriseStrategies() throws Exception {
        configuration = filteredConfigBy("auth0.com");
        assertThat(configuration.getEnterpriseStrategies(), contains(isStrategy(GoogleApps)));
    }

    @Test
    public void shouldReturnEmptyEnterpriseStrategiesIfNoneMatch() throws Exception {
        configuration = filteredConfigBy(Yandex.getName());
        assertThat(configuration.getEnterpriseStrategies(), emptyIterable());
    }

    @Test
    public void shouldUseNativeAuthentication() throws Exception {
        configuration = filteredConfigBy(MY_AD, MY_SECOND_AD);
        final Connection connection = configuration.getDefaultActiveDirectoryConnection();
        assertThat(configuration.shouldUseNativeAuthentication(connection, new ArrayList<String>()), is(true));
    }

    @Test
    public void shouldNotUseNativeAuthenticationBecauseOverrided() throws Exception {
        configuration = filteredConfigBy(MY_AD, MY_SECOND_AD);
        final Connection connection = configuration.getDefaultActiveDirectoryConnection();
        assertThat(configuration.shouldUseNativeAuthentication(connection, Arrays.asList(MY_AD, MY_SECOND_AD)), is(false));
    }

    @Test
    public void shouldNotUseNativeAuthenticationBecauseIsSocial() throws Exception {
        configuration = unfilteredConfig();
        final Connection connection = getConnectionByName("twitter");
        assertThat(configuration.shouldUseNativeAuthentication(connection, new ArrayList<String>()), is(false));
    }

    private Configuration unfilteredConfig() {
        return new Configuration(application, options);
    }

    private Configuration filteredConfigBy(String... names) {
        options.setConnections(Arrays.asList(names));
        return new Configuration(application, options);
    }

    private Connection getConnectionByName(String name) {
        for (Strategy strategy : application.getStrategies()) {
            for (Connection connection : strategy.getConnections()) {
                if (connection.getName().equals(name)) {
                    return connection;
                }
            }
        }
        return null;
    }

    private List<CustomField> createCustomFields() {
        CustomField fieldNumber = new CustomField(R.drawable.com_auth0_lock_ic_phone, FieldType.TYPE_PHONE_NUMBER, "number", R.string.com_auth0_lock_hint_phone_number);
        CustomField fieldSurname = new CustomField(R.drawable.com_auth0_lock_ic_username, FieldType.TYPE_TEXT_NAME, "surname", R.string.com_auth0_lock_hint_username);

        List<CustomField> customFields = new ArrayList<>();
        customFields.add(fieldNumber);
        customFields.add(fieldSurname);
        return customFields;
    }
}