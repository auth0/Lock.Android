/*
 * AuthenticationAPIClientTest.java
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

package com.auth0.api;

import com.auth0.android.BuildConfig;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Application;
import com.auth0.core.Auth0;
import com.squareup.okhttp.mockwebserver.MockResponse;
import com.squareup.okhttp.mockwebserver.MockWebServer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.concurrent.Callable;

import static com.jayway.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class AuthenticationAPIClientTest {

    private static final String CLIENT_ID = "CLIENTID";
    private static final String DOMAIN = "samples.auth0.com";

    private AuthenticationAPIClient client;

    private MockWebServer server;

    @Before
    public void setUp() throws Exception {
        server = new MockWebServer();
        server.start();
        final String domain = server.url("/").toString();
        Auth0 auth0 = new Auth0(CLIENT_ID, domain, domain);
        client = new AuthenticationAPIClient(auth0);
    }

    @After
    public void tearDown() throws Exception {
        server.shutdown();
    }

    @Test
    public void shouldCreateClientWithAccountInfo() throws Exception {
        AuthenticationAPIClient client = new AuthenticationAPIClient(new Auth0(CLIENT_ID, DOMAIN));
        assertThat(client, is(notNullValue()));
        assertThat(client.getClientId(), equalTo(CLIENT_ID));
        assertThat(client.getBaseURL(), equalTo("https://samples.auth0.com"));
    }

    @Test
    public void shouldLoadApplicationInfoFromConfigurationUrl() throws Exception {
        server.enqueue(validApplicationInfoResponse());

        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);

        assertThat(server.takeRequest().getPath(), equalTo("/client/CLIENTID.js"));
        await().until(callback.payload(), is(notNullValue()));
        await().until(callback.error(), is(nullValue()));
        Application application = client.getApplication();
        assertThat(application.getId(), equalTo(CLIENT_ID));
        assertThat(application.getStrategies(), hasSize(1));
    }

    @Test
    public void shoulFailWithInvalidJSON() throws Exception {
        server.enqueue(applicationResponseWithBody("Auth0Client.set({ })", 200));
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);
        await().until(callback.payload(), is(nullValue()));
        await().until(callback.error(), is(notNullValue()));
    }

    @Test
    public void shoulFailWithInvalidJSONP() throws Exception {
        server.enqueue(applicationResponseWithBody("INVALID_JSONP", 200));
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);
        await().until(callback.payload(), is(nullValue()));
        await().until(callback.error(), is(notNullValue()));
    }

    @Test
    public void shoulFailWithFailedStatusCode() throws Exception {
        server.enqueue(applicationResponseWithBody("Not Found", 404));
        final MockBaseCallback<Application> callback = new MockBaseCallback<>();
        client.fetchApplicationInfo(callback);
        await().until(callback.payload(), is(nullValue()));
        await().until(callback.error(), is(notNullValue()));
    }

    private MockResponse validApplicationInfoResponse() {
        return applicationResponseWithBody("Auth0.setClient({\"id\":\"CLIENTID\",\"tenant\":\"overmind\",\"subscription\":\"free\",\"authorize\":\"https://samples.auth0.com/authorize\",\"callback\":\"http://localhost:3000/\",\"hasAllowedOrigins\":true,\"strategies\":[{\"name\":\"twitter\",\"connections\":[{\"name\":\"twitter\"}]}]});", 200);
    }

    private MockResponse applicationResponseWithBody(String body, int statusCode) {
        return new MockResponse()
                .setResponseCode(statusCode)
                .addHeader("Content-Type", "application/x-javascript")
                .setBody(body);
    }

    private static class MockBaseCallback<T> implements BaseCallback<T> {

        private T payload;
        private Throwable error;

        @Override
        public void onSuccess(T payload) {
            this.payload = payload;
        }

        @Override
        public void onFailure(Throwable error) {
            this.error = error;
        }

        Callable<T> payload() {
            return new Callable<T>() {
                @Override
                public T call() throws Exception {
                    return payload;
                }
            };
        }

        Callable<Throwable> error() {
            return new Callable<Throwable>() {
                @Override
                public Throwable call() throws Exception {
                    return error;
                }
            };
        }
    }
}