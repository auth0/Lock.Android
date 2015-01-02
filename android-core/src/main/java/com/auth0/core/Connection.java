package com.auth0.core;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

import static com.auth0.util.CheckHelper.checkArgument;

/**
 * Created by hernan on 11/28/14.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Connection {

    private String name;

    private Map<String, Object> values;

    @JsonCreator
    public Connection(Map<String, Object> values) {
        checkArgument(values != null && values.size() > 0, "Must have at least one value");
        final String name = (String) values.remove("name");
        checkArgument(name != null, "Must have a non-null name");
        this.name = name;
        this.values = values;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public String getName() {
        return name;
    }

    public <T> T getValueForKey(String key) {
        return (T) this.values.get(key);
    }
}
