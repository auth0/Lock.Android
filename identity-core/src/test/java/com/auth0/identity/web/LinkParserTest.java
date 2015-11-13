package com.auth0.identity.web;

import android.content.Intent;
import android.net.Uri;

import com.auth0.android.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class LinkParserTest {

    @Test
    public void testCodeFromAppLinkUri() throws Exception {
        assertNull(LinkParser.getCodeFromAppLinkUri(null));
        assertNull(LinkParser.getCodeFromAppLinkUri(""));
        assertNull(LinkParser.getCodeFromAppLinkUri("http://example.com/"));
        assertNull(LinkParser.getCodeFromAppLinkUri("thisshouldreturnnull"));
        assertEquals("567234", LinkParser.getCodeFromAppLinkUri("https://tenant.auth0.com/android/com.example.app/email?code=567234"));
        assertNotEquals("234567", LinkParser.getCodeFromAppLinkUri("https://tenant.auth0.com/android/com.example.app/email?code=567234"));
    }

    @Test
    public void testCodeFromAppLinkIntent() throws Exception {
        Intent validIntent = new Intent(Intent.ACTION_VIEW);
        validIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=234567"));
        assertEquals("234567", LinkParser.getCodeFromAppLinkIntent(validIntent));

        Intent invalidIntent = new Intent();
        assertNull(LinkParser.getCodeFromAppLinkIntent(invalidIntent));

        invalidIntent.setAction(Intent.ACTION_VIEW);
        assertNull(LinkParser.getCodeFromAppLinkIntent(invalidIntent));

        invalidIntent.setData(Uri.parse("https://example.com/"));
        assertNull(LinkParser.getCodeFromAppLinkIntent(invalidIntent));
    }
}