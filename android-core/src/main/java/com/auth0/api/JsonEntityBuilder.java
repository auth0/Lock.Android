package com.auth0.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import java.util.Map;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class that converts a {@link java.util.Map} to an {@link org.apache.http.HttpEntity}
 */
public class JsonEntityBuilder {

    private static final String APPLICATION_JSON = "application/json";

    private ObjectMapper mapper;

    public JsonEntityBuilder(ObjectMapper mapper) {
        checkArgument(mapper != null, "Must supply a non-null mapper");
        this.mapper = mapper;
    }

    public HttpEntity newEntityFrom(Map<String, Object> values) throws JsonEntityBuildException {
        try {
            byte[] bytes = mapper.writeValueAsBytes(values);
            ByteArrayEntity entity = new ByteArrayEntity(bytes);
            entity.setContentType(APPLICATION_JSON);
            return entity;
        } catch (JsonProcessingException e) {
            throw new JsonEntityBuildException("Failed to convert Map<String, String> to JSON", e);
        }
    }
}
