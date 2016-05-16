package com.auth0.core;

import com.squareup.moshi.FromJson;
import com.squareup.moshi.ToJson;

/**
 * Created by daely on 5/13/2016.
 */
public class StrategyJsonAdapter {
    @FromJson
    Strategy fromJson(StrategyJson s) {
        return new Strategy(s.name,s.connections);
    }
    @ToJson StrategyJson toJson(Strategy s) {
        return (StrategyJson) s;
    }
}
