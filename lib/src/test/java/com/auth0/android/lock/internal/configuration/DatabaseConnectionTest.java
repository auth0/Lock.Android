package com.auth0.android.lock.internal.configuration;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.android.lock.internal.configuration.ConnectionMatcher.hasType;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DatabaseConnectionTest {

    @Test
    public void shouldRequireUsername() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("requires_username", true);
        final DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.requiresUsername());
    }

    @Test
    public void shouldNotRequireUsername() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("requires_username", false);
        DatabaseConnection connection = connectionFor(values);

        assertFalse(connection.requiresUsername());
    }

    @Test
    public void shouldShowSignup() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showSignup", true);
        DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.showSignUp());
    }

    @Test
    public void shouldNotShowSignup() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showSignup", false);
        DatabaseConnection connection = connectionFor(values);

        assertFalse(connection.showSignUp());
    }

    @Test
    public void shouldShowForgot() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showForgot", true);
        DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.showForgot());
    }

    @Test
    public void shouldNotShowForgot() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showForgot", false);
        DatabaseConnection connection = connectionFor(values);

        assertFalse(connection.showForgot());
    }

    @Test
    public void shouldGetExcellentPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "excellent");
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.EXCELLENT));
    }

    @Test
    public void shouldGetFairPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "fair");
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.FAIR));
    }

    @Test
    public void shouldGetGoodPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "good");
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.GOOD));
    }

    @Test
    public void shouldGetLowPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "low");
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.LOW));
    }

    @Test
    public void shouldGetNonePasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.NONE));
    }

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
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(10));
        assertThat(connection.getMaxUsernameLength(), is(60));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMissing() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        validation.put("username", usernameValidation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
    }

    @Test
    public void shouldOnlyGetMinUsernameLengthIfMissingUsernameValidation() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(-1));
        assertThat(connection.getMaxUsernameLength(), is(-1));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMissingValidation() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
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
        validation.put("username", usernameValidation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMaxIsMissing() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("min", 10);
        validation.put("username", usernameValidation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMinIsMissing() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("max", 60);
        validation.put("username", usernameValidation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
    }

    @Test
    public void shouldHaveName() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        DatabaseConnection connection = connectionFor(values);
        assertThat(connection.getName(), is("name"));
    }

    @Test
    public void shouldHaveStrategy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        DatabaseConnection connection = connectionFor(values);
        assertThat(connection.getStrategy(), is("auth0"));
    }

    @Test
    public void shouldBeDatabaseType() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = connectionFor(values);
        assertThat(connection, hasType(AuthType.DATABASE));
    }

    private DatabaseConnection connectionFor(Map<String, Object> values) {
        return Connection.newConnectionFor("auth0", values);
    }
}