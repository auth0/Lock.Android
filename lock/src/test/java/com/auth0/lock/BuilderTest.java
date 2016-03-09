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
import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.core.Strategies;
import com.auth0.identity.IdentityProvider;
import com.auth0.lock.credentials.CredentialStore;
import com.auth0.lock.credentials.NullCredentialStore;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isA;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class BuilderTest {

    private static final String CLIENT_ID = "CLIENTID";
    private static final String TENANT = "TENANT";
    private static final String DOMAIN = "domain.com";
    private static final String CONFIGURATION_URL = "https://config.com";
    private static final String CONFIGURATION_DOMAIN = "config.com";
    private static final String CONFIGURATION_FULL_URL = "https://config.com/client/" + CLIENT_ID + ".js";
    private static final String AUTH0_SUBDOMAIN = "pepe.auth0.com";
    private static final String EU_DOMAIN = "samples.eu.auth0.com";

    private Lock.Builder builder;
    private Lock lock;

    @Mock
    private CredentialStore store;
    @Mock
    private IdentityProvider identityProvider;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        builder = new LockBuilder();
        lock = null;
    }

    @Test
    public void shouldCreateLockWithDefaultValues() throws Exception {
        lock = basicBuilder().build();
        assertThat(lock, is(notNullValue()));

        assertThat(lock.shouldUseWebView(), is(false));
        assertThat(lock.isClosable(), is(false));
        assertThat(lock.shouldLoginAfterSignUp(), is(true));
        assertThat(lock.shouldUseEmail(), is(true));
        assertThat(lock.isFullScreen(), is(false));
        assertThat(lock.isSignUpEnabled(), is(true));
        assertThat(lock.isChangePasswordEnabled(), is(true));
        assertThat(lock.useLegacyPasswordReset(), is(false));
    }

    @Test
    public void shouldCreateLockWithClientIdAndTenant() throws Exception {
        lock = basicBuilder().build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient, is(notNullValue()));
        assertThat(apiClient.getClientID(), equalTo(CLIENT_ID));
        assertThat(apiClient.getBaseURL(), equalTo("https://TENANT.auth0.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo("https://cdn.auth0.com/client/CLIENTID.js"));
    }

    @Test
    public void shouldHaveAnAuthenticationAPIClient() throws Exception {
        lock = basicBuilder().build();
        assertThat(lock.getAuthenticationAPIClient(), isA(AuthenticationAPIClient.class));
    }

    @Test
    public void shouldSetUseWebViewFlag() throws Exception {
        lock = basicBuilder()
                .useWebView(true)
                .build();
        assertThat(lock.shouldUseWebView(), is(true));
    }

    @Test
    public void shouldSetLoginAfterSignUpFlag() throws Exception {
        lock = basicBuilder()
                .loginAfterSignUp(true)
                .build();
        assertThat(lock.shouldLoginAfterSignUp(), is(true));
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
        assertThat(lock.shouldUseEmail(), is(false));
    }

    @Test
    public void shouldBuildWithDomainOnly() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domainUrl(DOMAIN)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo("https://domain.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo("https://domain.com/client/" + CLIENT_ID + ".js"));
    }

    @Test
    public void shouldBuildWithDomainAndConfiguration() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domainUrl(DOMAIN)
                .configurationUrl(CONFIGURATION_URL)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo("https://domain.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo(CONFIGURATION_FULL_URL));
    }

    @Test
    public void shouldBuildWithConfigurationDomainName() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domainUrl(DOMAIN)
                .configurationUrl(CONFIGURATION_DOMAIN)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo("https://domain.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo(CONFIGURATION_FULL_URL));
    }

    @Test
    public void shouldUseEuropeCDNWhenAuth0DomainIsInEU() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domainUrl(EU_DOMAIN)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo("https://samples.eu.auth0.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo("https://cdn.eu.auth0.com/client/" + CLIENT_ID + ".js"));
    }

    @Test
    public void shouldAlwaysPickDomainOverTenant() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .tenant(TENANT)
                .domainUrl(DOMAIN)
                .configurationUrl(CONFIGURATION_URL)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo("https://domain.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo(CONFIGURATION_FULL_URL));
    }

    @Test
    public void shouldUseCDNWithAuth0Subdomain() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domainUrl(AUTH0_SUBDOMAIN)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo("https://pepe.auth0.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo("https://cdn.auth0.com/client/CLIENTID.js"));
    }

    @Test
    public void shouldAlwaysUseProvidedConfig() throws Exception {
        lock = builder
                .clientId(CLIENT_ID)
                .domainUrl(AUTH0_SUBDOMAIN)
                .configurationUrl(CONFIGURATION_URL)
                .build();
        assertThat(lock, is(notNullValue()));
        final APIClient apiClient = lock.getAPIClient();
        assertThat(apiClient.getBaseURL(), equalTo("https://pepe.auth0.com"));
        assertThat(apiClient.getConfigurationURL(), equalTo(CONFIGURATION_FULL_URL));
    }

    @Test
    public void shouldFailWithInsufficientData() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalToIgnoringCase("Missing Auth0 credentials. Please make sure you supplied at least ClientID and Domain."));
        builder.build();
    }

    @Test
    public void shouldFailWithOnlyClientId() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalToIgnoringCase("Missing Auth0 credentials. Please make sure you supplied at least ClientID and Domain."));
        builder
                .clientId(CLIENT_ID)
                .build();
    }

    @Test
    public void shouldEnableSignUpAndChangePasswordByDefault() throws Exception {
        lock = basicBuilder().build();
        assertThat(lock.isChangePasswordEnabled(), is(true));
        assertThat(lock.isSignUpEnabled(), is(true));
    }

    @Test
    public void shouldDisableSignUp() throws Exception {
        lock = basicBuilder()
                .disableSignUp(true)
                .build();
        assertThat(lock.isSignUpEnabled(), is(false));
    }

    @Test
    public void shouldDisableChangePassword() throws Exception {
        lock = basicBuilder()
                .disableChangePassword(true)
                .build();
        assertThat(lock.isChangePasswordEnabled(), is(false));
    }

    @Test
    public void shouldRequirePasswordOnPasswordReset() throws Exception {
        lock = basicBuilder()
                .useLegacyPasswordReset()
                .build();
        assertThat(lock.useLegacyPasswordReset(), is(true));
    }

    @Test
    public void shouldSetACredentialStore() throws Exception {
        lock = basicBuilder()
                .useCredentialStore(store)
                .build();
        assertThat(lock.getCredentialStore(), is(store));
    }

    @Test
    public void shouldSetADefaultCredentialStore() throws Exception {
        lock = basicBuilder()
                .build();
        assertThat(lock.getCredentialStore(), is(instanceOf(NullCredentialStore.class)));
    }

    @Test
    public void shouldSetADefaultCredentialStoreWhenSettingNull() throws Exception {
        lock = basicBuilder()
                .useCredentialStore(null)
                .build();
        assertThat(lock.getCredentialStore(), is(instanceOf(NullCredentialStore.class)));
    }

    @Test
    public void shouldSetIdPHandler() throws Exception {
        lock = basicBuilder()
                .withIdentityProvider(Strategies.Facebook, identityProvider)
                .build();
        assertThat(lock.providerForName("facebook"), is(identityProvider));
    }

    private Lock.Builder basicBuilder() {
        return builder
                .clientId(CLIENT_ID)
                .tenant(TENANT);
    }

}
