/*
 * ConfigurationTest.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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

package com.auth0.lock;

import com.auth0.android.*;
import com.auth0.core.Application;
import com.auth0.core.Connection;
import com.auth0.core.Strategies;
import com.auth0.core.Strategy;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import static com.auth0.core.Strategies.ActiveDirectory;
import static com.auth0.core.Strategies.Facebook;
import static com.auth0.core.Strategies.GoogleApps;
import static com.auth0.core.Strategies.GooglePlus;
import static com.auth0.core.Strategies.Instagram;
import static com.auth0.core.Strategies.Twitter;
import static com.auth0.core.Strategies.Yahoo;
import static com.auth0.core.Strategies.Yammer;
import static com.auth0.core.Strategies.Yandex;
import static com.auth0.lock.util.ConnectionMatcher.isConnection;
import static com.auth0.lock.util.StrategyMatcher.isStrategy;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.emptyIterable;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = com.auth0.android.BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class ConfigurationTest {

    private static final String CUSTOM_DATABASE = "CustomDatabase";
    private static final String USERNAME_PASSWORD_AUTHENTICATION = "Username-Password-Authentication";
    private static final String MY_AD = "MyAD";
    private static final String MY_SECOND_AD = "mySecondAD";
    private Configuration configuration;

    private Application application;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ObjectMapper mapper = new ObjectMapper();
        application = mapper.readValue(new File("src/test/resources/appinfo.json"), Application.class);
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
        configuration = new Configuration(application, null, null);
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
        configuration = filteredConfigBy("UnknownConnection");
        assertThat(configuration.getDefaultDatabaseConnection(), nullValue());
    }

    @Test
    public void shouldReturnSpecifiedDBConnection() throws Exception {
        configuration = new Configuration(application, Arrays.asList(CUSTOM_DATABASE, USERNAME_PASSWORD_AUTHENTICATION), USERNAME_PASSWORD_AUTHENTICATION);
        assertThat(configuration.getDefaultDatabaseConnection(), isConnection(USERNAME_PASSWORD_AUTHENTICATION));

        configuration = new Configuration(application, null, CUSTOM_DATABASE);
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
        configuration = filteredConfigBy("UnknownAD");
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

    private Configuration unfilteredConfig() {
        return new Configuration(application, null, null);
    }

    private Configuration filteredConfigBy(String ...names) {
        return new Configuration(application, Arrays.asList(names), null);
    }
}