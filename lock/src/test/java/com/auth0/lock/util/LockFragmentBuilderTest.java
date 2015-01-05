/*
 * LockFragmentBuilderTest.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.lock.util;

import com.auth0.core.Application;
import com.auth0.core.Strategy;
import com.auth0.lock.fragment.DatabaseLoginFragment;
import com.auth0.lock.fragment.DatabaseResetPasswordFragment;
import com.auth0.lock.fragment.DatabaseSignUpFragment;
import com.auth0.lock.fragment.SocialFragment;
import com.auth0.lock.util.LockFragmentBuilder;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Created by hernan on 12/16/14.
 */
@RunWith(RobolectricTestRunner.class)
@Config(emulateSdk = 18)
public class LockFragmentBuilderTest {

    private LockFragmentBuilder builder;

    @Mock
    private Application application;
    @Mock
    private Strategy socialStrategy;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        builder = new LockFragmentBuilder();
        builder.setApplication(application);
    }

    @Test
    public void shouldReturnDefaultRootFragment() throws Exception {
        assertThat(builder.root(), is(DatabaseLoginFragment.class));
    }

    @Test
    public void shouldReturnSocial() throws Exception {
        when(application.getSocialStrategies()).thenReturn(Arrays.asList(socialStrategy));
        assertThat(builder.root(), is(SocialFragment.class));
    }

    @Test
    public void shouldReturnSignUp() throws Exception {
        assertThat(builder.signUp(), is(DatabaseSignUpFragment.class));
    }

    @Test
    public void shouldReturnResetPassword() throws Exception {
        assertThat(builder.resetPassword(), is(DatabaseResetPasswordFragment.class));
    }
}
