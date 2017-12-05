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

package com.auth0.android.lock.views.next.configuration.internal;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class ClientInfoDeserializer extends GsonDeserializer<List<Connection>> {

    private static final String ID_KEY = "id";
    private static final String TENANT_KEY = "tenant";
    private static final String AUTHORIZE_KEY = "authorize";
    private static final String CALLBACK_KEY = "callback";
    private static final String STRATEGIES_KEY = "strategies";
    private static final String NAME_KEY = "name";
    private static final String CONNECTIONS_KEY = "connections";

    @Override
    public List<Connection> deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        assertJsonObject(json);

        final JsonObject object = json.getAsJsonObject();

        requiredValue(ID_KEY, String.class, object, context);
        requiredValue(TENANT_KEY, String.class, object, context);
        requiredValue(AUTHORIZE_KEY, String.class, object, context);
        requiredValue(CALLBACK_KEY, String.class, object, context);

        requiredValue(STRATEGIES_KEY, JsonArray.class, object, context);
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
        String name = requiredValue(NAME_KEY, String.class, strategy, context);
        requiredValue(CONNECTIONS_KEY, Object.class, strategy, context);

        JsonArray connectionsArray = strategy.getAsJsonArray(CONNECTIONS_KEY);
        List<Connection> connections = new ArrayList<>();
        for (int i = 0; i < connectionsArray.size(); i++) {
            final JsonObject connectionJson = connectionsArray.get(i).getAsJsonObject();
            requiredValue(NAME_KEY, String.class, connectionJson, context);
            Type mapType = new TypeToken<LinkedTreeMap<String, Object>>() {
            }.getType();
            Map<String, Object> values = context.deserialize(connectionJson, mapType);
            connections.add(Connection.newConnectionFor(name, values));
        }
        return connections;
    }
}
