package com.auth0.android.lock.errors;

import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.lock.R;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21)
public class SignUpErrorMessageBuilderTest {

    @Mock
    AuthenticationException exception;
    SignUpErrorMessageBuilder builder;


    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        builder = new SignUpErrorMessageBuilder();
    }

    @Test
    public void shouldHaveDefaultMessageIfAccessDenied() {
        Mockito.when(exception.isAccessDenied()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_sign_up_error_message)));
    }

    @Test
    public void shouldHaveDescriptionIfRuleError() {
        Mockito.when(exception.isRuleError()).thenReturn(true);
        Mockito.when(exception.getDescription()).thenReturn("Description");
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(R.string.com_auth0_lock_db_sign_up_error_message));
        assertThat(error.getCustomMessage(), is(equalTo("Description")));
    }

    @Test
    public void shouldHaveCustomMessageIfUsernameExists() {
        Mockito.when(exception.getCode()).thenReturn("user_exists");
        final AuthenticationError error1 = builder.buildFrom(exception);
        assertThat(error1.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_signup_user_already_exists_error_message)));

        Mockito.when(exception.getCode()).thenReturn("username_exists");
        final AuthenticationError error2 = builder.buildFrom(exception);
        assertThat(error2.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_signup_user_already_exists_error_message)));
    }

    @Test
    public void shouldHaveCustomMessageIfIsTooManyAttempts() {
        Mockito.when(exception.getCode()).thenReturn("too_many_attempts");
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_too_many_attempts_error_message)));
    }

    @Test
    public void shouldHaveCustomMessageIfPasswordNotStrongEnough() {
        Mockito.when(exception.isPasswordNotStrongEnough()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_signup_password_not_strong_error_message)));
    }

    @Test
    public void shouldHaveCustomMessageIfPasswordAlreadyUsed() {
        Mockito.when(exception.isPasswordAlreadyUsed()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_signup_password_already_used_error_message)));
    }
}