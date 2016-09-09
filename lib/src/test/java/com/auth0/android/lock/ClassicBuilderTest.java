package com.auth0.android.lock;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.util.Telemetry;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = android.support.v7.appcompat.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class ClassicBuilderTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Mock
    Auth0 account;
    @Mock
    LockCallback callback;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldThrowIfCallbackIsMissing() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing callback.");

        Lock.Builder builder = Lock.newBuilder(account, null);
        builder.build(new Activity());
    }

    @Test
    public void shouldThrowIfAccountIsMissing() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing Auth0 account information.");

        Lock.Builder builder = Lock.newBuilder(null, callback);
        builder.build(new Activity());
    }

    @Test
    public void shouldThrowIfAccountIsMissingAlsoFromResources() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing Auth0 account information.");

        Activity activity = Mockito.mock(Activity.class);
        Resources resources= Mockito.mock(Resources.class);
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(resources.getIdentifier(anyString(), anyString(), anyString())).thenReturn(0);

        Lock.Builder builder = Lock.newBuilder(callback);
        builder.build(activity);
    }

    @Test
    public void shouldCreateAccountFromResources() throws Exception {
        Activity activity = Mockito.mock(Activity.class);
        Resources resources= Mockito.mock(Resources.class);
        Mockito.when(activity.getApplicationContext()).thenReturn(new Application());
        Mockito.when(activity.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(null);
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(resources.getIdentifier(anyString(), anyString(), anyString())).thenReturn(1);
        //noinspection ResourceType
        Mockito.when(activity.getString(1)).thenReturn("asd");

        Lock.Builder builder = Lock.newBuilder(callback);
        builder.build(activity);
    }

    @Test
    public void shouldThrowIfAllScreensAreDisabled() throws Exception {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("You disabled all the Lock screens (LogIn/SignUp/ForgotPassword). Please enable at least one.");

        Lock.Builder builder = Lock.newBuilder(account, callback);
        builder.allowSignUp(false);
        builder.allowLogIn(false);
        builder.allowForgotPassword(false);
        builder.build(new Activity());
    }
}