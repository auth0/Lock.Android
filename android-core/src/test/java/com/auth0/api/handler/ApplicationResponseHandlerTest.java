package com.auth0.api.handler;

import com.auth0.BaseTestCase;
import com.auth0.core.Application;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.robolectric.annotation.Config;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Config(sdk = 18, manifest = Config.NONE)
public class ApplicationResponseHandlerTest extends BaseTestCase {

    public static final String APP_JSONP_VALID = "Auth0.setClient({\"id\": \"QWERTY123456\"})";
    public static final String APP_JSON_VALID = "{\"id\":\"QWERTY123456\"}";
    private ApplicationResponseHandler handler;
    private Application application;
    private Throwable throwable;
    private ObjectMapper mapper;
    private Application payload;

    @Before
    public void setUp() throws Exception {
        this.mapper = mock(ObjectMapper.class);
        this.handler = new ApplicationResponseHandler(mapper) {

            @Override
            public void onSuccess(Application payload) {
                ApplicationResponseHandlerTest.this.payload = payload;
            }

            @Override
            public void onFailure(Throwable error) {
                ApplicationResponseHandlerTest.this.throwable = error;
            }
        };
        this.application = mock(Application.class);
        this.throwable = null;
    }

    @Test
    public void shouldCallSuccessWithApplicationInstance() throws Exception {
        when(mapper.readValue(eq(APP_JSON_VALID), eq(Application.class))).thenReturn(application);
        handler.onSuccess(200, null, APP_JSONP_VALID.getBytes());
        assertThat(throwable, is(nullValue()));
        verify(mapper).readValue(eq(APP_JSON_VALID), eq(Application.class));
        assertThat(payload, equalTo(application));
    }

    @Test
    public void shouldCallFailureWhenJSONPIsInvalid() throws Exception {
        handler.onSuccess(200, null, APP_JSON_VALID.getBytes());
        verify(mapper, never()).readValue(eq(APP_JSON_VALID), eq(Application.class));
        assertThat(throwable, is(notNullValue()));
        assertThat(payload, is(nullValue()));
    }

    @Test
    public void shouldCallFailureWhenJSONParseFails() throws Exception {
        when(mapper.readValue(anyString(), eq(Application.class))).thenThrow(new JsonParseException("Failed", null));
        handler.onSuccess(200, null, APP_JSON_VALID.getBytes());
        assertThat(payload, is(nullValue()));
        assertThat(throwable, is(notNullValue()));
    }

    @Test
    public void shouldCallFailureWithEmptyJSONP() throws Exception {
        handler.onSuccess(200, null, "".getBytes());
        verify(mapper, never()).readValue(eq(APP_JSON_VALID), eq(Application.class));
        assertThat(throwable, is(notNullValue()));
        assertThat(payload, is(nullValue()));
    }

    @Test
    public void shouldCallFailure() throws Exception {
        handler.onFailure(200, null, APP_JSONP_VALID.getBytes(), new RuntimeException());
        verify(mapper, never()).readValue(eq(APP_JSON_VALID), eq(Application.class));
        assertThat(throwable, is(notNullValue()));
        assertThat(payload, is(nullValue()));
    }

}
