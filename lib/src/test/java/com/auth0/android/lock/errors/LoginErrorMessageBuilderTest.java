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
public class LoginErrorMessageBuilderTest {

    @Mock
    AuthenticationException exception;
    LoginErrorMessageBuilder builder;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        builder = new LoginErrorMessageBuilder();
    }

    @Test
    public void shouldHaveDefaultMessageIfAccessDenied() {
        Mockito.when(exception.isAccessDenied()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_login_error_message)));
    }

    @Test
    public void shouldHaveCustomMessageIfInvalidCredentials() {
        Mockito.when(exception.isInvalidCredentials()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_login_error_invalid_credentials_message)));
    }

    @Test
    public void shouldHaveDescriptionIfRuleError() {
        Mockito.when(exception.isRuleError()).thenReturn(true);
        Mockito.when(exception.getDescription()).thenReturn("Description");
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(R.string.com_auth0_lock_db_login_error_unauthorized_message));
        assertThat(error.getCustomMessage(), is(equalTo("Description")));
    }

    @Test
    public void shouldHaveCustomMessageIfPasswordLeaked() {
        Mockito.when(exception.isPasswordLeaked()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_password_leaked_error_message)));
    }

    @Test
    public void shouldHaveCustomMessageIfMultifactorCodeInvalid() {
        Mockito.when(exception.isMultifactorCodeInvalid()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_login_error_invalid_mfa_code_message)));
    }

    @Test
    public void shouldHaveDefaultMessageIfMultifactorRequired() {
        Mockito.when(exception.isMultifactorRequired()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_login_error_message)));
    }

    @Test
    public void shouldHaveDefaultMessageIfMultifactorEnrollRequired() {
        Mockito.when(exception.isMultifactorEnrollRequired()).thenReturn(true);
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_login_error_mfa_enroll_required)));
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
    public void shouldHaveCustomMessageIfUserIsBlocked() {
        Mockito.when(exception.isRuleError()).thenReturn(true);
        Mockito.when(exception.getDescription()).thenReturn("user is blocked");
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_login_error_unauthorized_message)));
    }

    @Test
    public void shouldHaveCustomMessageIfIsTooManyAttempts() {
        Mockito.when(exception.getCode()).thenReturn("too_many_attempts");
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_too_many_attempts_error_message)));
    }

    @Test
    public void shouldHaveDefaultMessageIfIsWrongClientType() {
        Mockito.when(exception.getDescription()).thenReturn("Unauthorized");
        final AuthenticationError error = builder.buildFrom(exception);
        assertThat(error.getMessageRes(), is(equalTo(R.string.com_auth0_lock_db_login_error_message)));
    }

}