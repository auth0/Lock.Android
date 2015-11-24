/*
 * LockContext.java
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
import android.util.Log;

/**
 * Class that provides the {@link com.auth0.lock.Lock} instance
 */
public class LockContext {

    private static final String TAG = LockContext.class.getName();

    static Lock sLock;

    private LockContext() {
        // private constructor
    }

    /**
     * Initializes this object's Lock from a builder
     * @param builder the builder
     */
    public static void configureLock(Lock.Builder builder) {
        sLock = builder.build();
    }

    /**
     * Returns the Lock provided by the Activity's Application when it implements LockProvider
     * interface, otherwise returns the local instance that should have been configured with
     * configureLock()
     * @see configureLock
     * @param activity the activity where Lock is used (if applicable), or null
     * @return the Lock instance
     */
    public static Lock getLock(Activity activity) {
        if (activity != null) {
            Application application = activity.getApplication();
            if (application instanceof LockProvider) {
                LockProvider provider = (LockProvider) application;
                Log.i(TAG, "Returning Application configured Lock");
                return provider.getLock();
            }
        }

        Log.i(TAG, "Returning LockContext configured lock");
        return sLock;
    }
}
