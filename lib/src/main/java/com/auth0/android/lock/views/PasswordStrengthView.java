/*
 * CheckableOptionView.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.PasswordStrength;


public class PasswordStrengthView extends LinearLayout {

    private static final String UPPERCASE_REGEX = "^[AZ]$";
    private static final String LOWERCASE_REGEX = "^[az]$";
    private static final String SPECIAL_REGEX = "^[ !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~]$";
    private static final String NUMERIC_REGEX = "^[09]$";
    private static final String IDENTICAL_REGEX = "(.)\\1\\1";

    @PasswordStrength
    private int strength;

    private TextView titleMustHave;
    private TextView titleAtLeast;
    private CheckableOptionView optionLength;
    private CheckableOptionView optionIdenticalCharacters;
    private CheckableOptionView optionLowercase;
    private CheckableOptionView optionUppercase;
    private CheckableOptionView optionNumbers;
    private CheckableOptionView optionSpecialCharacters;

    public PasswordStrengthView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_password_strength, this);
        titleMustHave = (TextView) findViewById(R.id.com_auth0_lock_password_strength_title_must_have);
        titleAtLeast = (TextView) findViewById(R.id.com_auth0_lock_password_strength_title_at_least);

        optionLength = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_length);
        optionIdenticalCharacters = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_identical_characters);
        optionLowercase = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_lowercase);
        optionUppercase = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_uppercase);
        optionNumbers = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_numbers);
        optionSpecialCharacters = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_special_characters);
    }

    /**
     * @see "https://auth0.com/docs/connections/database/password-strength"
     */
    private void showPolicyUI() {
        setVisibility(strength == PasswordStrength.NONE ? View.GONE : VISIBLE);

        switch (strength) {
            case PasswordStrength.EXCELLENT:
                break;
            case PasswordStrength.GOOD:
                break;
            case PasswordStrength.FAIR:
                break;
            case PasswordStrength.LOW:
                break;
            case PasswordStrength.NONE:

                break;
        }
    }

    private boolean hasIdenticalCharacters(@NonNull String input) {
        return IDENTICAL_REGEX.matches(input);
    }

    private boolean hasUppercaseCharacters(@NonNull String input) {
        return UPPERCASE_REGEX.matches(input);
    }

    private boolean hasLowercaseCharacters(@NonNull String input) {
        return LOWERCASE_REGEX.matches(input);
    }

    private boolean hasNumericCharacters(@NonNull String input) {
        return NUMERIC_REGEX.matches(input);
    }

    private boolean hasSpecialCharacters(@NonNull String input) {
        return SPECIAL_REGEX.matches(input);
    }

    private boolean hasMinimumLength(@NonNull String input, int length) {
        return input.length() >= length;
    }

    private boolean atLeastThree(boolean a, boolean b, boolean c, boolean d) {
        boolean one = a && b && (c ^ d);
        boolean two = b && c && (d ^ a);
        boolean three = c && d && (a ^ b);

        return one || two || three;
    }

    /**
     * Sets the current level of Strength that this widget is going to validate.
     *
     * @param strength the required strength level.
     */
    public void setStrength(@PasswordStrength int strength) {
        this.strength = strength;
    }

    /**
     * Checks that all the requirements are meet.
     *
     * @param password the current password to validate
     * @return whether the given password complies with this password policy or not.
     */
    public boolean isValid(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }

        boolean length = true;
        boolean atLeast = true;
        switch (strength) {
            case PasswordStrength.EXCELLENT:
                atLeast = atLeastThree(hasLowercaseCharacters(password), hasUppercaseCharacters(password), hasNumericCharacters(password), hasSpecialCharacters(password))
                        && !hasIdenticalCharacters(password);
                length = hasMinimumLength(password, 10);
                break;
            case PasswordStrength.GOOD:
                atLeast = atLeastThree(hasLowercaseCharacters(password), hasUppercaseCharacters(password), hasNumericCharacters(password), hasSpecialCharacters(password));
                length = hasMinimumLength(password, 8);
                break;
            case PasswordStrength.FAIR:
                atLeast = hasLowercaseCharacters(password) && hasUppercaseCharacters(password) && hasNumericCharacters(password);
                length = hasMinimumLength(password, 8);
                break;
            case PasswordStrength.LOW:
                length = hasMinimumLength(password, 6);
                break;
            case PasswordStrength.NONE:
                length = hasMinimumLength(password, 1);
                break;
        }
        return length && atLeast;
    }

}
