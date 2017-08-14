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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.PasswordStrength;

import java.util.regex.Pattern;


public class PasswordStrengthView extends LinearLayout {

    private static final String TAG = PasswordStrengthView.class.getSimpleName();

    private static final int MAX_IDENTICAL_CHARACTERS = 2;
    private static final int MAX_LENGTH = 128;
    private static final int MIN_LENGTH_EXCELLENT = 10;
    private static final int MIN_LENGTH_GOOD = 8;
    private static final int MIN_LENGTH_FAIR = 8;
    private static final int MIN_LENGTH_LOW = 6;
    private static final int MIN_LENGTH_NONE = 1;

    private final Pattern patternUppercase = Pattern.compile("^.*[A-Z]+.*$");
    private final Pattern patternLowercase = Pattern.compile("^.*[a-z]+.*$");
    private final Pattern patternSpecial = Pattern.compile("^.*[ !\"#\\$%&'\\(\\)\\*\\+,-\\./:;<=>\\?@\\[\\\\\\]\\^_`{\\|}~]+.*$");
    private final Pattern patternNumeric = Pattern.compile("^.*[0-9]+.*$");
    private final Pattern patternIdentical = Pattern.compile("^.*(?=(.)\\1{" + MAX_IDENTICAL_CHARACTERS + ",}).*$");

    @PasswordStrength
    private int strength;

    private TextView titleAtLeast;
    private CheckableOptionView optionLength;
    private CheckableOptionView optionIdenticalCharacters;
    private CheckableOptionView optionLowercase;
    private CheckableOptionView optionUppercase;
    private CheckableOptionView optionNumeric;
    private CheckableOptionView optionSpecialCharacters;

    public PasswordStrengthView(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_password_strength, this);
        titleAtLeast = (TextView) findViewById(R.id.com_auth0_lock_password_strength_title_at_least);

        optionLength = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_length);
        optionLength.setMandatory(true);
        optionIdenticalCharacters = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_identical_characters);
        optionIdenticalCharacters.setMandatory(true);
        optionIdenticalCharacters.setChecked(true);
        optionLowercase = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_lowercase);
        optionUppercase = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_uppercase);
        optionNumeric = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_numeric);
        optionSpecialCharacters = (CheckableOptionView) findViewById(R.id.com_auth0_lock_password_strength_option_special_characters);
        setStrength(PasswordStrength.NONE);
    }

    /**
     * @see "https://auth0.com/docs/connections/database/password-strength"
     */
    private void refreshPolicyUI() {
        if (strength == PasswordStrength.NONE) {
            setVisibility(GONE);
            return;
        }
        setVisibility(VISIBLE);

        optionLowercase.setMandatory(strength == PasswordStrength.FAIR);
        optionUppercase.setMandatory(strength == PasswordStrength.FAIR);
        optionNumeric.setMandatory(strength == PasswordStrength.FAIR);

        titleAtLeast.setVisibility(strength == PasswordStrength.FAIR || strength == PasswordStrength.LOW ? GONE : VISIBLE);
        String lengthRequirements = getContext().getResources().getString(R.string.com_auth0_lock_password_strength_chars_length);
        optionLength.setText(String.format(lengthRequirements, getMinimumLength()));

        optionLowercase.setVisibility(strength == PasswordStrength.LOW ? GONE : VISIBLE);
        optionUppercase.setVisibility(strength == PasswordStrength.LOW ? GONE : VISIBLE);
        optionNumeric.setVisibility(strength == PasswordStrength.LOW ? GONE : VISIBLE);
        optionSpecialCharacters.setVisibility(strength == PasswordStrength.EXCELLENT || strength == PasswordStrength.GOOD ? VISIBLE : GONE);
        optionIdenticalCharacters.setVisibility(strength == PasswordStrength.EXCELLENT ? VISIBLE : GONE);
    }

    private boolean hasIdenticalCharacters(@NonNull String input) {
        boolean v = !patternIdentical.matcher(input).matches();
        optionIdenticalCharacters.setChecked(v);
        return v;
    }

    private boolean hasUppercaseCharacters(@NonNull String input) {
        boolean v = patternUppercase.matcher(input).matches();
        optionUppercase.setChecked(v);
        return v;
    }

    private boolean hasLowercaseCharacters(@NonNull String input) {
        boolean v = patternLowercase.matcher(input).matches();
        optionLowercase.setChecked(v);
        return v;
    }

    private boolean hasNumericCharacters(@NonNull String input) {
        boolean v = patternNumeric.matcher(input).matches();
        optionNumeric.setChecked(v);
        return v;
    }

    private boolean hasSpecialCharacters(@NonNull String input) {
        boolean v = patternSpecial.matcher(input).matches();
        optionSpecialCharacters.setChecked(v);
        return v;
    }

    private boolean hasMinimumLength(@NonNull String input, int length) {
        boolean v = input.length() >= length && input.length() <= MAX_LENGTH;
        optionLength.setChecked(v);
        return v;
    }

    private boolean atLeastThree(boolean a, boolean b, boolean c, boolean d) {
        boolean all = a && b && c && d;
        boolean one = a && b && (c ^ d);
        boolean two = b && c && (d ^ a);
        boolean three = c && d && (a ^ b);

        return all || one || two || three;
    }

    private boolean allThree(boolean a, boolean b, boolean c) {
        return a && b && c;
    }

    private int getMinimumLength() {
        switch (strength) {
            case PasswordStrength.EXCELLENT:
                return MIN_LENGTH_EXCELLENT;
            case PasswordStrength.GOOD:
                return MIN_LENGTH_GOOD;
            case PasswordStrength.FAIR:
                return MIN_LENGTH_FAIR;
            case PasswordStrength.LOW:
                return MIN_LENGTH_LOW;
            default:
            case PasswordStrength.NONE:
                return MIN_LENGTH_NONE;
        }
    }

    /**
     * Sets the current level of Strength that this widget is going to validate.
     * Updating the strength will hide the widget until {@link #isValid(String)} is called.
     *
     * @param strength the required strength level.
     */
    public void setStrength(@PasswordStrength int strength) {
        this.strength = strength;
        refreshPolicyUI();
        setVisibility(GONE);
    }

    /**
     * Checks that all the requirements are meet.
     *
     * @param password the current password to validate
     * @return whether the given password complies with this password policy or not.
     */
    public boolean isValid(String password) {
        if (password == null) {
            return false;
        }

        boolean length = hasMinimumLength(password, getMinimumLength());
        boolean other = true;
        switch (strength) {
            case PasswordStrength.EXCELLENT:
                boolean atLeast = atLeastThree(hasLowercaseCharacters(password), hasUppercaseCharacters(password), hasNumericCharacters(password), hasSpecialCharacters(password));
                other = hasIdenticalCharacters(password) && atLeast;
                break;
            case PasswordStrength.GOOD:
                other = atLeastThree(hasLowercaseCharacters(password), hasUppercaseCharacters(password), hasNumericCharacters(password), hasSpecialCharacters(password));
                break;
            case PasswordStrength.FAIR:
                other = allThree(hasLowercaseCharacters(password), hasUppercaseCharacters(password), hasNumericCharacters(password));
                break;
            case PasswordStrength.LOW:
            case PasswordStrength.NONE:
        }
        return length && other;
    }

}
