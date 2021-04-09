package com.auth0.android.lock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;

import androidx.test.core.app.ApplicationProvider;

import com.auth0.android.Auth0;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class PasswordlessBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    Auth0 account;
    @Mock
    LockCallback callback;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldThrowIfCallbackIsMissing() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing callback.");

        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        PasswordlessLock.Builder builder = PasswordlessLock.newBuilder(account, null);
        builder.build(activity);
    }

    @Test
    public void shouldThrowIfAccountIsMissing() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing Auth0 account information.");

        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        PasswordlessLock.Builder builder = PasswordlessLock.newBuilder(null, callback);
        builder.build(activity);
    }

    @Test
    public void shouldThrowIfAccountIsMissingAlsoFromResources() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing Auth0 account information.");

        Activity activity = Mockito.mock(Activity.class);
        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(resources.getIdentifier(anyString(), anyString(), anyString())).thenReturn(0);

        PasswordlessLock.Builder builder = PasswordlessLock.newBuilder(callback);
        builder.build(activity);
    }

    @Test
    public void shouldCreateAccountFromResources() {
        Activity activity = Mockito.mock(Activity.class);
        Resources resources = Mockito.mock(Resources.class);
        Mockito.when(activity.getApplicationContext()).thenReturn(ApplicationProvider.getApplicationContext());
        Mockito.when(activity.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(null);
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(activity.getPackageName()).thenReturn("test.app");
        Mockito.when(resources.getIdentifier(eq("com_auth0_client_id"), eq("string"), eq("test.app"))).thenReturn(1);
        Mockito.when(resources.getIdentifier(eq("com_auth0_domain"), eq("string"), eq("test.app"))).thenReturn(2);
        Mockito.when(activity.getString(1)).thenReturn("clientId");
        Mockito.when(activity.getString(2)).thenReturn("domain");

        PasswordlessLock.Builder builder = PasswordlessLock.newBuilder(callback);
        builder.build(activity);
    }
}