/*
 * StrategyGsonTest.java
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

package com.auth0.android.lock.utils;

import com.auth0.android.lock.utils.json.JsonUtils;
import com.google.gson.JsonParseException;

import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class StrategyGsonTest extends GsonBaseTest {
    private static final String STRATEGY = "src/test/resources/strategy.json";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        gson = JsonUtils.createGson();
    }

    @Test
    public void shouldFailWithEmptyJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildStrategyFrom(json(EMPTY_OBJECT));
    }

    @Test
    public void shouldFailWithInvalidJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildStrategyFrom(json(INVALID));
    }

    @Test
    public void shouldRequireName() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildStrategyFrom(new StringReader("{\"connections\": \"[]\"}"));
    }

    @Test
    public void shouldRequireConnections() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildStrategyFrom(new StringReader("{\"name\": \"auth0\"}"));
    }

    @Test
    public void shouldReturnStrategy() throws Exception {
        final Strategy strategy = buildStrategyFrom(json(STRATEGY));
        assertThat(strategy, is(notNullValue()));
        assertThat(strategy.getName(), is("twitter"));
        assertThat(strategy.getConnections(), is(notNullValue()));
        assertThat(strategy.getConnections(), IsCollectionWithSize.hasSize(1));
        assertThat(strategy.getConnections().get(0), instanceOf(Connection.class));
    }


    private Strategy buildStrategyFrom(Reader json) throws IOException {
        return pojoFrom(json, Strategy.class);
    }

}
