package com.auth0.android.lock.enums;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.enums.PasswordStrength.EXCELLENT;
import static com.auth0.android.lock.enums.PasswordStrength.FAIR;
import static com.auth0.android.lock.enums.PasswordStrength.GOOD;
import static com.auth0.android.lock.enums.PasswordStrength.LOW;
import static com.auth0.android.lock.enums.PasswordStrength.NONE;

@IntDef({NONE, LOW, FAIR, GOOD, EXCELLENT})
@Retention(RetentionPolicy.SOURCE)
public @interface PasswordStrength {
    int NONE = 0;
    int LOW = 1;
    int FAIR = 2;
    int GOOD = 3;
    int EXCELLENT = 4;
}