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

import com.auth0.core.Application;
import com.auth0.core.Connection;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.File;
import java.util.Arrays;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class ConfigurationTest {

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
        final Connection connection = configuration.getDefaultDatabaseConnection();
        assertThat(connection, notNullValue());
        assertThat(connection.getName(), equalTo("Username-Password-Authentication"));
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
        configuration = filteredConfigBy("CustomDatabase");
        final Connection connection = configuration.getDefaultDatabaseConnection();
        assertThat(connection, notNullValue());
        assertThat(connection.getName(), equalTo("CustomDatabase"));
    }


    @Test
    public void shouldReturnNullDBConnectionWhenNoneMatch() throws Exception {
        configuration = new Configuration(application, Arrays.asList("CustomDatabase"), "Username-Password-Authentication");
        Connection connection = configuration.getDefaultDatabaseConnection();
        assertThat(connection, notNullValue());
        assertThat(connection.getName(), equalTo("Username-Password-Authentication"));

        configuration = new Configuration(application, null, "CustomDatabase");
        connection = configuration.getDefaultDatabaseConnection();
        assertThat(connection, notNullValue());
        assertThat(connection.getName(), equalTo("CustomDatabase"));

    }

    @Test
    public void shouldReturnSpecifiedDBConnection() throws Exception {
        configuration = filteredConfigBy("CustomDatabase");
        final Connection connection = configuration.getDefaultDatabaseConnection();
        assertThat(connection, notNullValue());
        assertThat(connection.getName(), equalTo("CustomDatabase"));
    }

    private Configuration unfilteredConfig() {
        return new Configuration(application, null, null);
    }

    private Configuration filteredConfigBy(String ...names) {
        return new Configuration(application, Arrays.asList(names), null);
    }
}