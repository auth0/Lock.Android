/*
 * StrategyTest.java
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

package com.auth0.android.lock.utils.json;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class AuthDataTest {

    @Before
    public void setUp() throws Exception {
    }
//
//    @Test
//    public void shouldReturnStrategyNameWhenNoConnectionsAndTypeSocial() throws Exception {
//        AuthData connection = connectionForStrategy("facebook");
//        assertThat(connection.getName(), is("facebook"));
//        assertThat(connection.getConnections().isEmpty(), is(true));
//    }
//
//    @Test
//    public void shouldReturnNullWhenNoConnectionsAndTypeEnterprise() throws Exception {
//        AuthData connection = connectionForStrategy("adfs");
//        assertThat(connection.getDefaultConnectionName(), is(nullValue()));
//        assertThat(connection.getConnections().isEmpty(), is(true));
//    }
//
//    @Test
//    public void shouldReturnNullWhenNoConnectionsAndTypeDatabase() throws Exception {
//        AuthData connection = connectionForStrategy("auth0");
//        assertThat(connection.getDefaultConnectionName(), is(nullValue()));
//        assertThat(connection.getConnections().isEmpty(), is(true));
//    }
//
//    @Test
//    public void shouldReturnNullWhenNoConnectionsAndTypePasswordless() throws Exception {
//        AuthData connection = connectionForStrategy("sms");
//        assertThat(connection.getDefaultConnectionName(), is(nullValue()));
//        assertThat(connection.getConnections().isEmpty(), is(true));
//    }

    public static final String CONNECTION_NAME = "Username-Password";
    public static final Object VALUE = "value";
    public static final String KEY = "key";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldBuildConnectionWithName() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        AuthData connection = new AuthData("strategy", values);
        assertNotNull(connection);
        assertThat(connection.getStrategy(), equalTo("strategy"));
        assertThat(connection.getName(), equalTo(CONNECTION_NAME));
    }

    @Test
    public void shouldBuildConnectionWithValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        AuthData connection = new AuthData("strategy", values);
        assertThat(connection.getValueForKey(KEY), is(VALUE));
    }

    @Test
    public void shouldNotStoreNameInValues() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        AuthData connection = new AuthData("strategy", values);
        assertThat(connection.getValueForKey("name"), is(nullValue()));
    }

    @Test
    public void shouldRaiseExceptionWhenNameIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        Map<String, Object> values = null;
        new AuthData("strategy", values);
    }

    @Test
    public void shouldNotHaveResourceOwnerEnabledByDefault() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        AuthData connection = new AuthData("strategy", values);
        assertThat(connection.isActiveFlowEnabled(), is(false));
    }

    @Test
    public void shouldReturnValueFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        AuthData connection = new AuthData("strategy", values);
        String value = connection.getValueForKey(KEY);
        assertThat(value, equalTo(VALUE));
    }

    @Test
    public void shouldReturnBooleanValueFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, true);
        AuthData connection = new AuthData("strategy", values);
        boolean value = connection.booleanForKey(KEY);
        assertThat(value, is(true));
    }

    @Test
    public void shouldReturnDefaultBooleanValueFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        AuthData connection = new AuthData("strategy", values);
        boolean value = connection.booleanForKey(KEY);
        assertThat(value, is(false));
    }

    @Test
    public void shouldRaiseExceptionWhenValueIsNotBoolean() {
        expectedException.expect(ClassCastException.class);
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        AuthData connection = new AuthData("strategy", values);
        connection.booleanForKey(KEY);
    }

    @Test
    public void shouldReturnDomainNameInSet() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put("domain", "domain.com");
        AuthData connection = new AuthData("strategy", values);
        assertThat(connection.getDomainSet(), hasItem("domain.com"));
    }

    @Test
    public void shouldReturnAllDomainNamesAsSet() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put("domain", "domain.com");
        values.put("domain_aliases", Arrays.asList("domain2.com", "domain3.com"));
        AuthData connection = new AuthData("strategy", values);
        assertThat(connection.getDomainSet(), hasItems("domain.com", "domain2.com", "domain3.com"));
    }

    @Test
    public void shouldReturnEmptySetWithNoDomainName() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        AuthData connection = new AuthData("strategy", values);
        assertThat(connection.getDomainSet().isEmpty(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfADFS() throws Exception {
        AuthData connection = connectionForStrategy("adfs");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfWaad() throws Exception {
        AuthData connection = connectionForStrategy("waad");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfActiveDirectory() throws Exception {
        AuthData connection = connectionForStrategy("ad");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldNotHaveResourceOwnerEnabledIfNotADFSWaadOrActiveDirectory() throws Exception {
        AuthData connectionAuth0LDAP = connectionForStrategy("auth0-adldap");
        AuthData connectionCustom = connectionForStrategy("custom");
        AuthData connectionGoogleApps = connectionForStrategy("google-apps");
        AuthData connectionGoogleOpenId = connectionForStrategy("google-openid");
        AuthData connectionIp = connectionForStrategy("ip");
        AuthData connectionOffice365 = connectionForStrategy("mscrm");
        AuthData connectionPingFederate = connectionForStrategy("pingfederate");
        AuthData connectionSAMLP = connectionForStrategy("samlp");
        AuthData connectionSharepoint = connectionForStrategy("sharepoint");

        assertThat(connectionAuth0LDAP.isActiveFlowEnabled(), is(false));
        assertThat(connectionCustom.isActiveFlowEnabled(), is(false));
        assertThat(connectionGoogleApps.isActiveFlowEnabled(), is(false));
        assertThat(connectionGoogleOpenId.isActiveFlowEnabled(), is(false));
        assertThat(connectionIp.isActiveFlowEnabled(), is(false));
        assertThat(connectionOffice365.isActiveFlowEnabled(), is(false));
        assertThat(connectionPingFederate.isActiveFlowEnabled(), is(false));
        assertThat(connectionSAMLP.isActiveFlowEnabled(), is(false));
        assertThat(connectionSharepoint.isActiveFlowEnabled(), is(false));
    }

    private AuthData connectionForStrategy(String connectionName) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "my-connection");
        return new AuthData(connectionName, map);
    }

}