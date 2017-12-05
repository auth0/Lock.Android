package com.auth0.android.lock.views.next.events;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public class NavigationEvent {

    public static final int NAVIGATE_TO_LOGIN = 0;
    public static final int NAVIGATE_TO_FORGOT_PASSWORD = 1;
    public static final int NAVIGATE_TO_SIGN_UP = 2;
    public static final int NAVIGATE_TO_SOCIAL_LIST = 3;

    @Retention(SOURCE)
    @IntDef({NAVIGATE_TO_LOGIN, NAVIGATE_TO_FORGOT_PASSWORD, NAVIGATE_TO_SIGN_UP, NAVIGATE_TO_SOCIAL_LIST})
    public @interface Action {
    }

    @Action
    private final int action;

    public NavigationEvent(@Action int action) {
        this.action = action;
    }

    @Action
    public int getAction() {
        return action;
    }
}
