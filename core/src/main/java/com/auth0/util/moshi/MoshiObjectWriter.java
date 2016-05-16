package com.auth0.util.moshi;

import java.lang.reflect.Type;

/**
 * Created by daely on 5/12/2016.
 */
public class MoshiObjectWriter {
    MoshiObjectMapper mapper;
    public MoshiObjectWriter(MoshiObjectMapper mapper)
    {
        this.mapper=mapper;
    }

    public <T> byte[] writeValueAsBytes(T pojo,Type typeOfT) {
        return mapper.writeValueAsBytes(pojo,typeOfT);
    }
}
