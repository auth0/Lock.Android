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

package com.auth0.android.lock.utils;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class StrategyTest {

    private Connection connection;

    @Before
    public void setUp() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "default");
        connection = new Connection(values);
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfADFS() throws Exception {
        Strategy strategy = new Strategy("adfs", Collections.singletonList(connection));
        assertThat(strategy.isActiveFlowEnabled(), is(true));
        assertThat(strategy.getConnections().get(0).isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfWaad() throws Exception {
        Strategy strategy = new Strategy("waad", Collections.singletonList(connection));
        assertThat(strategy.isActiveFlowEnabled(), is(true));
        assertThat(strategy.getConnections().get(0).isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfActiveDirectory() throws Exception {
        Strategy strategy = new Strategy("ad", Collections.singletonList(connection));
        assertThat(strategy.isActiveFlowEnabled(), is(true));
        assertThat(strategy.getConnections().get(0).isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldNotHaveResourceOwnerEnabledIfNotADFSWaadOrActiveDirectory() throws Exception {
        Strategy strategyAuth0LDAP = new Strategy("auth0-adldap", Collections.singletonList(connection));
        Strategy strategyCustom = new Strategy("custom", Collections.singletonList(connection));
        Strategy strategyGoogleApps = new Strategy("google-apps", Collections.singletonList(connection));
        Strategy strategyGoogleOpenId = new Strategy("google-openid", Collections.singletonList(connection));
        Strategy strategyIp = new Strategy("ip", Collections.singletonList(connection));
        Strategy strategyOffice365 = new Strategy("mscrm", Collections.singletonList(connection));
        Strategy strategyPingFederate = new Strategy("pingfederate", Collections.singletonList(connection));
        Strategy strategySAMLP = new Strategy("samlp", Collections.singletonList(connection));
        Strategy strategySharepoint = new Strategy("sharepoint", Collections.singletonList(connection));

        assertThat(strategyAuth0LDAP.isActiveFlowEnabled(), is(false));
        assertThat(strategyCustom.isActiveFlowEnabled(), is(false));
        assertThat(strategyGoogleApps.isActiveFlowEnabled(), is(false));
        assertThat(strategyGoogleOpenId.isActiveFlowEnabled(), is(false));
        assertThat(strategyIp.isActiveFlowEnabled(), is(false));
        assertThat(strategyOffice365.isActiveFlowEnabled(), is(false));
        assertThat(strategyPingFederate.isActiveFlowEnabled(), is(false));
        assertThat(strategySAMLP.isActiveFlowEnabled(), is(false));
        assertThat(strategySharepoint.isActiveFlowEnabled(), is(false));

        assertThat(strategyAuth0LDAP.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategyCustom.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategyGoogleApps.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategyGoogleOpenId.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategyIp.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategyOffice365.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategyPingFederate.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategySAMLP.getConnections().get(0).isActiveFlowEnabled(), is(false));
        assertThat(strategySharepoint.getConnections().get(0).isActiveFlowEnabled(), is(false));
    }

}