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

import com.auth0.android.lock.enums.AuthType;

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

    @Test
    public void shouldReturnUnknownSocial() {
        final AuthData unknownSocial = connectionForStrategy("this-strategy-does-not-exist");
        assertThat(unknownSocial.getType(), is(AuthType.SOCIAL));
    }

    @Test
    public void shouldReturnSocial() {
        final AuthData amazon = connectionForStrategy("amazon");
        final AuthData aol = connectionForStrategy("aol");
        final AuthData baidu = connectionForStrategy("baidu");
        final AuthData bitbucket = connectionForStrategy("bitbucket");
        final AuthData box = connectionForStrategy("box");
        final AuthData dropbox = connectionForStrategy("dropbox");
        final AuthData dwolla = connectionForStrategy("dwolla");
        final AuthData ebay = connectionForStrategy("ebay");
        final AuthData evernote = connectionForStrategy("evernote");
        final AuthData evernoteSandbox = connectionForStrategy("evernote-sandbox");
        final AuthData exact = connectionForStrategy("exact");
        final AuthData facebook = connectionForStrategy("facebook");
        final AuthData fitbit = connectionForStrategy("fitbit");
        final AuthData github = connectionForStrategy("github");
        final AuthData googleOauth2 = connectionForStrategy("google-oauth2");
        final AuthData instagram = connectionForStrategy("instagram");
        final AuthData linkedin = connectionForStrategy("linkedin");
        final AuthData miicard = connectionForStrategy("miicard");
        final AuthData paypal = connectionForStrategy("paypal");
        final AuthData planningcenter = connectionForStrategy("planningcenter");
        final AuthData renren = connectionForStrategy("renren");
        final AuthData salesforce = connectionForStrategy("salesforce");
        final AuthData salesforceSandbox = connectionForStrategy("salesforce-sandbox");
        final AuthData shopify = connectionForStrategy("shopify");
        final AuthData soundcloud = connectionForStrategy("soundcloud");
        final AuthData thecity = connectionForStrategy("thecity");
        final AuthData thecitySandbox = connectionForStrategy("thecity-sandbox");
        final AuthData thirtysevensignals = connectionForStrategy("thirtysevensignals");
        final AuthData twitter = connectionForStrategy("twitter");
        final AuthData vkontakte = connectionForStrategy("vkontakte");
        final AuthData weibo = connectionForStrategy("weibo");
        final AuthData windowslive = connectionForStrategy("windowslive");
        final AuthData wordpress = connectionForStrategy("wordpress");
        final AuthData yahoo = connectionForStrategy("yahoo");
        final AuthData yammer = connectionForStrategy("yammer");
        final AuthData yandex = connectionForStrategy("yandex");

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
        final AuthData unknownSocial = connectionForStrategy("auth0");

        assertThat(unknownSocial.getType(), is(AuthType.DATABASE));
    }

    @Test
    public void shouldReturnValidPasswordlessStrategy() {
        final AuthData sms = connectionForStrategy("sms");
        final AuthData email = connectionForStrategy("email");

        assertThat(sms.getType(), is(AuthType.PASSWORDLESS));
        assertThat(email.getType(), is(AuthType.PASSWORDLESS));
    }

    @Test
    public void shouldReturnEnterprise() {
        final AuthData ad = connectionForStrategy("ad");
        final AuthData adfs = connectionForStrategy("adfs");
        final AuthData auth0Adldap = connectionForStrategy("auth0-adldap");
        final AuthData custom = connectionForStrategy("custom");
        final AuthData googleApps = connectionForStrategy("google-apps");
        final AuthData googleOpenid = connectionForStrategy("google-openid");
        final AuthData ip = connectionForStrategy("ip");
        final AuthData mscrm = connectionForStrategy("mscrm");
        final AuthData office365 = connectionForStrategy("office365");
        final AuthData pingfederate = connectionForStrategy("pingfederate");
        final AuthData samlp = connectionForStrategy("samlp");
        final AuthData sharepoint = connectionForStrategy("sharepoint");
        final AuthData waad = connectionForStrategy("waad");

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

    private AuthData connectionForStrategy(String connectionName) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "my-connection");
        return new AuthData(connectionName, map);
    }

}