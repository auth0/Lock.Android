/*
 * EnterpriseConnectionMatcherTest.java
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

import com.auth0.android.lock.utils.EnterpriseConnectionMatcher;
<<<<<<< Updated upstream
=======
import com.auth0.android.lock.views.next.configuration.internal.Connection;
import com.auth0.android.lock.views.next.configuration.internal.OAuthConnection;
>>>>>>> Stashed changes

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

<<<<<<< Updated upstream
import static com.auth0.android.lock.internal.configuration.Connection.newConnectionFor;
=======
import static com.auth0.android.lock.views.next.configuration.internal.Connection.newConnectionFor;
>>>>>>> Stashed changes
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class EnterpriseConnectionMatcherTest {

    private static final String ENTERPRISE_STRATEGY = "adfs";
    private static final String NAME_KEY = "name";
    private static final String DOMAIN_KEY = "domain";
    private static final String DOMAIN_ALIASES_KEY = "domain_aliases";
    private static final String SAMPLE_VALID_EMAIL = "username@pep.com";
    private static final String SAMPLE_UNKNOWN_EMAIL = "username@unknown.net";
    private static final String SAMPLE_INVALID_EMAIL = "usern!p_epom";
    private static final String NAME_VALUE = "default";
    private static final String DOMAIN_VALUE = "pepe.com";
    private final List<String> DOMAIN_ALIASES_VALUE = new ArrayList<>(Arrays.asList(DOMAIN_VALUE, "pep.com", "pe.pe"));

    private EnterpriseConnectionMatcher parser;

    @Before
    public void setUp() throws Exception {
        List<OAuthConnection> connections = new ArrayList<>();
        OAuthConnection connection = createConnection();
        connections.add(connection);
        parser = new EnterpriseConnectionMatcher(connections);
    }

    @Test
    public void shouldExtractTheUsername() throws Exception {
        String username = parser.extractUsername(SAMPLE_VALID_EMAIL);
        assertThat(username, is(equalTo("username")));
    }

    @Test
    public void shouldParseTheConnection() throws Exception {
        OAuthConnection connection = parser.parse(SAMPLE_VALID_EMAIL);
        assertThat(connection, is(notNullValue()));
        assertThat(connection.getName(), is(equalTo(NAME_VALUE)));
        assertThat(connection.valueForKey(DOMAIN_KEY, String.class), is(equalTo(DOMAIN_VALUE)));
        assertThat((List<String>) connection.valueForKey(DOMAIN_ALIASES_KEY, List.class),
                is(equalTo(DOMAIN_ALIASES_VALUE)));
    }

    @Test
    public void shouldNotFindAnUnknownDomain() throws Exception {
        OAuthConnection connection = parser.parse(SAMPLE_UNKNOWN_EMAIL);
        assertThat(connection, is(nullValue()));
    }

    @Test
    public void shouldReturnTheMainDomain() throws Exception {
        OAuthConnection connection = parser.parse(SAMPLE_VALID_EMAIL);
        assertThat(parser.domainForConnection(connection), is(equalTo(DOMAIN_VALUE)));
    }

    @Test
    public void shouldFailToGetConnectionIfNotValidDomain() throws Exception {
        OAuthConnection connection = parser.parse(SAMPLE_INVALID_EMAIL);
        assertThat(connection, is(nullValue()));
    }

    @Test
    public void shouldFailToGetConnectionIfInstantiatedWithEmptyStrategies() throws Exception {
        EnterpriseConnectionMatcher parser = new EnterpriseConnectionMatcher(new ArrayList<OAuthConnection>());
        OAuthConnection connection = parser.parse(SAMPLE_VALID_EMAIL);
        assertThat(connection, is(nullValue()));
    }

    private Connection createConnection() {
        Map<String, Object> map = new HashMap<>();
        map.put(NAME_KEY, NAME_VALUE);
        map.put(DOMAIN_KEY, DOMAIN_VALUE);
        map.put(DOMAIN_ALIASES_KEY, DOMAIN_ALIASES_VALUE);

        return newConnectionFor(ENTERPRISE_STRATEGY, map);
    }
}