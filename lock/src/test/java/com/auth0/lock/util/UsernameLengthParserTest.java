package com.auth0.lock.util;

import com.auth0.core.Connection;
import com.auth0.lock.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23, manifest = Config.NONE)
public class UsernameLengthParserTest {

    @Test
    public void shouldGetMinMaxUsernameLength() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("min", "10");
        usernameValidation.put("max", "60");
        validation.put("username", usernameValidation);
        Connection connection = new Connection(values);
        final UsernameLengthParser parser = new UsernameLengthParser(connection);

        assertThat(parser.getMinLength(), is(10));
        assertThat(parser.getMaxLength(), is(60));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMissing() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("username", usernameValidation);
        Connection connection = new Connection(values);
        final UsernameLengthParser parser = new UsernameLengthParser(connection);

        assertThat(parser.getMinLength(), is(1));
        assertThat(parser.getMaxLength(), is(15));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMissingUsernameValidation() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Connection connection = new Connection(values);
        final UsernameLengthParser parser = new UsernameLengthParser(connection);

        assertThat(parser.getMinLength(), is(1));
        assertThat(parser.getMaxLength(), is(15));
    }


    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMissingValidation() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Connection connection = new Connection(values);
        final UsernameLengthParser parser = new UsernameLengthParser(connection);

        assertThat(parser.getMinLength(), is(1));
        assertThat(parser.getMaxLength(), is(15));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMaxIsLowerThanMin() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("min", 60);
        usernameValidation.put("max", 10);
        values.put("username", usernameValidation);
        Connection connection = new Connection(values);
        final UsernameLengthParser parser = new UsernameLengthParser(connection);

        assertThat(parser.getMinLength(), is(1));
        assertThat(parser.getMaxLength(), is(15));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMaxIsMissing() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("min", 10);
        values.put("username", usernameValidation);
        Connection connection = new Connection(values);
        final UsernameLengthParser parser = new UsernameLengthParser(connection);

        assertThat(parser.getMinLength(), is(1));
        assertThat(parser.getMaxLength(), is(15));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMinIsMissing() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("max", 60);
        values.put("username", usernameValidation);
        Connection connection = new Connection(values);
        final UsernameLengthParser parser = new UsernameLengthParser(connection);

        assertThat(parser.getMinLength(), is(1));
        assertThat(parser.getMaxLength(), is(15));
    }

}