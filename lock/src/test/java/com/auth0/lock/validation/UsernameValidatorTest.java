package com.auth0.lock.validation;

import com.auth0.lock.util.UsernameLengthParser;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class UsernameValidatorTest {
    private UsernameValidator validator;
    @Mock
    private UsernameLengthParser lengthParser;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        validator = new UsernameValidator(1, 2, 3, lengthParser);
    }

    @Test
    public void shouldFailIfTooShort() throws Exception {
        Mockito.when(lengthParser.getMinLength()).thenReturn(10);
        Mockito.when(lengthParser.getMaxLength()).thenReturn(100);
        assertThat(validator.doValidate("short"), is(false));
    }

    @Test
    public void shouldFailIfTooLongShort() throws Exception {
        Mockito.when(lengthParser.getMinLength()).thenReturn(1);
        Mockito.when(lengthParser.getMaxLength()).thenReturn(5);
        assertThat(validator.doValidate("too-long"), is(false));
    }

    @Test
    public void shouldPassIfLengthIsOk() throws Exception {
        Mockito.when(lengthParser.getMinLength()).thenReturn(1);
        Mockito.when(lengthParser.getMaxLength()).thenReturn(15);
        assertThat(validator.doValidate("username"), is(true));
    }
}