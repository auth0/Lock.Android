/*
 * ApplicationDeserializer.java
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

package com.auth0.android.lock.internal.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.auth0.android.lock.internal.json.Connection.connectionFor;

class ApplicationDeserializer extends GsonDeserializer<List<Connection>> {

    @Override
    public List<Connection> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        assertJsonObject(json);

        final JsonObject object = json.getAsJsonObject();

        requiredValue("id", String.class, object, context);
        requiredValue("tenant", String.class, object, context);
        requiredValue("authorize", String.class, object, context);
        requiredValue("callback", String.class, object, context);

        requiredValue("strategies", JsonArray.class, object, context);
        final JsonArray strategies = object.getAsJsonArray("strategies");
        return mergeConnections(strategies, context);
    }

    private List<Connection> mergeConnections(JsonArray list, JsonDeserializationContext context) {
        List<Connection> connections = new ArrayList<>();
        for (JsonElement strategy : list) {
            final List<Connection> c = parseStrategy(strategy, context);
            connections.addAll(c);
        }
        return connections;
    }

    private List<Connection> parseStrategy(JsonElement json, JsonDeserializationContext context) {
        final JsonObject strategy = json.getAsJsonObject();
        String name = requiredValue("name", String.class, strategy, context);
        requiredValue("connections", Object.class, strategy, context);

        JsonArray connectionsArray = strategy.getAsJsonArray("connections");
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < connectionsArray.size(); i++) {
            final JsonObject connectionJson = connectionsArray.get(i).getAsJsonObject();
            requiredValue("name", String.class, connectionJson, context);
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> values = context.deserialize(connectionJson, mapType);
            connections.add(connectionFor(name, values));
        }
        return connections;
    }
}
