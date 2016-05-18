package com.auth0.util.moshi;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Map;

/**
 * encapsulate a Map for customizing Json serialization/deserialization
 * Created by daely on 5/12/2016.
 */
public class MapOfStringsAdapter {
    @FromJson
    MapOfStrings fromJson(Map<String,String> m) {
        return new MapOfStrings(m);
    }
    @ToJson
    Map<String,String> toJson(MapOfStrings o) {
        return o.map;
    }}
