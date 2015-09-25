/*
 * CallbackHandlerTest.java
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

package com.auth0.api.okhttp;

import android.os.Handler;

import com.auth0.android.BuildConfig;
import com.auth0.api.callback.BaseCallback;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class CallbackHandlerTest {

    private CallbackHandler<String> callbackHandler;

    @Mock
    private Handler handler;
    @Mock
    private BaseCallback<String> callback;
    @Mock
    private Throwable throwable;
    @Captor
    private ArgumentCaptor<Runnable> captor;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        callbackHandler = new CallbackHandler<>(handler, callback);
    }

    @Test
    public void shouldPostOnSuccess() throws Exception {
        callbackHandler.postOnSuccess("OK");
        verify(handler).post(captor.capture());
        Runnable runnable = captor.getValue();
        runnable.run();
        verify(callback).onSuccess(eq("OK"));
        verifyNoMoreInteractions(callback, handler);
    }

    @Test
    public void shouldPostOnFailure() throws Exception {
        callbackHandler.postOnFailure(throwable);
        verify(handler).post(captor.capture());
        Runnable runnable = captor.getValue();
        runnable.run();
        verify(callback).onFailure(eq(throwable));
        verifyNoMoreInteractions(callback, handler);
    }
}