package com.auth0.android.lock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.internal.configuration.PasswordlessMode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PasswordlessIdentityHelperTest {

    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @SuppressLint("CommitPrefEdits")
    @Before
    public void setUp() throws Exception {
        context = Mockito.mock(Context.class);
        sp = Mockito.mock(SharedPreferences.class);
        editor = Mockito.mock(SharedPreferences.Editor.class);
        when(context.getSharedPreferences(eq("Lock"), eq(Context.MODE_PRIVATE))).thenReturn(sp);
        when(sp.edit()).thenReturn(editor);
        when(editor.putString(anyString(), anyString())).thenReturn(editor);
        when(editor.putInt(anyString(), anyInt())).thenReturn(editor);
    }

    @Test
    public void shouldSaveIdentity() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_CODE);
        helper.saveIdentity("me@auth0.com", null);

        verify(editor).putString("last_passwordless_identity", "me@auth0.com");
        verify(editor).putString("last_passwordless_country", null);
        verify(editor).putInt("last_passwordless_mode", PasswordlessMode.SMS_CODE);
    }

    @Test
    public void shouldSaveIdentityWithCountry() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_CODE);
        helper.saveIdentity("1234567890", new Country("ar", "54"));

        verify(editor).putString("last_passwordless_identity", "1234567890");
        verify(editor).putString("last_passwordless_country", "ar@54");
        verify(editor).putInt("last_passwordless_mode", PasswordlessMode.SMS_CODE);
    }

    @Test
    public void shouldGetSavedIdentity() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_CODE);
        when(sp.getString(eq("last_passwordless_identity"), anyString())).thenReturn("me@auth0.com");

        String identity = helper.getLastIdentity();
        assertThat(identity, is("me@auth0.com"));
    }

    @Test
    public void shouldGetSavedCountry() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_CODE);
        when(sp.getString(eq("last_passwordless_country"), anyString())).thenReturn("ar@54");

        Country country = helper.getLastCountry();
        assertThat(country, is(notNullValue()));
        assertThat(country.getDialCode(), is("54"));
        assertThat(country.getIsoCode(), is("ar"));
    }

    @Test
    public void shouldNotHaveLoggedInBeforeIfCurrentPasswordlessModeIsDisabled() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.DISABLED);
        assertThat(helper.hasLoggedInBefore(), is(false));
    }

    @Test
    public void shouldNotHaveLoggedInBeforeIfLastPasswordlessModeIsDisabled() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_CODE);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.DISABLED);
        assertThat(helper.hasLoggedInBefore(), is(false));
    }

    @Test
    public void shouldNotHaveLoggedInBeforeOnDifferentConnections() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_CODE);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_CODE);
        assertThat(helper.hasLoggedInBefore(), is(false));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_LINK);
        assertThat(helper.hasLoggedInBefore(), is(false));

        helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_LINK);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_CODE);
        assertThat(helper.hasLoggedInBefore(), is(false));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_LINK);
        assertThat(helper.hasLoggedInBefore(), is(false));

        helper = new PasswordlessIdentityHelper(context, PasswordlessMode.EMAIL_CODE);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_CODE);
        assertThat(helper.hasLoggedInBefore(), is(false));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_LINK);
        assertThat(helper.hasLoggedInBefore(), is(false));

        helper = new PasswordlessIdentityHelper(context, PasswordlessMode.EMAIL_LINK);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_CODE);
        assertThat(helper.hasLoggedInBefore(), is(false));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_LINK);
        assertThat(helper.hasLoggedInBefore(), is(false));
    }

    @Test
    public void shouldHaveLoggedInBeforeOnSameConnections() throws Exception {
        PasswordlessIdentityHelper helper = new PasswordlessIdentityHelper(context, PasswordlessMode.EMAIL_CODE);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_CODE);
        assertThat(helper.hasLoggedInBefore(), is(true));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_LINK);
        assertThat(helper.hasLoggedInBefore(), is(true));

        helper = new PasswordlessIdentityHelper(context, PasswordlessMode.EMAIL_LINK);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_CODE);
        assertThat(helper.hasLoggedInBefore(), is(true));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.EMAIL_LINK);
        assertThat(helper.hasLoggedInBefore(), is(true));

        helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_CODE);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_CODE);
        assertThat(helper.hasLoggedInBefore(), is(true));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_LINK);
        assertThat(helper.hasLoggedInBefore(), is(true));

        helper = new PasswordlessIdentityHelper(context, PasswordlessMode.SMS_LINK);
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_CODE);
        assertThat(helper.hasLoggedInBefore(), is(true));
        when(sp.getInt(eq("last_passwordless_mode"), anyInt())).thenReturn(PasswordlessMode.SMS_LINK);
        assertThat(helper.hasLoggedInBefore(), is(true));
    }
}