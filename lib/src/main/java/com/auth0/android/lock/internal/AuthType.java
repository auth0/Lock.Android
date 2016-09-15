package com.auth0.android.lock.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.internal.AuthType.DATABASE;
import static com.auth0.android.lock.internal.AuthType.ENTERPRISE;
import static com.auth0.android.lock.internal.AuthType.PASSWORDLESS;
import static com.auth0.android.lock.internal.AuthType.SOCIAL;

@IntDef({DATABASE, ENTERPRISE, PASSWORDLESS, SOCIAL})
@Retention(RetentionPolicy.SOURCE)
public @interface AuthType {
    int DATABASE = 0;
    int ENTERPRISE = 1;
    int PASSWORDLESS = 2;
    int SOCIAL = 3;
}
