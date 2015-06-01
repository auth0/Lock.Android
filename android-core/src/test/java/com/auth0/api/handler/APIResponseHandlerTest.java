/*
 * APIResponseHandlerTest.java
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

package com.auth0.api.handler;

import com.auth0.android.BuildConfig;
import com.auth0.api.APIClientException;
import com.auth0.api.callback.Callback;

import org.apache.http.Header;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static com.auth0.util.APIClientExceptionMatcher.hasErrorWith;
import static com.auth0.util.APIClientExceptionMatcher.hasGenericErrorWith;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.*;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = Config.NONE)
public class APIResponseHandlerTest {

    private static final Throwable EXCEPTION = new RuntimeException();
    private static final byte[] RESPONSE_BODY = new byte[1];
    private static final Header[] HEADERS = new Header[1];
    private static final int STATUS_CODE = 500;

    private Callback callback;
    private APIResponseHandler<Callback> handler;

    @Before
    public void setUp() throws Exception {
        callback = mock(Callback.class);
        handler = new APIResponseHandler<Callback>(callback) {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {}
        };
    }

    @Test
    public void shouldCallFailureInCallback() throws Exception {
        handler.onFailure(STATUS_CODE, HEADERS, RESPONSE_BODY, EXCEPTION);
        verify(callback).onFailure(isA(APIClientException.class));
    }

    @Test
    public void shouldCallFailureWithGenericError() throws Exception {
        handler.onFailure(STATUS_CODE, HEADERS, RESPONSE_BODY, EXCEPTION);
        ArgumentCaptor<APIClientException> argumentCaptor = ArgumentCaptor.forClass(APIClientException.class);
        verify(callback).onFailure(argumentCaptor.capture());
        APIClientException exception = argumentCaptor.getValue();
        assertThat(exception, hasGenericErrorWith(STATUS_CODE, EXCEPTION));
    }


    @Test
    public void shouldCallFailureWithGenericErrorWhenReponseBodyIsNotJSON() throws Exception {
        handler.onFailure(STATUS_CODE, HEADERS, "INVALID".getBytes(), EXCEPTION);
        ArgumentCaptor<APIClientException> argumentCaptor = ArgumentCaptor.forClass(APIClientException.class);
        verify(callback).onFailure(argumentCaptor.capture());
        APIClientException exception = argumentCaptor.getValue();
        assertThat(exception, hasGenericErrorWith(STATUS_CODE, EXCEPTION));
    }

    @Test
    public void shouldCallFailureWithResponseError() throws Exception {
        handler.onFailure(401, HEADERS, "{\"error\":\"message\"}".getBytes(), EXCEPTION);
        ArgumentCaptor<APIClientException> argumentCaptor = ArgumentCaptor.forClass(APIClientException.class);
        verify(callback).onFailure(argumentCaptor.capture());
        APIClientException exception = argumentCaptor.getValue();
        assertThat(exception, hasErrorWith(401, EXCEPTION, "message"));
    }

}
