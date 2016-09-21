package com.auth0.lock.validation;

import com.auth0.lock.util.UsernameLengthParser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

public class UsernameOrEmailValidatorTest {
    @Mock
    UsernameLengthParser lengthParser;
    private UsernameOrEmailValidator validator;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        Mockito.when(lengthParser.getMinLength()).thenReturn(1);
        Mockito.when(lengthParser.getMaxLength()).thenReturn(10);
        validator = new UsernameOrEmailValidator(lengthParser);
    }

    @Test
    public void shouldConstructValidators() throws Exception {
        assertThat(validator.emailValidator, is(notNullValue()));
        assertThat(validator.usernameValidator, is(notNullValue()));
    }

    @Test
    public void shouldUseEmailValidation() throws Exception {
        assertThat(validator.doValidate("email@me.com"), is(true));
    }

    @Test
    public void shouldUseUsernameValidation() throws Exception {
        assertThat(validator.doValidate("username"), is(true));
    }

    @Test
    public void shouldFailWithEmptyValue() throws Exception {
        assertThat(validator.doValidate(""), is(false));
    }

    @Test
    public void shouldFailWithLongUsername() throws Exception {
        assertThat(validator.doValidate("too-long-username"), is(false));
    }

}