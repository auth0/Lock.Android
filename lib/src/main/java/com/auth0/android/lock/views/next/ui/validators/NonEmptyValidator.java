package com.auth0.android.lock.views.next.ui.validators;

import android.text.TextUtils;

/**
 * Created by lbalmaceda on 27/11/2017.
 */

public class NonEmptyValidator implements Validator {
    @Override
    public boolean isValid(String text) {
        return !TextUtils.isEmpty(text);
    }
}
