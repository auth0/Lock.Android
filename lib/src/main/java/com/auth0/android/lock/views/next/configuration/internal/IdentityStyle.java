package com.auth0.android.lock.views.next.configuration.internal;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.views.next.configuration.internal.IdentityStyle.EMAIL;
import static com.auth0.android.lock.views.next.configuration.internal.IdentityStyle.USERNAME;
import static com.auth0.android.lock.views.next.configuration.internal.IdentityStyle.USERNAME_AND_EMAIL;

/**
 * Created by lbalmaceda on 04/12/2017.
 */

@IntDef({USERNAME, EMAIL, USERNAME_AND_EMAIL})
@Retention(RetentionPolicy.SOURCE)
public @interface IdentityStyle {
    int USERNAME = 1;
    int EMAIL = 2;
    int USERNAME_AND_EMAIL = 3;
}