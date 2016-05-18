package com.auth0.core;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

import java.util.Map;

/**
 * Created by daely on 5/13/2016.
 */
public class UserIdentityJsonAdapter {
    @FromJson
    UserIdentity fromJson(Map<String,Object> m) {
        return new UserIdentity(m);
    }
    @ToJson
    Map<String,Object> toJson(UserIdentity ui) {
        return ui.toMap();
    }
}
