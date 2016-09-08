/*
 * StrategyDeserializer.java
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

package com.auth0.android.lock.utils.json;

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

class StrategyDeserializer extends GsonDeserializer<Strategy> {
    @Override
    public Strategy deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        assertJsonObject(json);

        final JsonObject object = json.getAsJsonObject();

        String name = requiredValue("name", String.class, object, context);

        requiredValue("connections", Object.class, object, context);
        JsonArray connectionsArray = object.getAsJsonArray("connections");
        List<AuthData> authDataList = new ArrayList<>();
        for (int i = 0; i < connectionsArray.size(); i++) {
            final JsonObject connectionJson = connectionsArray.get(i).getAsJsonObject();
            requiredValue("name", String.class, connectionJson, context);
            Type mapType = new TypeToken<Map<String, Object>>() {
            }.getType();
            Map<String, Object> values = context.deserialize(connectionJson, mapType);
            authDataList.add(new AuthData(name, values));
        }

        return new Strategy(name, authDataList);
    }
}
