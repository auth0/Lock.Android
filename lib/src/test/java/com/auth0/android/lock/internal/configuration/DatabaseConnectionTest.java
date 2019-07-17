package com.auth0.android.lock.internal.configuration;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static com.auth0.android.lock.internal.configuration.ConnectionMatcher.hasType;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DatabaseConnectionTest {

    @Test
    public void shouldRequireUsername() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("requires_username", true);
        final DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.requiresUsername());
    }

    @Test
    public void shouldNotRequireUsername() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("requires_username", false);
        DatabaseConnection connection = connectionFor(values);

        assertFalse(connection.requiresUsername());
    }

    @Test
    public void shouldShowSignup() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showSignup", true);
        DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.showSignUp());
    }

    @Test
    public void shouldNotShowSignup() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showSignup", false);
        DatabaseConnection connection = connectionFor(values);

        assertFalse(connection.showSignUp());
    }

    @Test
    public void shouldShowForgot() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showForgot", true);
        DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.showForgot());
    }

    @Test
    public void shouldNotShowForgot() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("showForgot", false);
        DatabaseConnection connection = connectionFor(values);

        assertFalse(connection.showForgot());
    }

    @Test
    public void shouldGetExcellentPasswordPolicy() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "excellent");
        DatabaseConnection connection = connectionFor(values);
        PasswordComplexity passwordComplexity = connection.getPasswordComplexity();
        assertThat(passwordComplexity.getPasswordPolicy(), is(PasswordStrength.EXCELLENT));
        assertThat(passwordComplexity.getMinLengthOverride(), is(nullValue()));
    }

    @Test
    public void shouldGetFairPasswordPolicy() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "fair");
        DatabaseConnection connection = connectionFor(values);

        PasswordComplexity passwordComplexity = connection.getPasswordComplexity();
        assertThat(passwordComplexity.getPasswordPolicy(), is(PasswordStrength.FAIR));
        assertThat(passwordComplexity.getMinLengthOverride(), is(nullValue()));
    }

    @Test
    public void shouldGetGoodPasswordPolicy() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "good");
        DatabaseConnection connection = connectionFor(values);

        PasswordComplexity passwordComplexity = connection.getPasswordComplexity();
        assertThat(passwordComplexity.getPasswordPolicy(), is(PasswordStrength.GOOD));
        assertThat(passwordComplexity.getMinLengthOverride(), is(nullValue()));
    }

    @Test
    public void shouldGetLowPasswordPolicy() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        values.put("passwordPolicy", "low");
        DatabaseConnection connection = connectionFor(values);

        PasswordComplexity passwordComplexity = connection.getPasswordComplexity();
        assertThat(passwordComplexity.getPasswordPolicy(), is(PasswordStrength.LOW));
        assertThat(passwordComplexity.getMinLengthOverride(), is(nullValue()));
    }

    @Test
    public void shouldGetNonePasswordPolicy() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = connectionFor(values);

        PasswordComplexity passwordComplexity = connection.getPasswordComplexity();
        assertThat(passwordComplexity.getPasswordPolicy(), is(PasswordStrength.NONE));
        assertThat(passwordComplexity.getMinLengthOverride(), is(nullValue()));
    }

    @Test
    public void shouldGetMinPasswordLength() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> options = new HashMap<>();
        options.put("min_length", 123);
        values.put("password_complexity_options", options);
        DatabaseConnection connection = connectionFor(values);

        PasswordComplexity passwordComplexity = connection.getPasswordComplexity();
        assertThat(passwordComplexity.getPasswordPolicy(), is(PasswordStrength.NONE));
        assertThat(passwordComplexity.getMinLengthOverride(), is(123));
    }

    @Test
    public void shouldGetMinMaxUsernameLength() {
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
    public void shouldGetNonEmptyMinMaxUsernameLengthIfMissingMinMaxValues() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        validation.put("username", usernameValidation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldGetNonEmptyMinMaxUsernameLengthIfMissingUsernameValidation() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldGetNonEmptyMinMaxUsernameLengthIfMissingValidation() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldGetNonEmptyMinMaxUsernameLengthIfMaxIsLowerThanMin() {
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
        assertThat(connection.getMaxUsernameLength(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldGetNonEmptyMinMaxUsernameLengthIfMaxIsMissing() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("min", 10);
        validation.put("username", usernameValidation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldGetNonEmptyMinMaxUsernameLengthIfMinIsMissing() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> usernameValidation = new HashMap<>();
        usernameValidation.put("max", 60);
        validation.put("username", usernameValidation);
        DatabaseConnection connection = connectionFor(values);

        assertThat(connection.getMinUsernameLength(), is(1));
        assertThat(connection.getMaxUsernameLength(), is(Integer.MAX_VALUE));
    }

    @Test
    public void shouldBeCustomDatabaseIfMissingValidation() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.isCustomDatabase());
    }

    @Test
    public void shouldBeCustomDatabaseIfMissingUsernameValidation() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        DatabaseConnection connection = connectionFor(values);

        assertTrue(connection.isCustomDatabase());
    }

    @Test
    public void shouldNotBeCustomDatabaseIfContainsUsernameValidation() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        Map<String, Object> validation = new HashMap<>();
        values.put("validation", validation);
        Map<String, Object> username = new HashMap<>();
        validation.put("username", username);
        DatabaseConnection connection = connectionFor(values);

        assertFalse(connection.isCustomDatabase());
    }

    @Test
    public void shouldHaveName() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        DatabaseConnection connection = connectionFor(values);
        assertThat(connection.getName(), is("name"));
    }

    @Test
    public void shouldHaveStrategy() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "name");
        DatabaseConnection connection = connectionFor(values);
        assertThat(connection.getStrategy(), is("auth0"));
    }

    @Test
    public void shouldBeDatabaseType() {
        Map<String, Object> values = new HashMap<>();
        values.put("name", "Username-Password-Authentication");
        DatabaseConnection connection = connectionFor(values);
        assertThat(connection, hasType(AuthType.DATABASE));
    }

    private DatabaseConnection connectionFor(Map<String, Object> values) {
        return Connection.newConnectionFor("auth0", values);
    }
}