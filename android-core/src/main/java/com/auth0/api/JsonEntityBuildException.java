package com.auth0.api;

/**
 * Exception that wraps errors when creating a {@link org.apache.http.HttpEntity}
 */
public class JsonEntityBuildException extends RuntimeException {

    public JsonEntityBuildException(String message, Throwable cause) {
        super(message, cause);
    }

}
