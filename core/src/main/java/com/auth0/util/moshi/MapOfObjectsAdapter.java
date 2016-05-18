package com.auth0.util.moshi;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Map;

/**
 * Created by daely on 5/12/2016.
 */
public class MapOfObjectsAdapter {
    @FromJson
    MapOfObjects fromJson(Map<String,Object> m) {
        return new MapOfObjects(m);
    }
    @ToJson
    Map<String,Object> toJson(MapOfObjects o) {
        return o.map;
    }}
