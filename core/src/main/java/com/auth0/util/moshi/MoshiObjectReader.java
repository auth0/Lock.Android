package com.auth0.util.moshi;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Created by daely on 5/12/2016.
 */
public class MoshiObjectReader<T> {
    MoshiObjectMapper mapper;
    Type type;
    public MoshiObjectReader(MoshiObjectMapper mapper, Type type) {
        this.mapper=mapper;
        this.type=type;
    }

    public T readValue(String s) throws IOException {
        return mapper.readValue(s,type);
    }

    public T readValue(InputStream byteStream) throws IOException {
        return mapper.readValue(byteStream,type);
    }
}
