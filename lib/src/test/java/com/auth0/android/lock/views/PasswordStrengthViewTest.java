package com.auth0.android.lock.views;

import android.app.Activity;

import com.auth0.android.lock.BuildConfig;
import com.auth0.android.lock.internal.configuration.PasswordComplexity;
import com.auth0.android.lock.internal.configuration.PasswordStrength;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 21, manifest = Config.NONE)
public class PasswordStrengthViewTest {

    public static final String PASSWORD_TOO_LONG = "otPtgNsthiK98lw61BEwevHChF87YMNqVZDpvxgAWBESkBL" +
            "ytrGRrG6JDhZjIyt2HqqxJDeyeKLlKnRG1pnOoA5xaZWKI6zK6tk37BocILDZESio107JZiHWbc4DlIFe0";
    public static final String PASSWORD_128_LONG = "otPtgNsthiK98lw61BEwevHChF87YMNqVZDpvxgAWBESkBL" +
            "ytrGRrG6JDhZjIyt2HqqxJDeyeKLlKnRG1pnOoA5xaZWKI6zK6tk37BocILDZESio107JZiHWbc4DlIFe";
    public static final String PASSWORD_10_LONG = "123KImd$$.";
    public static final String PASSWORD_8_LONG = "12KImd$.";
    public static final String PASSWORD_6_LONG = "1Kd$$.";
    public static final String PASSWORD_1_LONG = "1";
    public static final String PASSWORD_EMPTY = "";

    public static final String PASSWORD_NUMERIC = "1234567890";
    public static final String PASSWORD_ALPHA = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String PASSWORD_ALPHA_LOWER_CASE = "abcdefghijklmnopqrstuvwxyz";
    public static final String PASSWORD_ALPHA_UPPER_CASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final String PASSWORD_SPECIAL = " !\"#$%&'()*+,-./:;<=>?@[\\]^_`{|}~";
    public static final String PASSWORD_IDENTICAL = "AAAAaaaa1111$$$$";

    public static final String PASSWORD_NUMERIC_SPECIAL = "12$#5!@321$314#%5667^&";
    public static final String PASSWORD_ALPHA_NUMERIC = "ab12ab12ab12";
    public static final String PASSWORD_ALPHA_NUMERIC_SPECIAL = "a!b1@ca2$bc1@bd2j$1j3";
    public static final String PASSWORD_ALPHA_CASE_NUMERIC_SPECIAL = "a!B1@CA2$bc1@bd2j$1j3E";
    public static final String PASSWORD_ALPHA_CASE_NUMERIC = "aB1aB1aB1aB1aB1";

    private PasswordStrengthView view;

    @Before
    public void setUp() throws Exception {
        Activity context = Robolectric.buildActivity(Activity.class).create().get();
        view = new PasswordStrengthView(context);
    }

