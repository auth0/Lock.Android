package com.auth0.android.lock.utils;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class HiddenFieldTest {
    private static final String KEY = "key";
    private static final String VALUE = "fixed value";
    private static final int STORAGE = CustomField.Storage.PROFILE_ROOT;
    private HiddenField field;

    @Before
    public void setUp() throws Exception {
        field = new HiddenField(KEY, VALUE, STORAGE);
    }

    @Test
    public void shouldGetKey() {
        assertThat(field.getKey(), is(KEY));
    }

    @Test
    public void shouldGetStorage() {
        assertThat(field.getStorage(), is(CustomField.Storage.PROFILE_ROOT));
    }

    @Test
    public void shouldGetValue() {
        assertThat(field.getValue(), is(VALUE));
    }
}