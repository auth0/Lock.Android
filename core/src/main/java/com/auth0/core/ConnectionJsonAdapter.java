package com.auth0.core;

import com.auth0.util.moshi.MapOfObjects;
import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Created by daely on 5/13/2016.
 */
public class ConnectionJsonAdapter {
    @FromJson
    Connection fromJson(MapOfObjects om) {
        return new Connection(om.map);
    }
    @ToJson
    MapOfObjects toJson(Connection c) {
        return c.toMap();
    }
}
