package com.auth0.android.lock.errors;

import androidx.annotation.NonNull;

import com.auth0.android.Auth0Exception;

public interface ErrorMessageBuilder<U extends Auth0Exception> {

    @NonNull
    AuthenticationError buildFrom(@NonNull U exception);
}
