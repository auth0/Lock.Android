package com.auth0.android.lock.internal.json;

import com.auth0.android.lock.internal.AuthType;
import com.auth0.android.lock.internal.PasswordStrength;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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
        DatabaseConnection connection = new DatabaseConnection(values);

        assertTrue(connection.requiresUsername());
    }

    @Test
    public void shouldNotRequireUsername() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("requires_username", false);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertFalse(connection.requiresUsername());
    }

    @Test
    public void shouldShowSignup() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showSignup", true);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertTrue(connection.showSignup());
    }

    @Test
    public void shouldNotShowSignup() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showSignup", false);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertFalse(connection.showSignup());
    }

    @Test
    public void shouldShowForgot() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showForgot", true);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertTrue(connection.showForgot());
    }

    @Test
    public void shouldNotShowForgot() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showForgot", false);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertFalse(connection.showForgot());
    }

    @Test
    public void shouldGetExcellentPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "excellent");
        DatabaseConnection connection = new DatabaseConnection(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.EXCELLENT));
    }

    @Test
    public void shouldGetFairPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "fair");
        DatabaseConnection connection = new DatabaseConnection(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.FAIR));
    }

    @Test
    public void shouldGetGoodPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "good");
        DatabaseConnection connection = new DatabaseConnection(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.GOOD));
    }

    @Test
    public void shouldGetLowPasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "low");
        DatabaseConnection connection = new DatabaseConnection(values);

        assertThat(connection.getPasswordPolicy(), is(PasswordStrength.LOW));
    }

    @Test
    public void shouldGetNonePasswordPolicy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = new DatabaseConnection(values);

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
        DatabaseConnection connection = new DatabaseConnection(values);

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
        usernameValidation.put("username", usernameValidation);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
    }

    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMissingUsernameValidation() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
    }


    @Test
    public void shouldGetDefaultMinMaxUsernameLengthIfMissingValidation() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = new DatabaseConnection(values);

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
        values.put("username", usernameValidation);
        DatabaseConnection connection = new DatabaseConnection(values);

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
        values.put("username", usernameValidation);
        DatabaseConnection connection = new DatabaseConnection(values);

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
        values.put("username", usernameValidation);
        DatabaseConnection connection = new DatabaseConnection(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(15));
    }

    @Test
    public void shouldHaveName() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        DatabaseConnection connection = new DatabaseConnection(values);
        assertThat(connection.getName(), is("name"));
    }

    @Test
    public void shouldHaveStrategy() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        DatabaseConnection connection = new DatabaseConnection(values);
        assertThat(connection.getStrategy(), is("auth0"));
    }

    @Test
    public void shouldBeDatabaseType() throws Exception {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = new DatabaseConnection(values);
        assertThat(connection.getType(), is(AuthType.DATABASE));
    }
}