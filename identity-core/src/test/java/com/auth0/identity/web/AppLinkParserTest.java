package com.auth0.identity.web;

import android.content.Intent;
import android.net.Uri;

import com.auth0.android.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class AppLinkParserTest {

    AppLinkParser linkParser;

    @Before
    public void setUp() throws Exception {
        linkParser = new AppLinkParser();
    }

    @Test
    public void testAppLinkTypeFromIntent() throws Exception {
        Intent validEmailIntent = new Intent(Intent.ACTION_VIEW);
        validEmailIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=234567"));
        assertEquals(AppLinkParser.TYPE_EMAIL, linkParser.getAppLinkTypeFromIntent(validEmailIntent));

        Intent validSmsIntent = new Intent(Intent.ACTION_VIEW);
        validSmsIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/sms?code=234567"));
        assertEquals(AppLinkParser.TYPE_SMS, linkParser.getAppLinkTypeFromIntent(validSmsIntent));

        Intent emptyIntent = new Intent();
        assertEquals(AppLinkParser.TYPE_INVALID, linkParser.getAppLinkTypeFromIntent(emptyIntent));

        Intent emptyViewIntent = new Intent(Intent.ACTION_VIEW);
        assertEquals(AppLinkParser.TYPE_INVALID, linkParser.getAppLinkTypeFromIntent(emptyViewIntent));

        Intent invalidIntent = new Intent(Intent.ACTION_VIEW);
        invalidIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/other?code=234567"));
        assertEquals(AppLinkParser.TYPE_INVALID, linkParser.getAppLinkTypeFromIntent(invalidIntent));
    }

    @Test
    public void testCodeFromAppLinkUri() throws Exception {
        assertNull(linkParser.getCodeFromAppLinkUri(null));
        assertNull(linkParser.getCodeFromAppLinkUri(Uri.parse("")));
        assertNull(linkParser.getCodeFromAppLinkUri(Uri.parse("http://example.com/")));
        assertNull(linkParser.getCodeFromAppLinkUri(Uri.parse("thisshouldreturnnull")));
        assertEquals("567234", linkParser.getCodeFromAppLinkUri(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=567234")));
    }

    @Test
    public void testCodeFromAppLinkIntent() throws Exception {
        Intent validIntent = new Intent(Intent.ACTION_VIEW);
        validIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=234567"));
        assertEquals("234567", linkParser.getCodeFromAppLinkIntent(validIntent));

        Intent invalidIntent = new Intent();
        assertNull(linkParser.getCodeFromAppLinkIntent(invalidIntent));

        invalidIntent.setAction(Intent.ACTION_VIEW);
        assertNull(linkParser.getCodeFromAppLinkIntent(invalidIntent));

        invalidIntent.setData(Uri.parse("https://example.com/"));
        assertNull(linkParser.getCodeFromAppLinkIntent(invalidIntent));
    }
}