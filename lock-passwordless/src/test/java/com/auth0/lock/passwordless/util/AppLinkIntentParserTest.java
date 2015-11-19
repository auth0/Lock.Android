/*
 * AppLinkIntentParserTest.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.lock.passwordless.util;

import android.content.Intent;
import android.net.Uri;

import com.auth0.android.BuildConfig;
import com.auth0.lock.passwordless.LockPasswordlessActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class AppLinkIntentParserTest {

    @Test
    public void testGetModeFromAppLink() throws Exception {
        Intent validEmailIntent = new Intent(Intent.ACTION_VIEW);
        validEmailIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=234567"));
        assertEquals(LockPasswordlessActivity.MODE_EMAIL_MAGIC_LINK,
                new AppLinkIntentParser(validEmailIntent).getModeFromAppLink());

        Intent validSmsIntent = new Intent(Intent.ACTION_VIEW);
        validSmsIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/sms?code=234567"));
        assertEquals(LockPasswordlessActivity.MODE_SMS_MAGIC_LINK,
                new AppLinkIntentParser(validSmsIntent).getModeFromAppLink());

        Intent emptyIntent = new Intent();
        assertEquals(LockPasswordlessActivity.MODE_UNKNOWN,
                new AppLinkIntentParser(emptyIntent).getModeFromAppLink());

        Intent emptyViewIntent = new Intent(Intent.ACTION_VIEW);
        assertEquals(LockPasswordlessActivity.MODE_UNKNOWN,
                new AppLinkIntentParser(emptyViewIntent).getModeFromAppLink());

        Intent invalidIntent = new Intent(Intent.ACTION_VIEW);
        invalidIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/other?code=234567"));
        assertEquals(LockPasswordlessActivity.MODE_UNKNOWN,
                new AppLinkIntentParser(invalidIntent).getModeFromAppLink());
    }

    @Test
    public void testGetCodeFromAppLinkUri() throws Exception {
        AppLinkIntentParser linkParser = new AppLinkIntentParser();
        assertNull(linkParser.getCodeFromAppLinkUri(null));
        assertNull(linkParser.getCodeFromAppLinkUri(Uri.parse("")));
        assertNull(linkParser.getCodeFromAppLinkUri(Uri.parse("http://example.com/")));
        assertNull(linkParser.getCodeFromAppLinkUri(Uri.parse("thisshouldreturnnull")));
        assertEquals("567234", linkParser.getCodeFromAppLinkUri(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=567234")));
    }

    @Test
    public void testGetCodeFromAppLinkIntent() throws Exception {
        Intent validIntent = new Intent(Intent.ACTION_VIEW);
        validIntent.setData(Uri.parse("https://tenant.auth0.com/android/com.example.app/email?code=234567"));
        assertEquals("234567", new AppLinkIntentParser(validIntent).getCodeFromAppLinkIntent());

        Intent invalidIntent = new Intent();
        assertNull(new AppLinkIntentParser(invalidIntent).getCodeFromAppLinkIntent());

        invalidIntent.setAction(Intent.ACTION_VIEW);
        assertNull(new AppLinkIntentParser(invalidIntent).getCodeFromAppLinkIntent());

        invalidIntent.setData(Uri.parse("https://example.com/"));
        assertNull(new AppLinkIntentParser(invalidIntent).getCodeFromAppLinkIntent());
    }
}