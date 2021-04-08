package com.auth0.android.lock;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.AuthenticationActivity;
import com.auth0.android.provider.CustomTabsOptions;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.UriMatchers.hasHost;
import static androidx.test.espresso.intent.matcher.UriMatchers.hasParamWithValue;
import static androidx.test.espresso.intent.matcher.UriMatchers.hasScheme;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.isOneOf;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.intThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class WebProviderTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private Activity activity;

    @Before
    public void setUp() {
        activity = spy(Robolectric.buildActivity(Activity.class)
                .create()
                .start()
                .resume()
                .get());
        setupBrowserContext(activity, Collections.singletonList("com.auth0.browser"));
    }

    @Test
    public void shouldStart() {
        Options options = new Options();
        options.setAccount(new Auth0("clientId", "domain.auth0.com"));
        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);

        webProvider.start(activity, "my-connection", null, callback, 123);
        verify(callback, never()).onFailure(any(AuthenticationException.class));
    }

    @Test
    public void shouldFailWhenBrowserAppIsMissing() {
        setupBrowserContext(activity, Collections.<String>emptyList());

        Options options = new Options();
        options.setAccount(new Auth0("clientId", "domain.auth0.com"));
        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);
        webProvider.start(activity, "my-connection", null, callback, 123);

        ArgumentCaptor<AuthenticationException> exceptionCaptor = ArgumentCaptor.forClass(AuthenticationException.class);
        verify(callback).onFailure(exceptionCaptor.capture());
        assertThat(exceptionCaptor.getValue(), is(notNullValue()));
        assertThat(exceptionCaptor.getValue().isBrowserAppNotAvailable(), is(true));
    }

    @Test
    public void shouldStartWithCustomAuthenticationParameters() {
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
        verify(callback, never()).onFailure(any(AuthenticationException.class));

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));

        assertThat(intent.hasExtra("com.auth0.android.EXTRA_AUTHORIZE_URI"), is(true));
        Uri authorizeUri = intent.getParcelableExtra("com.auth0.android.EXTRA_AUTHORIZE_URI");
        assertThat(authorizeUri, hasHost("domain.auth0.com"));
        assertThat(authorizeUri, hasParamWithValue("custom-param-1", "value-1"));
        assertThat(authorizeUri, hasParamWithValue("custom-param-2", "value-2"));
        assertThat(authorizeUri, hasParamWithValue("client_id", "clientId"));
        assertThat(authorizeUri, hasParamWithValue("connection", "my-connection"));
        assertThat(authorizeUri, hasParamWithValue("audience", "https://me.auth0.com/myapi"));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldStartWithCustomAudience() {
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
        verify(callback, never()).onFailure(any(AuthenticationException.class));

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));

        assertThat(intent.hasExtra("com.auth0.android.EXTRA_AUTHORIZE_URI"), is(true));
        Uri authorizeUri = intent.getParcelableExtra("com.auth0.android.EXTRA_AUTHORIZE_URI");
        assertThat(authorizeUri, hasHost("domain.auth0.com"));
        assertThat(authorizeUri, hasParamWithValue("client_id", "clientId"));
        assertThat(authorizeUri, hasParamWithValue("connection", "my-connection"));
        assertThat(authorizeUri, hasParamWithValue("audience", "https://me.auth0.com/myapi"));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldStartBrowserWithOptions() {
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
        CustomTabsOptions customTabsOptions = CustomTabsOptions.newBuilder().build();
        options.withCustomTabsOptions(customTabsOptions);

        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);

        webProvider.start(activity, "my-connection", null, callback, 123);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(intentCaptor.capture());
        verify(callback, never()).onFailure(any(AuthenticationException.class));

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.hasExtra("com.auth0.android.EXTRA_AUTHORIZE_URI"), is(true));
        Uri authorizeUri = intent.getParcelableExtra("com.auth0.android.EXTRA_AUTHORIZE_URI");

        assertThat(authorizeUri.getQueryParameter("redirect_uri"), is(notNullValue()));
        Uri redirectUri = Uri.parse(authorizeUri.getQueryParameter("redirect_uri"));
        assertThat(redirectUri, hasScheme("auth0"));

        assertThat(authorizeUri, hasHost("domain.auth0.com"));
        assertThat(authorizeUri, hasParamWithValue("client_id", "clientId"));
        assertThat(authorizeUri, hasParamWithValue("connection", "my-connection"));
        assertThat(authorizeUri, hasParamWithValue("custom-param-1", "value-1"));
        assertThat(authorizeUri, hasParamWithValue("custom-param-2", "value-2"));
        assertThat(authorizeUri, hasParamWithValue("scope", "email profile photos"));
        assertThat(authorizeUri, hasParamWithValue("connection_scope", "the connection scope"));
        assertThat(intent.hasExtra("com.auth0.android.EXTRA_USE_BROWSER"), is(true));
        assertThat(intent.getParcelableExtra("com.auth0.android.EXTRA_CT_OPTIONS"), is(notNullValue()));
        assertThat(intent.getBooleanExtra("com.auth0.android.EXTRA_USE_BROWSER", false), is(true));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldStartWebViewWithOptions() {
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
        verify(callback, never()).onFailure(any(AuthenticationException.class));

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.hasExtra("com.auth0.android.EXTRA_AUTHORIZE_URI"), is(true));
        Uri authorizeUri = intent.getParcelableExtra("com.auth0.android.EXTRA_AUTHORIZE_URI");

        assertThat(authorizeUri.getQueryParameter("redirect_uri"), is(notNullValue()));
        Uri redirectUri = Uri.parse(authorizeUri.getQueryParameter("redirect_uri"));
        assertThat(redirectUri, hasScheme("auth0"));

        assertThat(authorizeUri, hasHost("domain.auth0.com"));
        assertThat(authorizeUri, hasParamWithValue("client_id", "clientId"));
        assertThat(authorizeUri, hasParamWithValue("connection", "my-connection"));
        assertThat(authorizeUri, hasParamWithValue("custom-param-1", "value-1"));
        assertThat(authorizeUri, hasParamWithValue("custom-param-2", "value-2"));
        assertThat(authorizeUri, hasParamWithValue("scope", "email profile photos"));
        assertThat(authorizeUri, hasParamWithValue("connection_scope", "the connection scope"));
        assertThat(intent.hasExtra("com.auth0.android.EXTRA_USE_BROWSER"), is(true));
        assertThat(intent.getBooleanExtra("com.auth0.android.EXTRA_USE_BROWSER", true), is(false));
        assertThat(intent, hasComponent(AuthenticationActivity.class.getName()));
    }

    @Test
    public void shouldResumeWithIntent() {
        Intent intent = mock(Intent.class);
        Options options = mock(Options.class);
        WebProvider webProvider = new WebProvider(options);
        assertThat(webProvider.resume(intent), is(false));
    }

    @Test
    public void shouldResumeWithCodesAndIntent() {
        Intent intent = mock(Intent.class);
        Options options = mock(Options.class);
        WebProvider webProvider = new WebProvider(options);
        assertThat(webProvider.resume(1, Activity.RESULT_CANCELED, intent), is(false));
    }

    /**
     * Sets up a given context for using browsers.
     * If the list passed is empty, then no browser packages would be available.
     */
    static void setupBrowserContext(@NonNull Context context, @NonNull List<String> browserPackages) {
        PackageManager pm = mock(PackageManager.class);
        when(context.getPackageManager()).thenReturn(pm);

        List<ResolveInfo> allBrowsers = new ArrayList<>();
        for (String browser : browserPackages) {
            ResolveInfo info = resolveInfoForPackageName(browser);
            allBrowsers.add(info);
        }
        when(pm.queryIntentActivities(any(Intent.class), intThat(isOneOf(0, PackageManager.MATCH_ALL)))).thenReturn(allBrowsers);
    }

    private static ResolveInfo resolveInfoForPackageName(@Nullable String packageName) {
        if (packageName == null) {
            return null;
        }
        ResolveInfo resInfo = mock(ResolveInfo.class);
        resInfo.activityInfo = new ActivityInfo();
        resInfo.activityInfo.packageName = packageName;
        return resInfo;
    }

}