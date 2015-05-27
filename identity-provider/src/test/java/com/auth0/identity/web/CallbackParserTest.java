/*
 * CallbackParserTest.java
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

package com.auth0.identity.web;

import android.net.Uri;

import com.auth0.android.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class CallbackParserTest {

    private static final String VALUE_STRING = "access_token=dAj4h5dZk93J56jm&token_type=Bearer&state=HvzHSoleBlWp63fi";
    private static final String TOKEN_TYPE_VALUE = "Bearer";
    private static final String ACCESS_TOKEN_VALUE = "dAj4h5dZk93J56jm";
    private static final String STATE_VALUE = "HvzHSoleBlWp63fi";
    private static final String TOKEN_TYPE = "token_type";
    private static final String ACCESS_TOKEN = "access_token";
    private static final String STATE = "state";
    
    private CallbackParser parser;

    @Mock
    private Uri uri;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        parser = new CallbackParser();
    }

    @Test
    public void shouldParseQueryParameters() throws Exception {
        when(uri.getQuery()).thenReturn(VALUE_STRING);
        final Map<String, String> values = parser.getValuesFromUri(uri);
        assertThat(values, hasEntry(TOKEN_TYPE, TOKEN_TYPE_VALUE));
        assertThat(values, hasEntry(ACCESS_TOKEN, ACCESS_TOKEN_VALUE));
        assertThat(values, hasEntry(STATE, STATE_VALUE));
    }

    @Test
    public void shouldParseFragmentParameters() throws Exception {
        when(uri.getFragment()).thenReturn(VALUE_STRING);
        final Map<String, String> values = parser.getValuesFromUri(uri);
        assertThat(values, hasEntry(TOKEN_TYPE, TOKEN_TYPE_VALUE));
        assertThat(values, hasEntry(ACCESS_TOKEN, ACCESS_TOKEN_VALUE));
        assertThat(values, hasEntry(STATE, STATE_VALUE));
    }

    @Test
    public void shouldParseOneQueryParameter() throws Exception {
        when(uri.getQuery()).thenReturn("key=value");
        final Map<String, String> values = parser.getValuesFromUri(uri);
        assertThat(values, hasEntry("key", "value"));
    }

    @Test
    public void shouldSkipInvalidEntriesInQueryParameter() throws Exception {
        when(uri.getQuery()).thenReturn("key=value&missing");
        final Map<String, String> values = parser.getValuesFromUri(uri);
        assertThat(values, hasEntry("key", "value"));
        assertThat(values.size(), equalTo(1));
    }

    @Test
    public void shouldReturnEmptyMapWithNullQuery() throws Exception {
        assertThat(parser.getValuesFromUri(uri).size(), equalTo(0));
    }

    @Test
    public void shouldReturnEmptyMapWithMalformedQuery() throws Exception {
        when(uri.getQuery()).thenReturn("pepe&p");
        assertThat(parser.getValuesFromUri(uri).size(), equalTo(0));
    }

}
