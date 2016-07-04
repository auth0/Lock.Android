package com.auth0.android.lock.errors;

import com.auth0.android.auth0.Auth0Exception;

public interface ErrorMessageBuilder<U extends Auth0Exception> {

    AuthenticationError buildFrom(U exception);
}
