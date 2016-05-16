package com.auth0.util.moshi;

import java.util.HashMap;
import java.util.Map;

/**
 * encapsulate a Map for customizing Json serialization/deserialization
 * Created by daely on 5/12/2016.
 */
public class MapOfObjects {
    public final Map<String,Object> map;
    public MapOfObjects() {
        map=new HashMap<String,Object>();
    }
    public MapOfObjects(Map<String,Object> map) {
        this.map=map;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapOfObjects mapOfObjects = (MapOfObjects) o;

        return map.equals(mapOfObjects.map);

    }
    public String getAsString(String key) {
        return (String) map.get(key);
    }
    public Integer getAsInteger(String key) {
        Object tmp=map.get(key);
        if(tmp==null) return  null;
        return toInteger(tmp);
    }
    /**
     * fix an issue with gson/moshi, that can cause an integer in Json to be parsed as Double
     * see also http://stackoverflow.com/questions/21920436/object-autoconvert-to-double-with-serialization-gson
     * @param obj
     * @return
     */
    private Integer toInteger(Object obj) {
        Integer expires_in=null;
        if(obj instanceof Integer)
            expires_in = (Integer) obj;
        else if(obj instanceof Double) {
            double tmp_ = (Double) obj;
            expires_in = (int) Math.round(tmp_);
        } else if(obj instanceof Float) {
            float tmp_ = (Float) obj;
            expires_in = (int) Math.round(tmp_);
        }
        return expires_in;
    }
    @Override
    public int hashCode() {
        return map.hashCode();
    }
}
