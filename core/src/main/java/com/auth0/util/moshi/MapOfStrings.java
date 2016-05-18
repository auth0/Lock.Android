package com.auth0.util.moshi;

import java.util.HashMap;
import java.util.Map;

/**
 * encapsulate a Map for customizing Json serialization/deserialization
 * Created by daely on 5/12/2016.
 */
public class MapOfStrings {
    public final Map<String,String> map;
    public MapOfStrings() {
        map=new HashMap<String,String>();
    }
    public MapOfStrings(Map<String,String> map) {
        this.map=map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapOfStrings mapOfStrings = (MapOfStrings) o;

        return map.equals(mapOfStrings.map);

    }

    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
