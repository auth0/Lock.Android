package com.auth0.android.lock.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.enums.AuthType.DATABASE;
import static com.auth0.android.lock.enums.AuthType.ENTERPRISE;
import static com.auth0.android.lock.enums.AuthType.PASSWORDLESS;
import static com.auth0.android.lock.enums.AuthType.SOCIAL;

@IntDef({DATABASE, ENTERPRISE, PASSWORDLESS, SOCIAL})
@Retention(RetentionPolicy.SOURCE)
public @interface AuthType {
    int DATABASE = 0;
    int ENTERPRISE = 1;
    int PASSWORDLESS = 2;
    int SOCIAL = 3;
}
