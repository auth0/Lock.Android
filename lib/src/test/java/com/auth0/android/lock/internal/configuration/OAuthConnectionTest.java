package com.auth0.android.lock.internal.configuration;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static com.auth0.android.lock.internal.configuration.ConnectionMatcher.hasType;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class OAuthConnectionTest {

    @Test
    public void shouldHaveName() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        OAuthConnection connection = Connection.newConnectionFor("strategy", values);
        assertThat(connection.getName(), is("name"));
    }

    @Test
    public void shouldHaveStrategy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        OAuthConnection connection = Connection.newConnectionFor("strategy", values);
        assertThat(connection.getStrategy(), is("strategy"));
    }

    @Test
    public void shouldBeSocialType() throws Exception {
        OAuthConnection connection = connectionForStrategy("facebook");
        assertThat(connection, hasType(AuthType.SOCIAL));
    }

    @Test
    public void shouldBeEnterpriseType() throws Exception {
        OAuthConnection connection = connectionForStrategy("ad");
        assertThat(connection, hasType(AuthType.ENTERPRISE));
    }

    @Test
    public void shouldReturnDomainNameInSet() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "ad");
        values.put("domain", "domain.com");
        OAuthConnection connection = Connection.newConnectionFor("ad", values);
        assertThat(connection.getDomainSet(), hasItem("domain.com"));
    }

    @Test
    public void shouldReturnAllDomainNamesAsSet() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "ad");
        values.put("domain", "domain.com");
        values.put("domain_aliases", Arrays.asList("domain2.com", "domain3.com"));
        OAuthConnection connection = Connection.newConnectionFor("ad", values);
        assertThat(connection.getDomainSet(), hasItems("domain.com", "domain2.com", "domain3.com"));
    }

    @Test
    public void shouldReturnEmptySetWithNoDomainName() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "ad");
        OAuthConnection connection = Connection.newConnectionFor("ad", values);
        assertThat(connection.getDomainSet().isEmpty(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfADFS() throws Exception {
        OAuthConnection connection = connectionForStrategy("adfs");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfWaad() throws Exception {
        OAuthConnection connection = connectionForStrategy("waad");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfActiveDirectory() throws Exception {
        OAuthConnection connection = connectionForStrategy("ad");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldNotHaveResourceOwnerEnabledByDefault() throws Exception {
        OAuthConnection connection = connectionForStrategy("strategy");
        assertThat(connection.isActiveFlowEnabled(), is(false));
    }

    @Test
    public void shouldNotHaveResourceOwnerEnabledIfNotADFSWaadOrActiveDirectory() throws Exception {
        OAuthConnection connectionAuth0LDAP = connectionForStrategy("auth0-adldap");
        OAuthConnection connectionCustom = connectionForStrategy("custom");
        OAuthConnection connectionGoogleApps = connectionForStrategy("google-apps");
        OAuthConnection connectionGoogleOpenId = connectionForStrategy("google-openid");
        OAuthConnection connectionIp = connectionForStrategy("ip");
        OAuthConnection connectionOffice365 = connectionForStrategy("mscrm");
        OAuthConnection connectionPingFederate = connectionForStrategy("pingfederate");
        OAuthConnection connectionSAMLP = connectionForStrategy("samlp");
        OAuthConnection connectionSharepoint = connectionForStrategy("sharepoint");

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

    private OAuthConnection connectionForStrategy(String name) {
        Map<String, Object> values = new HashMap<>();
        values.put("name", name);
        return Connection.newConnectionFor(name, values);
    }
}