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

package com.auth0.android.lock.internal.configuration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.android.lock.internal.configuration.Connection.newConnectionFor;
import static com.auth0.android.lock.internal.configuration.ConnectionMatcher.hasType;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class ConnectionTest {

    private static final String CONNECTION_NAME = "Username-Password";
    private static final Object VALUE = "value";
    private static final String KEY = "key";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldBuildConnectionWithName() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = newConnectionFor("strategy", values);
        assertNotNull(connection);
        assertThat(connection.getStrategy(), equalTo("strategy"));
        assertThat(connection.getName(), equalTo(CONNECTION_NAME));
    }

    @Test
    public void shouldBuildConnectionWithValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        Connection connection = newConnectionFor("strategy", values);
        assertThat(connection.valueForKey(KEY, String.class), is(VALUE));
    }

    @Test
    public void shouldNotStoreNameInValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = newConnectionFor("strategy", values);
        assertThat(connection.valueForKey("name", String.class), is(nullValue()));
    }

    @Test
    public void shouldRaiseExceptionWhenNameIsNull() {
        expectedException.expect(IllegalArgumentException.class);
        Map<String, Object> values = null;
        newConnectionFor("strategy", values);
    }

    @Test
    public void shouldReturnValueFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        Connection connection = newConnectionFor("strategy", values);
        String value = connection.valueForKey(KEY, String.class);
        assertThat(value, equalTo(VALUE));
    }

    @Test
    public void shouldReturnNullValueFromMissingKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = newConnectionFor("strategy", values);
        String value = connection.valueForKey(KEY, String.class);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void shouldReturnNullValueOnWrongClassType() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, "3");
        Connection connection = newConnectionFor("strategy", values);
        Integer value = connection.valueForKey(KEY, Integer.class);
        assertThat(value, is(nullValue()));
    }

    @Test
    public void shouldReturnBooleanFromKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, true);
        Connection connection = newConnectionFor("strategy", values);
        boolean value = connection.booleanForKey(KEY);
        assertThat(value, is(true));
    }

    @Test
    public void shouldReturnBooleanFromMissingKey() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        Connection connection = newConnectionFor("strategy", values);
        boolean value = connection.booleanForKey(KEY);
        assertThat(value, is(false));
    }

    @Test
    public void shouldReturnBooleanFromNullValue() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, null);
        Connection connection = newConnectionFor("strategy", values);
        boolean value = connection.booleanForKey(KEY);
        assertThat(value, is(false));
    }

    @Test
    public void shouldReturnUnknownSocial() {
        final Connection unknownSocial = connectionForStrategy("this-strategy-does-not-exist");
        assertThat(unknownSocial, hasType(AuthType.SOCIAL));
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
        final Connection paypalSandbox = connectionForStrategy("paypal-sandbox");
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

        assertThat(amazon, hasType(AuthType.SOCIAL));
        assertThat(aol, hasType(AuthType.SOCIAL));
        assertThat(baidu, hasType(AuthType.SOCIAL));
        assertThat(bitbucket, hasType(AuthType.SOCIAL));
        assertThat(box, hasType(AuthType.SOCIAL));
        assertThat(dropbox, hasType(AuthType.SOCIAL));
        assertThat(dwolla, hasType(AuthType.SOCIAL));
        assertThat(ebay, hasType(AuthType.SOCIAL));
        assertThat(evernote, hasType(AuthType.SOCIAL));
        assertThat(evernoteSandbox, hasType(AuthType.SOCIAL));
        assertThat(exact, hasType(AuthType.SOCIAL));
        assertThat(facebook, hasType(AuthType.SOCIAL));
        assertThat(fitbit, hasType(AuthType.SOCIAL));
        assertThat(github, hasType(AuthType.SOCIAL));
        assertThat(googleOauth2, hasType(AuthType.SOCIAL));
        assertThat(instagram, hasType(AuthType.SOCIAL));
        assertThat(linkedin, hasType(AuthType.SOCIAL));
        assertThat(miicard, hasType(AuthType.SOCIAL));
        assertThat(paypal, hasType(AuthType.SOCIAL));
        assertThat(paypalSandbox, hasType(AuthType.SOCIAL));
        assertThat(planningcenter, hasType(AuthType.SOCIAL));
        assertThat(renren, hasType(AuthType.SOCIAL));
        assertThat(salesforce, hasType(AuthType.SOCIAL));
        assertThat(salesforceSandbox, hasType(AuthType.SOCIAL));
        assertThat(shopify, hasType(AuthType.SOCIAL));
        assertThat(soundcloud, hasType(AuthType.SOCIAL));
        assertThat(thecity, hasType(AuthType.SOCIAL));
        assertThat(thecitySandbox, hasType(AuthType.SOCIAL));
        assertThat(thirtysevensignals, hasType(AuthType.SOCIAL));
        assertThat(twitter, hasType(AuthType.SOCIAL));
        assertThat(vkontakte, hasType(AuthType.SOCIAL));
        assertThat(weibo, hasType(AuthType.SOCIAL));
        assertThat(windowslive, hasType(AuthType.SOCIAL));
        assertThat(wordpress, hasType(AuthType.SOCIAL));
        assertThat(yahoo, hasType(AuthType.SOCIAL));
        assertThat(yammer, hasType(AuthType.SOCIAL));
        assertThat(yandex, hasType(AuthType.SOCIAL));
    }

    @Test
    public void shouldReturnValidDatabaseStrategy() {
        final Connection unknownSocial = connectionForStrategy("auth0");
        assertThat(unknownSocial, hasType(AuthType.DATABASE));
    }

    @Test
    public void shouldReturnValidPasswordlessStrategy() {
        final Connection sms = connectionForStrategy("sms");
        final Connection email = connectionForStrategy("email");

        assertThat(sms, hasType(AuthType.PASSWORDLESS));
        assertThat(email, hasType(AuthType.PASSWORDLESS));
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

        assertThat(ad, hasType(AuthType.ENTERPRISE));
        assertThat(ad.isActiveFlowEnabled(), is(true));
        assertThat(adfs, hasType(AuthType.ENTERPRISE));
        assertThat(adfs.isActiveFlowEnabled(), is(true));
        assertThat(auth0Adldap, hasType(AuthType.ENTERPRISE));
        assertThat(auth0Adldap.isActiveFlowEnabled(), is(false));
        assertThat(custom, hasType(AuthType.ENTERPRISE));
        assertThat(custom.isActiveFlowEnabled(), is(false));
        assertThat(googleApps, hasType(AuthType.ENTERPRISE));
        assertThat(googleApps.isActiveFlowEnabled(), is(false));
        assertThat(googleOpenid, hasType(AuthType.ENTERPRISE));
        assertThat(googleOpenid.isActiveFlowEnabled(), is(false));
        assertThat(ip, hasType(AuthType.ENTERPRISE));
        assertThat(ip.isActiveFlowEnabled(), is(false));
        assertThat(mscrm, hasType(AuthType.ENTERPRISE));
        assertThat(mscrm.isActiveFlowEnabled(), is(false));
        assertThat(office365, hasType(AuthType.ENTERPRISE));
        assertThat(office365.isActiveFlowEnabled(), is(false));
        assertThat(pingfederate, hasType(AuthType.ENTERPRISE));
        assertThat(pingfederate.isActiveFlowEnabled(), is(false));
        assertThat(samlp, hasType(AuthType.ENTERPRISE));
        assertThat(samlp.isActiveFlowEnabled(), is(false));
        assertThat(sharepoint, hasType(AuthType.ENTERPRISE));
        assertThat(sharepoint.isActiveFlowEnabled(), is(false));
        assertThat(waad, hasType(AuthType.ENTERPRISE));
        assertThat(waad.isActiveFlowEnabled(), is(true));
    }

    @Test
    public void shouldDisableActiveFlowOnDemand() {
        final Connection waad = connectionForStrategy("waad");
        assertThat(waad.isActiveFlowEnabled(), is(true));
        waad.disableActiveFlow();
        assertThat(waad.isActiveFlowEnabled(), is(false));
    }

    private Connection connectionForStrategy(String connectionName) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "my-connection");
        return newConnectionFor(connectionName, map);
    }

}