package com.auth0.identity.util;

import com.auth0.android.BuildConfig;
import com.auth0.api.authentication.AuthenticationAPIClient;
import com.auth0.api.authentication.AuthenticationRequest;
import com.auth0.identity.IdentityProviderCallback;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class PKCEUtilTest {

    private static final String CODE_VERIFIER = "dBjftJeZ4CVP-mB92K27uhbUJU1p1r_wW1gFWFOEjXk";
    private static final String CODE_CHALLENGE = "E9Melhoa2OwvFrEMTJguCHaoeK1t8URWbuGJSstw-cM";
    private static final String REDIRECT_URI = "redirectUri";
    private static final String AUTHORIZATION_CODE = "authorizationCode";

    private PKCEUtil pkce;
    @Mock
    private AuthenticationAPIClient apiClient;
    @Mock
    private IdentityProviderCallback callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        pkce = new PKCEUtil(apiClient, REDIRECT_URI, CODE_VERIFIER);
    }

    @Test
    public void shouldGenerateValidRandomCodeChallenge() throws Exception {
        PKCEUtil randomPKCE = new PKCEUtil(apiClient, REDIRECT_URI);
        String challenge = randomPKCE.generateCodeChallenge();
        assertThat(challenge, is(notNullValue()));
        assertThat(challenge, not(Matchers.isEmptyString()));
        assertThat(challenge, not(containsString("=")));
        assertThat(challenge, not(containsString("+")));
        assertThat(challenge, not(containsString("/")));
    }

    @Test
    public void shouldGenerateExpectedCodeChallenge() throws Exception {
        String challenge = pkce.generateCodeChallenge();
        assertThat(challenge, is(equalTo(CODE_CHALLENGE)));
    }

    @Test
    public void testGetToken() throws Exception {
        AuthenticationRequest authenticationRequest = Mockito.mock(AuthenticationRequest.class);
        Mockito.when(apiClient.tokenRequest(AUTHORIZATION_CODE, CODE_VERIFIER, REDIRECT_URI)).thenReturn(authenticationRequest);
        pkce.getToken(AUTHORIZATION_CODE, callback);
        Mockito.verify(apiClient).tokenRequest(AUTHORIZATION_CODE, CODE_VERIFIER, REDIRECT_URI);
    }
}