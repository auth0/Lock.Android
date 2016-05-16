package com.auth0.util.moshi;

import com.auth0.core.ApplicationJsonAdapter;
import com.auth0.core.ConnectionJsonAdapter;
import com.auth0.core.DatabaseUserJsonAdapter;
import com.auth0.core.StrategyJsonAdapter;
import com.auth0.core.TokenJsonAdapter;
import com.auth0.core.UserIdentityJsonAdapter;
import com.auth0.core.UserProfileJsonAdapter;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.Map;

import okio.Okio;

/**
 * Created by daely on 5/12/2016.
 */
public class MoshiObjectMapper {
    public final Moshi moshi;

    private static Moshi configureMoshi() {
        Moshi res=new Moshi.Builder()
                .add(new ApplicationJsonAdapter())
                .add(new ConnectionJsonAdapter())
                .add(new DatabaseUserJsonAdapter())
                .add(new StrategyJsonAdapter())
                .add(new TokenJsonAdapter())
                .add(new UserIdentityJsonAdapter())
                .add(new UserProfileJsonAdapter())
                .add(new MapOfObjectsAdapter())
                .add(new MapOfStringsAdapter())
                .build();
        return res;
    }
    public MoshiObjectMapper() {
        moshi= configureMoshi();
    }

    public  <T> T readValue(String json,Type typeOfT) throws IOException {
        JsonAdapter<T> adapter=moshi.adapter(typeOfT);
        return adapter.fromJson(json);
    }
    public  <T> T readValue(InputStream inputStream, Type typeOfT) throws IOException {
        JsonAdapter<T> adapter=moshi.adapter(typeOfT);
        return adapter.fromJson(Okio.buffer(Okio.source(inputStream)));
    }
    public  <T> T readValue(File file, Type typeOfT) throws IOException {
        JsonAdapter<T> adapter=moshi.adapter(typeOfT);
        return adapter.fromJson(Okio.buffer(Okio.source(file)));
    }
    public String writeValueAsString(Map<String, Object> map) {
        MapOfObjects om = new MapOfObjects(map);
        JsonAdapter<MapOfObjects> adapter=moshi.adapter( MapOfObjects.class );
        return adapter.toJson(om);
    }
    public <T> byte[] writeValueAsBytes(T pojo,Type typeOfT) {
        JsonAdapter<T> adapter= moshi.adapter(typeOfT);
        return adapter.toJson(pojo).getBytes();
    }

}
