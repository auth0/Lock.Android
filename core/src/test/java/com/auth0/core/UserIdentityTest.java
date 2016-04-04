/*
 * UserIdentityTest.java
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

package com.auth0.core;

import com.auth0.android.BuildConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class UserIdentityTest {

    private static final String USER_ID = "1234567890abc";
    private static final String USERNAME_PASSWORD_AUTHENTICATION = "Username-PasswordAuthentication";
    private static final String AUTH0 = "auth0";

    @Test
    public void shouldCreateObjectFromRequiredFieldsOnly() throws Exception {
        String json = "{ \"user_id\": \"" +
                USER_ID +
                "\", \"connection\": \"" +
                USERNAME_PASSWORD_AUTHENTICATION +
                "\", \"provider\": \"" +
                AUTH0 +
                "\"}";
        final UserIdentity identity = new ObjectMapper().readValue(json, UserIdentity.class);
        assertThat(identity, is(notNullValue()));
        assertThat(identity.getId(), equalTo(USER_ID));
        assertThat(identity.getProvider(), equalTo(AUTH0));
        assertThat(identity.getConnection(), equalTo(USERNAME_PASSWORD_AUTHENTICATION));
    }

    @Test
    public void shouldSkipInvalidIsSocialFlag() throws Exception {
        String json = "{ \"user_id\": \"" +
                USER_ID +
                "\", \"connection\": \"" +
                USERNAME_PASSWORD_AUTHENTICATION +
                "\", \"provider\": \"" +
                AUTH0 +
                "\", \"isSocial\": \"null\" }";
        final UserIdentity identity = new ObjectMapper().readValue(json, UserIdentity.class);
        assertThat(identity, is(notNullValue()));
        assertThat(identity.getId(), equalTo(USER_ID));
        assertThat(identity.getProvider(), equalTo(AUTH0));
        assertThat(identity.getConnection(), equalTo(USERNAME_PASSWORD_AUTHENTICATION));
    }

    @Test
    public void shouldHandleInvalidIsSocialFromMap() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("user_id", USER_ID);
        map.put("connection", USERNAME_PASSWORD_AUTHENTICATION);
        map.put("provider", AUTH0);
        map.put("isSocial", "null");
        final UserIdentity identity = new UserIdentity(map);
        assertThat(identity, is(notNullValue()));
        assertThat(identity.isSocial(), is(false));
    }

    @Test
    public void shouldHandleNoIsSocialFromMap() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("user_id", USER_ID);
        map.put("connection", USERNAME_PASSWORD_AUTHENTICATION);
        map.put("provider", AUTH0);
        final UserIdentity identity = new UserIdentity(map);
        assertThat(identity, is(notNullValue()));
        assertThat(identity.isSocial(), is(false));
    }

    @Test
    public void shouldHandleStringIdentifier() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("user_id", USER_ID);
        map.put("connection", USERNAME_PASSWORD_AUTHENTICATION);
        map.put("provider", AUTH0);
        final UserIdentity identity = new UserIdentity(map);
        assertThat(identity, is(notNullValue()));
        assertThat(identity.getId(), equalTo(USER_ID));
    }

    @Test
    public void shouldHandleNonStringIdentifier() throws Exception {
        final Map<String, Object> map = new HashMap<>();
        map.put("user_id", 1234567890);
        map.put("connection", USERNAME_PASSWORD_AUTHENTICATION);
        map.put("provider", AUTH0);
        final UserIdentity identity = new UserIdentity(map);
        assertThat(identity, is(notNullValue()));
        assertThat(identity.getId(), equalTo("1234567890"));
    }

}