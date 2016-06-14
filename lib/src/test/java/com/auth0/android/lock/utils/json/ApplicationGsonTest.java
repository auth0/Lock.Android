/*
 * ApplicationGsonTest.java
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

package com.auth0.android.lock.utils.json;

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

public class ApplicationGsonTest extends GsonBaseTest {
    private static final String APPLICATION = "src/test/resources/application.json";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        gson = createGson();
    }

    @Test
    public void shouldFailWithEmptyJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildApplicationFrom(json(EMPTY_OBJECT));
    }

    @Test
    public void shouldFailWithInvalidJson() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildApplicationFrom(json(INVALID));
    }

    @Test
    public void shouldRequireId() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildApplicationFrom(new StringReader("{\"tenant\":\"samples\",\"authorize\":\"https://samples.auth0.com/authorize\",\"strategies\":[{\"name\":\"twitter\",\"connections\":[{\"name\":\"twitter\"}]}]}"));
    }

    @Test
    public void shouldRequireTenant() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildApplicationFrom(new StringReader("{\"id\":\"CBBlULbbyQHSVWj5EqZSTMhUrJAS3UFA\",\"authorize\":\"https://samples.auth0.com/authorize\",\"strategies\":[{\"name\":\"twitter\",\"connections\":[{\"name\":\"twitter\"}]}]}"));
    }

    @Test
    public void shouldRequireAuthorize() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildApplicationFrom(new StringReader("{\"id\":\"CBBlULbbyQHSVWj5EqZSTMhUrJAS3UFA\",\"tenant\":\"samples\",\"strategies\":[{\"name\":\"twitter\",\"connections\":[{\"name\":\"twitter\"}]}]}"));
    }

    @Test
    public void shouldRequireStrategies() throws Exception {
        expectedException.expect(JsonParseException.class);
        buildApplicationFrom(new StringReader("{\"id\":\"CBBlULbbyQHSVWj5EqZSTMhUrJAS3UFA\",\"tenant\":\"samples\",\"authorize\":\"https://samples.auth0.com/authorize\"}"));
    }

    @Test
    public void shouldReturnApplication() throws Exception {
        final Application application = buildApplicationFrom(json(APPLICATION));
        assertThat(application, is(notNullValue()));
        assertThat(application.getId(), is("CBBlULbbyQHSVWj5EqZSTMhUrJAS3UFA"));
        assertThat(application.getTenant(), is("samples"));
        assertThat(application.getAuthorizeURL(), is("https://samples.auth0.com/authorize"));
        assertThat(application.getStrategies(), is(notNullValue()));
        assertThat(application.getStrategies(), IsCollectionWithSize.hasSize(1));
        assertThat(application.getStrategies().get(0), instanceOf(Strategy.class));
        assertThat(application.getSubscription(), is("dev"));
        assertThat(application.getCallbackURL(), is("http://localhost:3000/"));
        assertThat(application.hasAllowedOrigins(), is(true));
    }

    private Application buildApplicationFrom(Reader json) throws IOException {
        return pojoFrom(json, Application.class);
    }

}
