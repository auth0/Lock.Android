package com.auth0.core;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.io.IOException;
import java.util.Map;

/**
 * Created by daely on 5/13/2016.
 */
public class UserProfileJsonAdapter {
    @FromJson
    UserProfile fromJson(Map<String,Object> m) {
        return new UserProfile(m);
    }
    @ToJson
    Map<String,Object> toJson(UserProfile up) {
       return up.toMap();
    }
}
