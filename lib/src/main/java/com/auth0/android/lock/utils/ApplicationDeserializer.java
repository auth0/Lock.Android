/*
 * ListSerializer.java
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

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ApplicationDeserializer implements JsonDeserializer<Application> {
    @Override
    public Application deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        final JsonObject map = json.getAsJsonObject();
        String id = map.get("id").getAsString();
        String tenant = map.get("tenant").getAsString();
        String authorizeURL = map.get("authorize").getAsString();

        final JsonElement callbackURLJson = map.get("callback");
        String callbackURL = callbackURLJson == null ? null : callbackURLJson.getAsString();
        final JsonElement subscriptionJson = map.get("subscription");
        String subscription = subscriptionJson == null ? null : subscriptionJson.getAsString();
        final JsonElement hasAllowedOriginsJson = map.get("hasAllowedOrigins");
        boolean hasAllowedOrigins = hasAllowedOriginsJson != null && hasAllowedOriginsJson.getAsBoolean();

        Type strategyType = new TypeToken<List<Strategy>>() {
        }.getType();
        List<Strategy> strategies = context.deserialize(map.get("strategies"), strategyType);

        return new Application(id, tenant, authorizeURL, callbackURL, subscription, hasAllowedOrigins, strategies);
    }
}
