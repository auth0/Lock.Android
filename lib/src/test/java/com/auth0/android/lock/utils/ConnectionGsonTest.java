/*
 * CredentialsGsonTest.java
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

package com.auth0.android.lock.utils;

import com.auth0.android.lock.utils.json.JsonUtils;
import com.google.gson.JsonParseException;

import org.hamcrest.core.IsCollectionContaining;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ConnectionGsonTest extends GsonBaseTest {
    private static final String ENTERPRISE_CONNECTION = "src/test/resources/enterprise_connection.json";
    private static final String DATABASE_CONNECTION = "src/test/resources/db_connection.json";
    private static final String SOCIAL_CONNECTION = "src/test/resources/social_connection.json";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        gson = JsonUtils.createGson();
    }

    @Test
    public void shouldFailWithEmptyJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildConnectionFrom(json(EMPTY_OBJECT));
    }

    @Test
    public void shouldFailWithInvalidJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildConnectionFrom(json(INVALID));
    }

    @Test
    public void shouldRequireName() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildConnectionFrom(new StringReader("{\"domain\": \"auth10.com\"}"));
    }

    @Test
    public void shouldReturnEnterprise() throws Exception {
        final Connection connection = buildConnectionFrom(json(ENTERPRISE_CONNECTION));
        assertThat(connection, is(notNullValue()));
        assertThat(connection.getName(), is("ad"));
        assertThat(connection.getValues(), is(notNullValue()));
        assertThat(connection.getValues().isEmpty(), is(false));
        assertThat((String) connection.getValues().get("domain"), is("auth10.com"));
        assertThat((List<String>) connection.getValues().get("domain_aliases"), IsCollectionContaining.hasItem("auth10.com"));
    }

    @Test
    public void shouldReturnSocial() throws Exception {
        final Connection connection = buildConnectionFrom(json(SOCIAL_CONNECTION));
        assertThat(connection, is(notNullValue()));
        assertThat(connection.getName(), is("twitter"));
        assertThat(connection.getValues(), is(notNullValue()));
        assertThat(connection.getValues().isEmpty(), is(false));
        assertThat((String) connection.getValues().get("scope"), is("public_profile"));
    }

    @Test
    public void shouldReturnDatabase() throws Exception {
        final Connection connection = buildConnectionFrom(json(DATABASE_CONNECTION));
        assertThat(connection, is(notNullValue()));
        assertThat(connection.getName(), is("Username-Password-Authentication"));
        assertThat(connection.getValues(), is(notNullValue()));
        assertThat(connection.getValues().isEmpty(), is(false));
        assertThat((String) connection.getValues().get("forgot_password_url"), is("https://login.auth0.com/lo/forgot?wtrealm=urn:auth0:samples:Username-Password-Authentication"));
        assertThat((String) connection.getValues().get("signup_url"), is("https://login.auth0.com/lo/signup?wtrealm=urn:auth0:samples:Username-Password-Authentication"));
        assertThat(connection.booleanForKey("showSignup"), is(true));
        assertThat(connection.booleanForKey("showForgot"), is(true));
        assertThat(connection.booleanForKey("requires_username"), is(false));
    }

    private Connection buildConnectionFrom(Reader json) throws IOException {
        return pojoFrom(json, Connection.class);
    }

}
