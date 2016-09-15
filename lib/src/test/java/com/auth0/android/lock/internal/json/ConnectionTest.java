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

package com.auth0.android.lock.internal.json;

import com.auth0.android.lock.internal.AuthType;

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
public class ConnectionTest {

    public static final String CONNECTION_NAME = "Username-Password";
    public static final Object VALUE = "value";
    public static final String KEY = "key";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldBuildConnectionWithName() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection("strategy", values);
        assertNotNull(connection);
        assertThat(connection.getStrategy(), equalTo("strategy"));
        assertThat(connection.getName(), equalTo(CONNECTION_NAME));
    }

    @Test
    public void shouldBuildConnectionWithValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        Connection connection = new Connection("strategy", values);
        assertThat(connection.getValueForKey(KEY), is(VALUE));
    }

    @Test
    public void shouldNotStoreNameInValues() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection("strategy", values);
        assertThat(connection.getValueForKey("name"), is(nullValue()));
    }

    @Test
    public void shouldRaiseExceptionWhenNameIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        Map<String, Object> values = null;
        new Connection("strategy", values);
    }

    @Test
    public void shouldNotHaveResourceOwnerEnabledByDefault() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection("strategy", values);
        assertThat(connection.isActiveFlowEnabled(), is(false));
    }

    @Test
    public void shouldReturnValueFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        Connection connection = new Connection("strategy", values);
        String value = connection.getValueForKey(KEY);
        assertThat(value, equalTo(VALUE));
    }

    @Test
    public void shouldReturnBooleanValueFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, true);
        Connection connection = new Connection("strategy", values);
        boolean value = connection.booleanForKey(KEY);
        assertThat(value, is(true));
    }

    @Test
    public void shouldReturnDefaultBooleanValueFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection("strategy", values);
        boolean value = connection.booleanForKey(KEY);
        assertThat(value, is(false));
    }

    @Test
    public void shouldRaiseExceptionWhenValueIsNotBoolean() {
        expectedException.expect(ClassCastException.class);
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        Connection connection = new Connection("strategy", values);
        connection.booleanForKey(KEY);
    }

    @Test
    public void shouldReturnDomainNameInSet() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put("domain", "domain.com");
        Connection connection = new Connection("strategy", values);
        assertThat(connection.getDomainSet(), hasItem("domain.com"));
    }

    @Test
    public void shouldReturnAllDomainNamesAsSet() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put("domain", "domain.com");
        values.put("domain_aliases", Arrays.asList("domain2.com", "domain3.com"));
        Connection connection = new Connection("strategy", values);
        assertThat(connection.getDomainSet(), hasItems("domain.com", "domain2.com", "domain3.com"));
    }

    @Test
    public void shouldReturnEmptySetWithNoDomainName() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection("strategy", values);
        assertThat(connection.getDomainSet().isEmpty(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfADFS() throws Exception {
        Connection connection = connectionForStrategy("adfs");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfWaad() throws Exception {
        Connection connection = connectionForStrategy("waad");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldHaveResourceOwnerEnabledIfActiveDirectory() throws Exception {
        Connection connection = connectionForStrategy("ad");
        assertThat(connection.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldNotHaveResourceOwnerEnabledIfNotADFSWaadOrActiveDirectory() throws Exception {
        Connection connectionAuth0LDAP = connectionForStrategy("auth0-adldap");
        Connection connectionCustom = connectionForStrategy("custom");
        Connection connectionGoogleApps = connectionForStrategy("google-apps");
        Connection connectionGoogleOpenId = connectionForStrategy("google-openid");
        Connection connectionIp = connectionForStrategy("ip");
        Connection connectionOffice365 = connectionForStrategy("mscrm");
        Connection connectionPingFederate = connectionForStrategy("pingfederate");
        Connection connectionSAMLP = connectionForStrategy("samlp");
        Connection connectionSharepoint = connectionForStrategy("sharepoint");

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

    @Test
    public void shouldReturnUnknownSocial() {
        final Connection unknownSocial = connectionForStrategy("this-strategy-does-not-exist");
        assertThat(unknownSocial.getType(), is(AuthType.SOCIAL));
    }

    @Test
    public void shouldReturnSocial() {
        final Connection amazon = connectionForStrategy("amazon");
        final Connection aol = connectionForStrategy("aol");
        final Connection baidu = connectionForStrategy("baidu");
        final Connection bitbucket = connectionForStrategy("bitbucket");
        final Connection box = connectionForStrategy("box");
        final Connection dropbox = connectionForStrategy("dropbox");
        final Connection dwolla = connectionForStrategy("dwolla");
        final Connection ebay = connectionForStrategy("ebay");
        final Connection evernote = connectionForStrategy("evernote");
        final Connection evernoteSandbox = connectionForStrategy("evernote-sandbox");
        final Connection exact = connectionForStrategy("exact");
        final Connection facebook = connectionForStrategy("facebook");
        final Connection fitbit = connectionForStrategy("fitbit");
        final Connection github = connectionForStrategy("github");
        final Connection googleOauth2 = connectionForStrategy("google-oauth2");
        final Connection instagram = connectionForStrategy("instagram");
        final Connection linkedin = connectionForStrategy("linkedin");
        final Connection miicard = connectionForStrategy("miicard");
        final Connection paypal = connectionForStrategy("paypal");
        final Connection planningcenter = connectionForStrategy("planningcenter");
        final Connection renren = connectionForStrategy("renren");
        final Connection salesforce = connectionForStrategy("salesforce");
        final Connection salesforceSandbox = connectionForStrategy("salesforce-sandbox");
        final Connection shopify = connectionForStrategy("shopify");
        final Connection soundcloud = connectionForStrategy("soundcloud");
        final Connection thecity = connectionForStrategy("thecity");
        final Connection thecitySandbox = connectionForStrategy("thecity-sandbox");
        final Connection thirtysevensignals = connectionForStrategy("thirtysevensignals");
        final Connection twitter = connectionForStrategy("twitter");
        final Connection vkontakte = connectionForStrategy("vkontakte");
        final Connection weibo = connectionForStrategy("weibo");
        final Connection windowslive = connectionForStrategy("windowslive");
        final Connection wordpress = connectionForStrategy("wordpress");
        final Connection yahoo = connectionForStrategy("yahoo");
        final Connection yammer = connectionForStrategy("yammer");
        final Connection yandex = connectionForStrategy("yandex");

        assertThat(amazon.getType(), is(AuthType.SOCIAL));
        assertThat(aol.getType(), is(AuthType.SOCIAL));
        assertThat(baidu.getType(), is(AuthType.SOCIAL));
        assertThat(bitbucket.getType(), is(AuthType.SOCIAL));
        assertThat(box.getType(), is(AuthType.SOCIAL));
        assertThat(dropbox.getType(), is(AuthType.SOCIAL));
        assertThat(dwolla.getType(), is(AuthType.SOCIAL));
        assertThat(ebay.getType(), is(AuthType.SOCIAL));
        assertThat(evernote.getType(), is(AuthType.SOCIAL));
        assertThat(evernoteSandbox.getType(), is(AuthType.SOCIAL));
        assertThat(exact.getType(), is(AuthType.SOCIAL));
        assertThat(facebook.getType(), is(AuthType.SOCIAL));
        assertThat(fitbit.getType(), is(AuthType.SOCIAL));
        assertThat(github.getType(), is(AuthType.SOCIAL));
        assertThat(googleOauth2.getType(), is(AuthType.SOCIAL));
        assertThat(instagram.getType(), is(AuthType.SOCIAL));
        assertThat(linkedin.getType(), is(AuthType.SOCIAL));
        assertThat(miicard.getType(), is(AuthType.SOCIAL));
        assertThat(paypal.getType(), is(AuthType.SOCIAL));
        assertThat(planningcenter.getType(), is(AuthType.SOCIAL));
        assertThat(renren.getType(), is(AuthType.SOCIAL));
        assertThat(salesforce.getType(), is(AuthType.SOCIAL));
        assertThat(salesforceSandbox.getType(), is(AuthType.SOCIAL));
        assertThat(shopify.getType(), is(AuthType.SOCIAL));
        assertThat(soundcloud.getType(), is(AuthType.SOCIAL));
        assertThat(thecity.getType(), is(AuthType.SOCIAL));
        assertThat(thecitySandbox.getType(), is(AuthType.SOCIAL));
        assertThat(thirtysevensignals.getType(), is(AuthType.SOCIAL));
        assertThat(twitter.getType(), is(AuthType.SOCIAL));
        assertThat(vkontakte.getType(), is(AuthType.SOCIAL));
        assertThat(weibo.getType(), is(AuthType.SOCIAL));
        assertThat(windowslive.getType(), is(AuthType.SOCIAL));
        assertThat(wordpress.getType(), is(AuthType.SOCIAL));
        assertThat(yahoo.getType(), is(AuthType.SOCIAL));
        assertThat(yammer.getType(), is(AuthType.SOCIAL));
        assertThat(yandex.getType(), is(AuthType.SOCIAL));
    }

    @Test
    public void shouldReturnValidDatabaseStrategy() {
        final Connection unknownSocial = connectionForStrategy("auth0");

        assertThat(unknownSocial.getType(), is(AuthType.DATABASE));
    }

    @Test
    public void shouldReturnValidPasswordlessStrategy() {
        final Connection sms = connectionForStrategy("sms");
        final Connection email = connectionForStrategy("email");

        assertThat(sms.getType(), is(AuthType.PASSWORDLESS));
        assertThat(email.getType(), is(AuthType.PASSWORDLESS));
    }

    @Test
    public void shouldReturnEnterprise() {
        final Connection ad = connectionForStrategy("ad");
        final Connection adfs = connectionForStrategy("adfs");
        final Connection auth0Adldap = connectionForStrategy("auth0-adldap");
        final Connection custom = connectionForStrategy("custom");
        final Connection googleApps = connectionForStrategy("google-apps");
        final Connection googleOpenid = connectionForStrategy("google-openid");
        final Connection ip = connectionForStrategy("ip");
        final Connection mscrm = connectionForStrategy("mscrm");
        final Connection office365 = connectionForStrategy("office365");
        final Connection pingfederate = connectionForStrategy("pingfederate");
        final Connection samlp = connectionForStrategy("samlp");
        final Connection sharepoint = connectionForStrategy("sharepoint");
        final Connection waad = connectionForStrategy("waad");

        assertThat(ad.getType(), is(AuthType.ENTERPRISE));
        assertThat(adfs.getType(), is(AuthType.ENTERPRISE));
        assertThat(auth0Adldap.getType(), is(AuthType.ENTERPRISE));
        assertThat(custom.getType(), is(AuthType.ENTERPRISE));
        assertThat(googleApps.getType(), is(AuthType.ENTERPRISE));
        assertThat(googleOpenid.getType(), is(AuthType.ENTERPRISE));
        assertThat(ip.getType(), is(AuthType.ENTERPRISE));
        assertThat(mscrm.getType(), is(AuthType.ENTERPRISE));
        assertThat(office365.getType(), is(AuthType.ENTERPRISE));
        assertThat(pingfederate.getType(), is(AuthType.ENTERPRISE));
        assertThat(samlp.getType(), is(AuthType.ENTERPRISE));
        assertThat(sharepoint.getType(), is(AuthType.ENTERPRISE));
        assertThat(waad.getType(), is(AuthType.ENTERPRISE));
    }

    private Connection connectionForStrategy(String connectionName) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "my-connection");
        return new Connection(connectionName, map);
    }

}