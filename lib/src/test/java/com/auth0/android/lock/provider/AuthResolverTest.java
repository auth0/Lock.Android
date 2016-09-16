/*
 * ProviderResolverManagerTest.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
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

package com.auth0.android.lock.provider;

import com.auth0.android.provider.AuthHandler;
import com.auth0.android.provider.AuthProvider;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = com.auth0.android.lock.BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class AuthResolverTest {

    @Test
    public void shouldHaveReturnNullWhenNoAuthHandlers() {
        assertThat(AuthResolver.providerFor("", ""), nullValue());
    }

    @Test
    public void shouldKeepACopyOfTheList() {
        AuthProvider aProvider = Mockito.mock(AuthProvider.class);
        AuthProvider bProvider = Mockito.mock(AuthProvider.class);
        AuthHandler aHandler = Mockito.mock(AuthHandler.class);
        AuthHandler bHandler = Mockito.mock(AuthHandler.class);
        Mockito.when(aHandler.providerFor("aStrategy", "aConnection")).thenReturn(aProvider);
        Mockito.when(bHandler.providerFor("bStrategy", "bConnection")).thenReturn(bProvider);

        List<AuthHandler> list = new ArrayList<>();
        list.add(aHandler);
        list.add(bHandler);
        AuthResolver.setAuthHandlers(list);
        list.clear();

        assertThat(AuthResolver.providerFor("aStrategy", "aConnection"), is(equalTo(aProvider)));
        assertThat(AuthResolver.providerFor("bStrategy", "bConnection"), is(equalTo(bProvider)));
    }

    @Test
    public void shouldSetAuthHandlers() {
        AuthProvider aProvider = Mockito.mock(AuthProvider.class);
        AuthProvider bProvider = Mockito.mock(AuthProvider.class);
        AuthProvider cProvider = Mockito.mock(AuthProvider.class);
        AuthHandler abHandler = Mockito.mock(AuthHandler.class);
        AuthHandler cHandler = Mockito.mock(AuthHandler.class);
        Mockito.when(abHandler.providerFor("aStrategy", "aConnection")).thenReturn(aProvider);
        Mockito.when(abHandler.providerFor("bStrategy", "bConnection")).thenReturn(bProvider);
        Mockito.when(cHandler.providerFor("cStrategy", "cConnection")).thenReturn(cProvider);
        AuthResolver.setAuthHandlers(Arrays.asList(abHandler, cHandler));

        assertThat(AuthResolver.providerFor("aStrategy", "aConnection"), is(equalTo(aProvider)));
        assertThat(AuthResolver.providerFor("bStrategy", "bConnection"), is(equalTo(bProvider)));
        assertThat(AuthResolver.providerFor("cStrategy", "cConnection"), is(equalTo(cProvider)));
    }

    @Test
    public void shouldRespectAuthHandlersOrder() {
        AuthProvider aProvider = Mockito.mock(AuthProvider.class);
        AuthProvider bProvider = Mockito.mock(AuthProvider.class);

        AuthProvider cProvider = Mockito.mock(AuthProvider.class);
        AuthHandler firstHandler = Mockito.mock(AuthHandler.class);
        AuthHandler secondHandler = Mockito.mock(AuthHandler.class);
        Mockito.when(firstHandler.providerFor("sameStrategy", "sameConnection")).thenReturn(aProvider);
        Mockito.when(firstHandler.providerFor("differentStrategy", "differentConnection")).thenReturn(bProvider);
        Mockito.when(secondHandler.providerFor("sameStrategy", "sameConnection")).thenReturn(cProvider);
        AuthResolver.setAuthHandlers(Arrays.asList(secondHandler, firstHandler));

        assertThat(AuthResolver.providerFor("sameStrategy", "sameConnection"), is(equalTo(cProvider)));
        assertThat(AuthResolver.providerFor("differentStrategy", "differentConnection"), is(equalTo(bProvider)));
    }
}