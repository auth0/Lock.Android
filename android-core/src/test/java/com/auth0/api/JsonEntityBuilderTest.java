package com.auth0.api;

import com.auth0.BaseTestCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.ByteStreams;

import org.apache.http.HttpEntity;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

@Config(emulateSdk = 18, manifest = Config.NONE)
public class JsonEntityBuilderTest extends BaseTestCase {

    private static final String JSON = "{\"key\":\"value\"}";
    private static final byte[] JSON_BYTES = JSON.getBytes();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ObjectMapper mapper;
    private JsonEntityBuilder builder;
    private Map parameters;

    @Before
    public void setUp() throws Exception {
        this.mapper = mock(ObjectMapper.class);
        this.parameters = mock(Map.class);
        when(this.mapper.writeValueAsBytes(eq(this.parameters))).thenReturn(JSON_BYTES);
        this.builder = new JsonEntityBuilder(this.mapper);
    }

    @Test
    public void shouldCreateANewInstance() throws Exception {
        assertThat(new JsonEntityBuilder(mapper), is(notNullValue()));
    }

    @Test
    public void shouldNoAcceptNullMapper() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(equalToIgnoringCase("Must supply a non-null mapper"));
        new JsonEntityBuilder(null);
    }

    @Test
    public void shouldBuildEntity() throws Exception {
        HttpEntity entity = builder.newEntityFrom(parameters);
        assertThat(entity, is(notNullValue()));
    }

    @Test
    public void shouldSetContentType() throws Exception {
        HttpEntity entity = builder.newEntityFrom(parameters);
        assertThat(entity.getContentType().getValue(), equalTo("application/json"));
    }

    @Test
    public void shouldHaveJSONBytes() throws Exception {
        HttpEntity entity = builder.newEntityFrom(parameters);
        assertThat(ByteStreams.toByteArray(entity.getContent()), equalTo(JSON_BYTES));
    }

    @Test
    public void shouldRaiseExceptionWhenJSONSerializeFails() throws Exception {
        expectedException.expect(JsonEntityBuildException.class);
        expectedException.expectMessage(equalToIgnoringCase("Failed to convert Map<String, String> to JSON"));
        when(mapper.writeValueAsBytes(any())).thenThrow(mock(JsonProcessingException.class));
        builder.newEntityFrom(parameters);
    }

}
