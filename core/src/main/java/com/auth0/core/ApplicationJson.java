package com.auth0.core;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class with your Auth0's application information and the list of enabled connections (DB, Social, Enterprise, Passwordless).
 */
public class ApplicationJson {
    @Json(name = "id") String id;
    @Json(name = "tenant") String tenant;
    @Json(name = "authorize") String authorizeURL;
    @Json(name = "callback") String callbackURL;
    @Json(name = "subscription") String subscription;
    @Json(name = "hasAllowedOrigins") boolean hasAllowedOrigins;
    @Json(name = "strategies") List<Strategy> strategies;

    @SuppressWarnings("unused") // Moshi uses this!
    ApplicationJson() {
    }
}
