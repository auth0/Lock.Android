package com.auth0.android.lock;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.content.res.Resources;

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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 23)
public class ClassicBuilderTest {

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
        Lock.Builder builder = Lock.newBuilder(account, null);
        builder.build(activity);
    }

    @Test
    public void shouldThrowIfAccountIsMissing() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("Missing Auth0 account information.");

        final Activity activity = Robolectric.buildActivity(Activity.class).create().get();
        Lock.Builder builder = Lock.newBuilder(null, callback);
        builder.build(activity);
    }

    @Test
    public void shouldThrowIfAccountIsMissingAlsoFromResources() {
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
    public void shouldCreateAccountFromResources() {
        Activity activity = Mockito.mock(Activity.class);
        Resources resources= Mockito.mock(Resources.class);
        Mockito.when(activity.getApplicationContext()).thenReturn(RuntimeEnvironment.application);
        Mockito.when(activity.registerReceiver(any(BroadcastReceiver.class), any(IntentFilter.class))).thenReturn(null);
        Mockito.when(activity.getResources()).thenReturn(resources);
        Mockito.when(resources.getIdentifier(anyString(), anyString(), anyString())).thenReturn(1);
        //noinspection ResourceType
        Mockito.when(activity.getString(1)).thenReturn("asd");

        Lock.Builder builder = Lock.newBuilder(callback);
        builder.build(activity);
    }

    @Test
    public void shouldThrowIfAllScreensAreDisabled() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("You disabled all the Lock screens (LogIn/SignUp/ForgotPassword). Please enable at least one.");

        Lock.Builder builder = Lock.newBuilder(account, callback);
        builder.allowSignUp(false);
        builder.allowLogIn(false);
        builder.allowForgotPassword(false);
        builder.build(new Activity());
    }

    @Test
    public void shouldThrowIfInitialScreenIsLogInButIsDisabled() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("You chose LOG_IN as the initial screen but you have also disabled that screen.");

        Lock.Builder builder = Lock.newBuilder(account, callback);
        builder.allowLogIn(false);
        builder.initialScreen(InitialScreen.LOG_IN);
        builder.build(new Activity());
    }

    @Test
    public void shouldThrowIfInitialScreenIsSignUpButIsDisabled() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("You chose SIGN_UP as the initial screen but you have also disabled that screen.");

        Lock.Builder builder = Lock.newBuilder(account, callback);
        builder.allowSignUp(false);
        builder.initialScreen(InitialScreen.SIGN_UP);
        builder.build(new Activity());
    }

    @Test
    public void shouldThrowIfInitialScreenIsForgotPasswordButIsDisabled() {
        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("You chose FORGOT_PASSWORD as the initial screen but you have also disabled that screen.");

        Lock.Builder builder = Lock.newBuilder(account, callback);
        builder.allowForgotPassword(false);
        builder.initialScreen(InitialScreen.FORGOT_PASSWORD);
        builder.build(new Activity());
    }
}