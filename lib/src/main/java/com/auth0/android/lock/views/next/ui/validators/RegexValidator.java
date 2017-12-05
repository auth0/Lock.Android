package com.auth0.android.lock.views.next.ui.validators;

import android.support.annotation.NonNull;
import android.util.Patterns;

/**
 * Created by lbalmaceda on 27/11/2017.
 */

public class RegexValidator implements Validator {
    public static final String EMAIL_REGEX = Patterns.EMAIL_ADDRESS.pattern();
    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]+$";  //TODO: Are dots allowed?
    public static final String PHONE_NUMBER_REGEX = "^[0-9]{6,14}$";
    public static final String MFA_CODE_REGEX = "^[0-9]{4,12}$";

    private final String regex;

    public RegexValidator(@NonNull String regex) {
        this.regex = regex;
    }

    @Override
    public boolean isValid(String text) {
        return text.matches(regex);
    }
}
