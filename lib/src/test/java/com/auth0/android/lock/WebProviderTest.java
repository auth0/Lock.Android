package com.auth0.android.lock;

import android.app.Activity;
import android.content.Intent;

import com.auth0.android.Auth0;
import com.auth0.android.lock.internal.configuration.Options;
import com.auth0.android.provider.AuthCallback;
import com.auth0.android.provider.WebAuthActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasHost;
import static android.support.test.espresso.intent.matcher.UriMatchers.hasParamWithValue;
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

    @Test
    public void shouldStart() throws Exception {
        Options options = new Options();
        options.setAccount(new Auth0("clientId", "domain.auth0.com"));
        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);
        Activity activity = Robolectric.buildActivity(Activity.class)
                .create()
                .start()
                .resume()
                .get();

        webProvider.start(activity, "my-connection", callback, 123);
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
        options.withAudience("https://me.auth0.com/myapi");

        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);
        Activity activity = spy(Robolectric.buildActivity(Activity.class)
                .create()
                .start()
                .resume()
                .get());

        webProvider.start(activity, "my-connection", callback, 123);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivity(intentCaptor.capture());

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.getData(), hasHost("domain.auth0.com"));
        assertThat(intent.getData(), hasParamWithValue("client_id", "clientId"));
        assertThat(intent.getData(), hasParamWithValue("connection", "my-connection"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-1", "value-1"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-2", "value-2"));
        assertThat(intent.getData(), hasParamWithValue("scope", "email profile photos"));
        assertThat(intent.getData(), hasParamWithValue("connection_scope", "the connection scope"));
        assertThat(intent.getData(), hasParamWithValue("audience", "https://me.auth0.com/myapi"));
        assertThat(intent, hasAction(Intent.ACTION_VIEW));
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
        options.withAudience("https://me.auth0.com/myapi");

        AuthCallback callback = mock(AuthCallback.class);
        WebProvider webProvider = new WebProvider(options);
        Activity activity = spy(Robolectric.buildActivity(Activity.class)
                .create()
                .start()
                .resume()
                .get());

        webProvider.start(activity, "my-connection", callback, 123);
        ArgumentCaptor<Intent> intentCaptor = ArgumentCaptor.forClass(Intent.class);
        verify(activity).startActivityForResult(intentCaptor.capture(), eq(123));

        Intent intent = intentCaptor.getValue();
        assertThat(intent, is(notNullValue()));
        assertThat(intent.getData(), hasHost("domain.auth0.com"));
        assertThat(intent.getData(), hasParamWithValue("client_id", "clientId"));
        assertThat(intent.getData(), hasParamWithValue("connection", "my-connection"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-1", "value-1"));
        assertThat(intent.getData(), hasParamWithValue("custom-param-2", "value-2"));
        assertThat(intent.getData(), hasParamWithValue("scope", "email profile photos"));
        assertThat(intent.getData(), hasParamWithValue("connection_scope", "the connection scope"));
        assertThat(intent.getData(), hasParamWithValue("audience", "https://me.auth0.com/myapi"));
        assertThat(intent, hasComponent(WebAuthActivity.class.getName()));
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