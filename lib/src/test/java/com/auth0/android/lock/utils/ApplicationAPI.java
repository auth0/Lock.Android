/*
 * ApplicationAPI.java
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


import java.io.IOException;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;

public class ApplicationAPI {

    private MockWebServer server;

    public ApplicationAPI(SSLTestUtils sslUtils) throws IOException {
        this.server = sslUtils.createMockWebServer();
        this.server.start();
    }

    public String getDomain() {
        return server.url("/").toString();
    }

    public void shutdown() throws IOException {
        this.server.shutdown();
    }

    public RecordedRequest takeRequest() throws InterruptedException {
        return server.takeRequest();
    }

    public void willReturnValidJSONPResponse() {
        willReturnApplicationResponseWithBody("Auth0.setClient({\"id\":\"CLIENTID\",\"tenant\":\"overmind\",\"subscription\":\"free\",\"authorize\":\"https://samples.auth0.com/authorize\",\"callback\":\"http://localhost:3000/\",\"hasAllowedOrigins\":true,\"strategies\":[{\"name\":\"twitter\",\"connections\":[{\"name\":\"twitter\"}]}]});", 200);
    }

    public void willReturnInvalidJSONPLengthResponse() {
        server.enqueue(responseWithJSON("SHORTJSON", 200));
    }

    private void willReturnApplicationResponseWithBody(String body, int statusCode) {
        MockResponse response = new MockResponse()
                .setResponseCode(statusCode)
                .addHeader("Content-Type", "application/x-javascript")
                .setBody(body);
        server.enqueue(response);
    }

    private MockResponse responseWithJSON(String json, int statusCode) {
        return new MockResponse()
                .setResponseCode(statusCode)
                .addHeader("Content-Type", "application/json")
                .setBody(json);
    }

}
