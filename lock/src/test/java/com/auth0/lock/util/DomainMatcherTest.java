/*
 * DomainMatcherTest.java
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

package com.auth0.lock.util;

import com.auth0.android.BuildConfig;
import com.auth0.core.Connection;
import com.auth0.core.Strategies;
import com.auth0.core.Strategy;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Mockito.when;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class DomainMatcherTest {

    public static final String ONE_COM = "1.com";
    public static final String TWO_COM = "2.com";
    public static final String A_COM = "a.com";
    public static final String B_COM = "b.com";
    public static final String C_COM = "c.com";
    private DomainMatcher matcher;

    @Mock
    private Strategy adfs;
    @Mock
    private Strategy azureAd;
    @Mock
    private Strategy db;
    @Mock
    private Strategy sharepoint;
    @Mock
    private Strategy office365;
    @Mock
    private Connection adfsConnection;
    @Mock
    private Connection azureConnection;
    @Mock
    private Connection dbConnection;
    @Mock
    private Connection sharepointConnection;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(adfs.getType()).thenReturn(Strategies.Type.ENTERPRISE);
        when(adfs.getConnections()).thenReturn(Arrays.asList(adfsConnection));
        when(azureAd.getType()).thenReturn(Strategies.Type.ENTERPRISE);
        when(azureAd.getConnections()).thenReturn(Arrays.asList(azureConnection));
        when(db.getType()).thenReturn(Strategies.Type.DATABASE);
        when(db.getConnections()).thenReturn(Arrays.asList(dbConnection));
        when(sharepoint.getType()).thenReturn(Strategies.Type.ENTERPRISE);
        when(sharepoint.getConnections()).thenReturn(Arrays.asList(sharepointConnection));
        when(office365.getType()).thenReturn(Strategies.Type.ENTERPRISE);
        when(office365.getConnections()).thenReturn(new ArrayList<Connection>());
        when(sharepointConnection.getDomainSet()).thenReturn(new HashSet<String>());
        when(azureConnection.getDomainSet()).thenReturn(set(ONE_COM, TWO_COM));
        when(adfsConnection.getDomainSet()).thenReturn(set(A_COM, B_COM, C_COM));
        matcher = new DomainMatcher(Arrays.asList(adfs, azureAd));
    }

    @Test
    public void shouldPickOnlyEnterpriseAccounts() throws Exception {
        matcher = new DomainMatcher(Arrays.asList(adfs, db, azureAd));
        assertThat(matcher, is(notNullValue()));
        assertThat(matcher.domains.keySet(), hasItem(adfsConnection));
        assertThat(matcher.domains.keySet(), hasItem(azureConnection));
        assertThat(matcher.domains.keySet(), not(hasItem(dbConnection)));
    }

    @Test
    public void shouldStopDomainsInValues() throws Exception {
        matcher = new DomainMatcher(Arrays.asList(adfs, db, azureAd));
        assertThat(matcher, is(notNullValue()));
        assertThat(matcher.domains, hasEntry(adfsConnection, set(A_COM, B_COM, C_COM)));
        assertThat(matcher.domains, hasEntry(azureConnection, set(ONE_COM, TWO_COM)));
    }

    @Test
    public void shouldSkipConnectionWithNoDomain() throws Exception {
        matcher = new DomainMatcher(Arrays.asList(adfs, db, azureAd, sharepoint));
        assertThat(matcher.domains.keySet(), not(hasItem(sharepointConnection)));
    }

    @Test
    public void shouldSkipNoConnectionStrategy() throws Exception {
        matcher = new DomainMatcher(Arrays.asList(adfs, office365));
        assertThat(matcher.domains.keySet(), equalTo(set(adfsConnection)));
    }

    @Test
    public void shouldMatchWithEmailDomain() throws Exception {
        assertThat(matcher.matches("pepe@1.com"), is(true));
        assertThat(matcher.matches("pepe@2.com"), is(true));
        assertThat(matcher.matches("pepe@a.com"), is(true));
        assertThat(matcher.matches("pepe@b.com"), is(true));
        assertThat(matcher.matches("pepe@c.com"), is(true));
        assertThat(matcher.matches("pepe@gmail.com"), is(false));
    }

    @Test
    public void shouldMatchIgnoringCase() throws Exception {
        assertThat(matcher.matches("pepe@a.com".toUpperCase()), is(true));
    }

    @Test
    public void shouldNotMatchNull() throws Exception {
        assertThat(matcher.matches(null), is(false));
    }

    @Test
    public void shouldNotMatchInvalidEmail() throws Exception {
        assertThat(matcher.matches("pepe"), is(false));
        assertThat(matcher.matches("pepe@"), is(false));
        assertThat(matcher.matches("pepe@1"), is(false));
        assertThat(matcher.matches("pepe@a."), is(false));
        assertThat(matcher.matches("pepe@a.co"), is(false));
    }

    @Test
    public void shouldReturnMatchedConnection() throws Exception {
        matcher.matches("pepe@1.com");
        assertThat(matcher.getConnection(), equalTo(azureConnection));
        matcher.matches("pepe@a.com");
        assertThat(matcher.getConnection(), equalTo(adfsConnection));
        matcher.matches("");
        assertThat(matcher.getConnection(), is(nullValue()));
    }

    @Test
    public void shouldFilterConnection() throws Exception {
        matcher.filterConnection(azureConnection);
        assertThat(matcher.matches("pepe@1.com"), is(false));
    }

    @Test
    public void shouldDoNoFilterWithNullConnection() throws Exception {
        matcher.filterConnection(null);
        assertThat(matcher.matches("pepe@1.com"), is(true));
        assertThat(matcher.matches("pepe@2.com"), is(true));
        assertThat(matcher.matches("pepe@a.com"), is(true));
        assertThat(matcher.matches("pepe@b.com"), is(true));
        assertThat(matcher.matches("pepe@c.com"), is(true));
    }

    private <T> Set<T> set(T... values) {
        return new HashSet<>(Arrays.asList(values));
    }

}