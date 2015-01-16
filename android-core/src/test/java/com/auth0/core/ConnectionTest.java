package com.auth0.core;

import com.auth0.BaseTestCase;
import com.google.common.collect.Maps;

import org.junit.Test;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasValue;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
        Map<String, Object> values = null;
        new Connection(values);
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

    @Test
    public void shouldReturnDomainNameInSet() throws Exception {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", CONNECTION_NAME);
        values.put("domain", "domain.com");
        Connection connection = new Connection(values);
        assertThat(connection.getDomainSet(), hasItem("domain.com"));
    }

    @Test
    public void shouldReturnAllDomainNamesAsSet() throws Exception {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", CONNECTION_NAME);
        values.put("domain", "domain.com");
        values.put("domain_aliases", Arrays.asList("domain2.com", "domain3.com"));
        Connection connection = new Connection(values);
        assertThat(connection.getDomainSet(), hasItems("domain.com", "domain2.com", "domain3.com"));
    }

    @Test
    public void shouldReturnEmptySetWithNoDomainName() throws Exception {
        Map<String, Object> values = Maps.newHashMap();
        values.put("name", CONNECTION_NAME);
        Connection connection = new Connection(values);
        assertThat(connection.getDomainSet().isEmpty(), is(true));
    }
}
