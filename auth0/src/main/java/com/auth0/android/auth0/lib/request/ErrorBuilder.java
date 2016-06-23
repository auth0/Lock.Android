package com.auth0.android.auth0.lib.request;

import com.auth0.android.auth0.lib.Auth0Exception;

import java.util.Map;

public interface ErrorBuilder<U extends Auth0Exception> {

    U from(String message, Auth0Exception exception);

    U from(Map<String, Object> values);

}
