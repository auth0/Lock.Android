package com.auth0.core;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Created by daely on 5/13/2016.
 */
public class DatabaseUserJsonAdapter {
    @FromJson
    DatabaseUser fromJson(DatabaseUserJson duj) {
        return new DatabaseUser(duj.email,duj.username,duj.emailVerified);
    }
    @ToJson DatabaseUserJson toJson(DatabaseUser du) {
        return (DatabaseUserJson) du;
    }
}
