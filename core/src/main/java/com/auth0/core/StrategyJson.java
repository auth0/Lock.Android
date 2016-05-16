package com.auth0.core;

import com.squareup.moshi.Json;

import java.util.List;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Class with Auth0 authentication strategy info
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class StrategyJson {

    @Json(name="name")
    protected String name;
    @Json(name="connections")
    protected List<Connection> connections;

    @SuppressWarnings("unused") // Moshi uses this!
    protected StrategyJson() {

    }
}
