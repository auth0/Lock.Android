package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.auth0.android.Auth0;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.AuthenticationActivity;
import com.auth0.android.provider.WebAuthActivity;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasParamWithValue;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasScheme;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = "src/main/AndroidManifest.xml")
public class WebProviderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Activity activity;

    @Before
    public void setUp() throws Exception {
        activity = spy(Robolectric.buildActivity(Activity.class)
                .create()
                .start()
                .resume()
                .get());
    }

    @Test
    public void shouldStart() throws Exception {
        Options options = new Options();
        options.setAccount(new Auth0("clientId", "domain.auth0.com"));
        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);

        webProvider.start(activity, "my-connection", null, callback, 123);
    }

    @Test
    public void shouldStartWithCustomAuthenticationParameters() throws Exception {
        Auth0 account = new Auth0("clientId", "domain.auth0.com");
        account.setOIDCConformant(true);
        Options options = new Options();
        options.setAccount(account);

        options.setUseBrowser(true);
        options.withAudience("https://me.auth0.com/myapi");

        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);

        Map<String, Object> parameters = new HashMap<>();
        parameters.put("custom-param-1", "value-1");
        parameters.put("custom-param-2", "value-2");

        webProvider.start(activity, "my-connection", parameters, callback, 123);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.getData(), hasHost("domain.auth0.com"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-1", "value-1"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-2", "value-2"));
        assertThat(intent.getData(), hasParamWithValue("client_id", "clientId"));
        assertThat(intent.getData(), hasParamWithValue("connection", "my-connection"));
        assertThat(intent.getData(), hasParamWithValue("audience", "https://me.auth0.com/myapi"));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldStartWithCustomAudience() throws Exception {
        Auth0 account = new Auth0("clientId", "domain.auth0.com");
        account.setOIDCConformant(true);
        Options options = new Options();
        options.setAccount(account);

        options.setUseBrowser(true);
        options.withAudience("https://me.auth0.com/myapi");

        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);

        webProvider.start(activity, "my-connection", null, callback, 123);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.getData(), hasHost("domain.auth0.com"));
        assertThat(intent.getData(), hasParamWithValue("client_id", "clientId"));
        assertThat(intent.getData(), hasParamWithValue("connection", "my-connection"));
        assertThat(intent.getData(), hasParamWithValue("audience", "https://me.auth0.com/myapi"));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldStartBrowserWithOptions() throws Exception {
        Auth0 account = new Auth0("clientId", "domain.auth0.com");
        Options options = new Options();
        options.setAccount(account);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("custom-param-1", "value-1");
        parameters.put("custom-param-2", "value-2");
        options.setAuthenticationParameters(parameters);
        options.withScope("email profile photos");
        options.withConnectionScope("my-connection", "the connection scope");
        options.setUseBrowser(true);
        options.withScheme("auth0");

        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);

        webProvider.start(activity, "my-connection", null, callback, 123);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.getData().getQueryParameter("redirect_uri"), is(notNullValue()));
        Uri redirectUri = Uri.parse(intent.getData().getQueryParameter("redirect_uri"));
        assertThat(redirectUri, hasScheme("auth0"));
        assertThat(intent.getData(), hasHost("domain.auth0.com"));
        assertThat(intent.getData(), hasParamWithValue("client_id", "clientId"));
        assertThat(intent.getData(), hasParamWithValue("connection", "my-connection"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-1", "value-1"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-2", "value-2"));
        assertThat(intent.getData(), hasParamWithValue("scope", "email profile photos"));
        assertThat(intent.getData(), hasParamWithValue("connection_scope", "the connection scope"));
        assertThat(intent.hasExtra("com.auth0.android.EXTRA_USE_BROWSER"), is(true));
        assertThat(intent.getBooleanExtra("com.auth0.android.EXTRA_USE_BROWSER", false), is(true));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldStartWebViewWithOptions() throws Exception {
        Auth0 account = new Auth0("clientId", "domain.auth0.com");
        Options options = new Options();
        options.setAccount(account);

        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("custom-param-1", "value-1");
        parameters.put("custom-param-2", "value-2");
        options.setAuthenticationParameters(parameters);
        options.withScope("email profile photos");
        options.withConnectionScope("my-connection", "the connection scope");
        options.setUseBrowser(false);
        options.withScheme("auth0");

        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);

        webProvider.start(activity, "my-connection", null, callback, 123);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivityForResult(intentCaptor.capture(), eq(123));

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.getData().getQueryParameter("redirect_uri"), is(notNullValue()));
        Uri redirectUri = Uri.parse(intent.getData().getQueryParameter("redirect_uri"));
        assertThat(redirectUri, hasScheme("auth0"));
        assertThat(intent.getData(), hasHost("domain.auth0.com"));
        assertThat(intent.getData(), hasParamWithValue("client_id", "clientId"));
        assertThat(intent.getData(), hasParamWithValue("connection", "my-connection"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-1", "value-1"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-2", "value-2"));
        assertThat(intent.getData(), hasParamWithValue("scope", "email profile photos"));
        assertThat(intent.getData(), hasParamWithValue("connection_scope", "the connection scope"));
        assertThat(intent.hasExtra("com.auth0.android.EXTRA_USE_BROWSER"), is(true));
        assertThat(intent.getBooleanExtra("com.auth0.android.EXTRA_USE_BROWSER", true), is(false));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldResumeWithIntent() throws Exception {
        Intent intent = mock(Intent.class);
        Options options = mock(Options.class);
        WebProvider webProvider = new WebProvider(options);
        assertThat(webProvider.resume(intent), is(false));
    }

    @Test
    public void shouldResumeWithCodesAndIntent() throws Exception {
        Intent intent = mock(Intent.class);
        Options options = mock(Options.class);
        WebProvider webProvider = new WebProvider(options);
        assertThat(webProvider.resume(1, Activity.RESULT_CANCELED, intent), is(false));
    }

}