package com.auth0.core;

import com.auth0.BaseTestCase;
import com.google.common.collect.Maps;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

/**
 * Created by hernan on 11/28/14.
 */
@Config(emulateSdk = 18)
public class ConnectionTest extends BaseTestCase {

    public static final String CONNECTION_NAME = "Username-Password";
    public static final Object VALUE = "value";
    public static final String KEY = "key";

    @Test
    public void shouldBuildConnectionWithName() {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection(values);
        assertNotNull(connection);
        assertThat(connection.getName(), equalTo(CONNECTION_NAME));
    }

    @Test
    public void shouldBuildConnectionWithValues() {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        Connection connection = new Connection(values);
        assertThat(connection.getValues(), hasEntry(KEY, VALUE));
    }

    @Test
    public void shouldNotStoreNameInValues() throws Exception {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection(values);
        assertThat(connection.getValues(), not(hasKey("name")));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRaiseExceptionWhenNameIsNull() {
        new Connection(null);
    }

    @Test
    public void shouldReturnValueFromKey() {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", CONNECTION_NAME);
        values.put(KEY, VALUE);
        Connection connection = new Connection(values);
        String value = connection.getValueForKey(KEY);
        assertThat(value, equalTo(VALUE));
    }
}
