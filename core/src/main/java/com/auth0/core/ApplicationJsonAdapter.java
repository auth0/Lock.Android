package com.auth0.core;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Created by daely on 5/13/2016.
 */
public class ApplicationJsonAdapter {
    @FromJson
    Application fromJson(ApplicationJson a) {
        return new Application(a.id,a.tenant,a.authorizeURL,a.callbackURL,a.subscription,a.hasAllowedOrigins,a.strategies);
    }
    @ToJson
    ApplicationJson toJson(Application a) {
        return a;
    }
}
