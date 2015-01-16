/*
 * LockBuilderTest.java
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

import com.auth0.api.APIClient;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LockBuilderTest {

    public static final String CLIENT_ID = "CLIENTID";
    public static final String TENANT = "TENANT";
    public static final String DOMAIN = "http://domain.com";
    public static final String CONFIGURATION = "https://config.com";
    public static final String AUTH0_SUBDOMAIN = "http://pepe.auth0.com";
    
    private LockBuilder builder;
    private Lock lock;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Before
    public void setUp() throws Exception {
        builder = new LockBuilder();
        lock = null;
    }

    @Test
    public void shouldCreateLockWithClientIdAndTenant() throws Exception {
        lock = basicBuilder().build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient, is(notNullValue()));
        assertThat(apiClient.getClientID(), equalTo(CLIENT_ID));
        assertThat(apiClient.getTenantName(), equalTo(TENANT));
        assertThat(apiClient.getBaseURL(), equalTo("https://TENANT.auth0.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo("https://cdn.auth0.com/client/CLIENTID.js"));
    }

    @Test
    public void shouldSetUseWebViewFlag() throws Exception {
        lock = basicBuilder()
                .useWebView(true)
                .build();
        assertThat(lock.isUseWebView(), is(true));
    }

    @Test
    public void shouldSetLoginAfterSignUpFlag() throws Exception {
        lock = basicBuilder()
                .loginAfterSignUp(true)
                .build();
        assertThat(lock.isLoginAfterSignUp(), is(true));
    }

    @Test
    public void shouldSetClosableFlag() throws Exception {
        lock = basicBuilder()
                .closable(true)
                .build();
        assertThat(lock.isClosable(), is(true));
    }

    @Test
    public void shouldSetAuthenticationParams() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        lock = basicBuilder()
                .authenticationParameters(parameters)
                .build();
        assertThat(lock.getAuthenticationParameters(), equalTo(parameters));
    }

    @Test
    public void shouldSetUseEmail() throws Exception {
        lock = basicBuilder()
                .useEmail(false)
                .build();
        assertThat(lock.isUseEmail(), is(false));
    }

    @Test
    public void shouldBuildWithDomainOnly() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domain(DOMAIN)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getTenantName(), equalTo("domain.com"));
        assertThat(apiClient.getBaseURL(), equalTo(DOMAIN));
        assertThat(apiClient.getConfigurationURL(), equalTo(DOMAIN));
    }

    @Test
    public void shouldBuildWithDomainAndConfiguration() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domain(DOMAIN)
                .configuration(CONFIGURATION)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getTenantName(), equalTo("domain.com"));
        assertThat(apiClient.getBaseURL(), equalTo(DOMAIN));
        assertThat(apiClient.getConfigurationURL(), equalTo(CONFIGURATION));

    }

    @Test
    public void shouldAlwaysPickDomainOverTenant() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .tenant(TENANT)
                .domain(DOMAIN)
                .configuration(CONFIGURATION)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getTenantName(), equalTo("domain.com"));
        assertThat(apiClient.getBaseURL(), equalTo(DOMAIN));
        assertThat(apiClient.getConfigurationURL(), equalTo(CONFIGURATION));
    }

    @Test
    public void shouldUseCDNWithAuth0Subdomain() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domain(AUTH0_SUBDOMAIN)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getTenantName(), equalTo("pepe.auth0.com"));
        assertThat(apiClient.getBaseURL(), equalTo(AUTH0_SUBDOMAIN));
        assertThat(apiClient.getConfigurationURL(), equalTo("https://cdn.auth0.com/client/CLIENTID.js"));
    }

    @Test
    public void shouldAlwaysUseProvidedConfig() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domain(AUTH0_SUBDOMAIN)
                .configuration(CONFIGURATION)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo(AUTH0_SUBDOMAIN));
        assertThat(apiClient.getConfigurationURL(), equalTo(CONFIGURATION));
    }

    @Test
    public void shouldFailWithInsufficientData() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalToIgnoringCase("Must supply a non-null ClientId"));
        builder.build();
    }

    @Test
    public void shouldFailWithOnlyClientId() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalToIgnoringCase("Missing Auth0 credentials. Please make sure you supplied at least ClientID and Tenant."));
        builder
                .clientId(CLIENT_ID)
                .build();
    }

    private LockBuilder basicBuilder() {
        return builder
                .clientId(CLIENT_ID)
                .tenant(TENANT);
    }

}
