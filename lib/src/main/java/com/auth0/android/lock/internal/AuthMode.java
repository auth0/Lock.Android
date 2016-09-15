package com.auth0.android.lock.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.internal.AuthMode.LOG_IN;
import static com.auth0.android.lock.internal.AuthMode.SIGN_UP;

@IntDef({LOG_IN, SIGN_UP})
@Retention(RetentionPolicy.SOURCE)
public @interface AuthMode {
    int LOG_IN = 0;
    int SIGN_UP = 1;
}
