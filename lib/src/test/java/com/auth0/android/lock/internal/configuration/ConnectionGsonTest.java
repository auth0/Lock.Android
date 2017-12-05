/*
 * StrategyGsonTest.java
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

package com.auth0.android.lock.internal.configuration;

<<<<<<< Updated upstream
=======
import com.auth0.android.lock.views.next.configuration.internal.Connection;
>>>>>>> Stashed changes
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import static com.auth0.android.lock.internal.configuration.ConnectionMatcher.hasType;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class ConnectionGsonTest extends GsonBaseTest {
    private static final String STRATEGY = "src/test/resources/strategy.json";
    private static final String ENTERPRISE_CONNECTION = "src/test/resources/enterprise_connection.json";
    private static final String DATABASE_CONNECTION = "src/test/resources/db_connection.json";
    private static final String SOCIAL_CONNECTION = "src/test/resources/social_connection.json";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        gson = createGson();
    }

    @Test
    public void shouldFailWithEmptyJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildConnectionsFrom(json(EMPTY_OBJECT));
    }

    @Test
    public void shouldFailWithInvalidJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildConnectionsFrom(json(INVALID));
    }

    @Test
    public void shouldRequireName() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildConnectionsFrom(new StringReader("{\"connections\": \"[]\"}"));
    }

    @Test
    public void shouldRequireConnections() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildConnectionsFrom(new StringReader("{\"name\": \"auth0\"}"));
    }

    @Test
    public void shouldReturnStrategy() throws Exception {
        final List<Connection> connections = buildConnectionsFrom(json(STRATEGY));
        assertThat(connections, is(notNullValue()));
        assertThat(connections, hasSize(1));
        assertThat(connections.get(0).getName(), is("twitter"));
        assertThat(connections.get(0).getStrategy(), is("twitter"));
    }

    @Test
    public void shouldReturnConnectionName() throws Exception {
        final List<Connection> connections = buildConnectionsFrom(json(STRATEGY));
        assertThat(connections, hasSize(1));
        assertThat(connections.get(0), is(notNullValue()));
        assertThat(connections.get(0).getName(), is("twitter"));
    }

    @Test
    public void shouldReturnEnterpriseConnections() throws Exception {
        final List<Connection> connections = buildConnectionsFrom(json(ENTERPRISE_CONNECTION));
        assertThat(connections, hasSize(1));
        assertThat(connections.get(0), is(notNullValue()));
        assertThat(connections.get(0), hasType(AuthType.ENTERPRISE));
        assertThat(connections.get(0).getName(), is("ad"));
        assertThat(connections.get(0).getDomainSet(), contains("auth10.com"));
        assertThat(connections.get(0).valueForKey("domain", String.class), is("auth10.com"));
        assertThat((List<String>) connections.get(0).valueForKey("domain_aliases", List.class), hasItem("auth10.com"));
    }

    @Test
    public void shouldReturnSocial() throws Exception {
        final List<Connection> connections = buildConnectionsFrom(json(SOCIAL_CONNECTION));
        assertThat(connections, hasSize(1));
        assertThat(connections.get(0), is(notNullValue()));
        assertThat(connections.get(0), hasType(AuthType.SOCIAL));
        assertThat(connections.get(0).getName(), is("twitter"));
        assertThat(connections.get(0).getStrategy(), is("twitter"));
        assertThat(connections.get(0).valueForKey("scope", String.class), is("public_profile"));
    }

    @Test
    public void shouldReturnDatabase() throws Exception {
        final List<Connection> connections = buildConnectionsFrom(json(DATABASE_CONNECTION));
        assertThat(connections.get(0), is(notNullValue()));
        assertThat(connections.get(0), hasType(AuthType.DATABASE));
        assertThat(connections.get(0).getName(), is("Username-Password-Authentication"));
        assertThat(connections.get(0).getStrategy(), is("auth0"));
<<<<<<< Updated upstream
        assertThat(connections.get(0).valueForKey("forgot_password_url", String.class), is("https://login.auth0.com/lo/forgot?wtrealm=urn:auth0:samples:Username-Password-Authentication"));
        assertThat(connections.get(0).valueForKey("signup_url", String.class), is("https://login.auth0.com/lo/signup?wtrealm=urn:auth0:samples:Username-Password-Authentication"));
=======
        assertThat(connections.get(0).valueForKey("forgot_password_url", String.class), is("https://realmLogin.auth0.com/lo/forgot?wtrealm=urn:auth0:samples:Username-Password-Authentication"));
        assertThat(connections.get(0).valueForKey("signup_url", String.class), is("https://realmLogin.auth0.com/lo/signup?wtrealm=urn:auth0:samples:Username-Password-Authentication"));
>>>>>>> Stashed changes
        assertThat(connections.get(0).showSignUp(), is(true));
        assertThat(connections.get(0).showForgot(), is(true));
        assertThat(connections.get(0).requiresUsername(), is(false));
        assertThat(connections.get(0).getPasswordPolicy(), is(PasswordStrength.GOOD));
    }


    private List<Connection> buildConnectionsFrom(Reader json) throws IOException {
        final TypeToken<List<Connection>> strategyType = new TypeToken<List<Connection>>() {};
        return pojoFrom(json, strategyType);
    }

}