    @Test
    public void shouldHandlePasswordStrengthNONE() throws Exception {
        view.setStrength(PasswordStrength.NONE);

        assertTrue(view.isValid(PASSWORD_NUMERIC));
        assertTrue(view.isValid(PASSWORD_ALPHA));
        assertTrue(view.isValid(PASSWORD_ALPHA_LOWER_CASE));
        assertTrue(view.isValid(PASSWORD_ALPHA_UPPER_CASE));
        assertTrue(view.isValid(PASSWORD_SPECIAL));
        assertTrue(view.isValid(PASSWORD_IDENTICAL));

        assertTrue(view.isValid(PASSWORD_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_NUMERIC));
        assertTrue(view.isValid(PASSWORD_ALPHA_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC));

        assertTrue(view.isValid(PASSWORD_128_LONG));
        assertTrue(view.isValid(PASSWORD_10_LONG));
        assertTrue(view.isValid(PASSWORD_8_LONG));
        assertTrue(view.isValid(PASSWORD_6_LONG));
        assertTrue(view.isValid(PASSWORD_1_LONG));

        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));
    }

    @Test
    public void shouldHandlePasswordStrengthLOW() throws Exception {
        view.setStrength(PasswordStrength.LOW);

        assertTrue(view.isValid(PASSWORD_NUMERIC));
        assertTrue(view.isValid(PASSWORD_ALPHA));
        assertTrue(view.isValid(PASSWORD_ALPHA_LOWER_CASE));
        assertTrue(view.isValid(PASSWORD_ALPHA_UPPER_CASE));
        assertTrue(view.isValid(PASSWORD_SPECIAL));
        assertTrue(view.isValid(PASSWORD_IDENTICAL));

        assertTrue(view.isValid(PASSWORD_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_NUMERIC));
        assertTrue(view.isValid(PASSWORD_ALPHA_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC));

        assertTrue(view.isValid(PASSWORD_128_LONG));
        assertTrue(view.isValid(PASSWORD_10_LONG));
        assertTrue(view.isValid(PASSWORD_8_LONG));
        assertTrue(view.isValid(PASSWORD_6_LONG));
        assertFalse(view.isValid(PASSWORD_1_LONG));

        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));
    }

    @Test
    public void shouldHandlePasswordStrengthFAIR() throws Exception {
        view.setStrength(PasswordStrength.FAIR);

        assertFalse(view.isValid(PASSWORD_NUMERIC));
        assertFalse(view.isValid(PASSWORD_ALPHA));
        assertFalse(view.isValid(PASSWORD_ALPHA_LOWER_CASE));
        assertFalse(view.isValid(PASSWORD_ALPHA_UPPER_CASE));
        assertFalse(view.isValid(PASSWORD_SPECIAL));
        assertTrue(view.isValid(PASSWORD_IDENTICAL));

        assertFalse(view.isValid(PASSWORD_NUMERIC_SPECIAL));
        assertFalse(view.isValid(PASSWORD_ALPHA_NUMERIC));
        assertFalse(view.isValid(PASSWORD_ALPHA_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC));

        assertTrue(view.isValid(PASSWORD_128_LONG));
        assertTrue(view.isValid(PASSWORD_10_LONG));
        assertTrue(view.isValid(PASSWORD_8_LONG));
        assertFalse(view.isValid(PASSWORD_6_LONG));
        assertFalse(view.isValid(PASSWORD_1_LONG));

        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));
    }

    @Test
    public void shouldHandlePasswordStrengthGOOD() throws Exception {
        view.setStrength(PasswordStrength.GOOD);

        assertFalse(view.isValid(PASSWORD_NUMERIC));
        assertFalse(view.isValid(PASSWORD_ALPHA));
        assertFalse(view.isValid(PASSWORD_ALPHA_LOWER_CASE));
        assertFalse(view.isValid(PASSWORD_ALPHA_UPPER_CASE));
        assertFalse(view.isValid(PASSWORD_SPECIAL));
        assertTrue(view.isValid(PASSWORD_IDENTICAL));

        assertFalse(view.isValid(PASSWORD_NUMERIC_SPECIAL));
        assertFalse(view.isValid(PASSWORD_ALPHA_NUMERIC));
        assertTrue(view.isValid(PASSWORD_ALPHA_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC));

        assertTrue(view.isValid(PASSWORD_128_LONG));
        assertTrue(view.isValid(PASSWORD_10_LONG));
        assertTrue(view.isValid(PASSWORD_8_LONG));
        assertFalse(view.isValid(PASSWORD_6_LONG));
        assertFalse(view.isValid(PASSWORD_1_LONG));

        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));
    }

    @Test
    public void shouldHandlePasswordStrengthEXCELLENT() throws Exception {
        view.setStrength(PasswordStrength.EXCELLENT);

        assertFalse(view.isValid(PASSWORD_NUMERIC));
        assertFalse(view.isValid(PASSWORD_ALPHA));
        assertFalse(view.isValid(PASSWORD_ALPHA_LOWER_CASE));
        assertFalse(view.isValid(PASSWORD_ALPHA_UPPER_CASE));
        assertFalse(view.isValid(PASSWORD_SPECIAL));
        assertFalse(view.isValid(PASSWORD_IDENTICAL));

        assertFalse(view.isValid(PASSWORD_NUMERIC_SPECIAL));
        assertFalse(view.isValid(PASSWORD_ALPHA_NUMERIC));
        assertTrue(view.isValid(PASSWORD_ALPHA_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC_SPECIAL));
        assertTrue(view.isValid(PASSWORD_ALPHA_CASE_NUMERIC));

        assertTrue(view.isValid(PASSWORD_128_LONG));
        assertTrue(view.isValid(PASSWORD_10_LONG));
        assertFalse(view.isValid(PASSWORD_8_LONG));
        assertFalse(view.isValid(PASSWORD_6_LONG));
        assertFalse(view.isValid(PASSWORD_1_LONG));

        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));
    }

    @Test
    public void shouldOverrideMinLength() throws Exception {
        PasswordComplexity passwordComplexity;

        // NONE is normally >= 1
        passwordComplexity = new PasswordComplexity(PasswordStrength.NONE, 3);
        view.setPasswordComplexity(passwordComplexity);

        assertFalse(view.isValid("a"));
        assertTrue(view.isValid("abc"));
        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));

        // LOW is normally >= 6
        passwordComplexity = new PasswordComplexity(PasswordStrength.LOW, 8);
        view.setPasswordComplexity(passwordComplexity);

        assertFalse(view.isValid("abcdef"));
        assertTrue(view.isValid("abcdefgh"));
        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));

        // FAIR is normally >= 8
        passwordComplexity = new PasswordComplexity(PasswordStrength.FAIR, 10);
        view.setPasswordComplexity(passwordComplexity);

        assertFalse(view.isValid("ABCdefg9"));
        assertTrue(view.isValid("ABCdefghi9"));
        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));

        // GOOD is normally >= 8
        passwordComplexity = new PasswordComplexity(PasswordStrength.GOOD, 11);
        view.setPasswordComplexity(passwordComplexity);

        assertFalse(view.isValid("ABCdefg9"));
        assertTrue(view.isValid("ABCdefghij9"));
        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));

        // EXCELLENT is normally >= 10
        passwordComplexity = new PasswordComplexity(PasswordStrength.EXCELLENT, 15);
        view.setPasswordComplexity(passwordComplexity);

        assertFalse(view.isValid("ABCdefgh9!"));
        assertTrue(view.isValid("ABCdefghijklm9!"));
        assertFalse(view.isValid(PASSWORD_EMPTY));
        assertFalse(view.isValid(PASSWORD_TOO_LONG));
        assertFalse(view.isValid(null));
    }
}