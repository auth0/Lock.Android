/*
 * LockContextTest.java
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

package com.auth0.lock;

import android.app.Activity;
import android.app.Application;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class LockContextTest {

    @Test
    public void shouldReturnNullLock() throws Exception {
        // not configured yet, so should return null
        assertNull(LockContext.getLock(null));
    }

    @Test
    public void shouldReturnNullLockFromActivity() throws Exception {
        // application doesn't implement LockProvider, so should also return null
        Activity activity = new Activity();
        assertNull(LockContext.getLock(activity));
    }

    @Test
    public void shouldReturnLockContextConfiguredLock() throws Exception {
        assertNull(LockContext.getLock(null));

        Lock.Builder builder = new Lock.Builder()
                .clientId("client-id")
                .domainUrl("domain-url");

        LockContext.configureLock(builder);

        assertNotNull(LockContext.getLock(null));

        // reset state
        LockContext.lockInstance = null;
        assertNull(LockContext.getLock(null));
    }

    @Config(application = LockProviderApplication.class)
    @Test
    public void shouldReturnApplicationConfiguredLock() throws Exception {
        assertNull(LockContext.getLock(null));

        Activity activity = new Activity();
        Lock appLock = LockContext.getLock(activity);
        assertNotNull(appLock);
        assertEquals(appLock, ((LockProvider) activity.getApplication()).getLock());
    }

    @Config(application = LockProviderApplication.class)
    @Test
    public void shouldReturnDifferentLocks() throws Exception {
        assertNull(LockContext.getLock(null));

        Activity activity = new Activity();
        Lock appLock = LockContext.getLock(activity);
        assertNotNull(appLock);

        Lock.Builder builder = new Lock.Builder()
                .clientId("client-id")
                .domainUrl("domain-url");
        LockContext.configureLock(builder);
        Lock builderLock = LockContext.getLock(null);

        assertNotEquals(appLock, builderLock);

        // reset state
        LockContext.lockInstance = null;
        assertNull(LockContext.getLock(null));
    }

    static public class LockProviderApplication extends Application implements LockProvider {

        private Lock lock;

        @Override
        public void onCreate() {
            super.onCreate();
            lock = new Lock.Builder()
                    //.loadFromApplication(this)
                    .clientId("client-id-app")
                    .domainUrl("domain-url-app")
                    .closable(true)
                    .useEmail(true)
                    .fullscreen(false)
                    .useWebView(true)
                    .build();
        }

        @Override
        public Lock getLock() {
            return lock;
        }
    }
}