package com.auth0.android.lock;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.auth0.android.lock.adapters.Country;
import com.auth0.android.lock.internal.configuration.PasswordlessMode;

import static com.auth0.android.lock.internal.configuration.PasswordlessMode.DISABLED;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.EMAIL_LINK;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_CODE;
import static com.auth0.android.lock.internal.configuration.PasswordlessMode.SMS_LINK;

class PasswordlessIdentityHelper {
    private static final String LAST_PASSWORDLESS_IDENTITY_KEY = "last_passwordless_identity";
    private static final String LAST_PASSWORDLESS_COUNTRY_KEY = "last_passwordless_country";
    private static final String LAST_PASSWORDLESS_MODE_KEY = "last_passwordless_mode";
    private static final String LOCK_PREFERENCES_NAME = "Lock";
    private static final String COUNTRY_DATA_DIV = "@";

    private final SharedPreferences sp;
    @PasswordlessMode
    private final int mode;

    PasswordlessIdentityHelper(@NonNull Context context, @PasswordlessMode int mode) {
        sp = context.getSharedPreferences(LOCK_PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.mode = mode;
    }

    public void saveIdentity(@NonNull String identity, @Nullable Country country) {
        String countryData = country != null ? country.getIsoCode() + COUNTRY_DATA_DIV + country.getDialCode() : null;
        sp.edit()
                .putString(LAST_PASSWORDLESS_IDENTITY_KEY, identity)
                .putString(LAST_PASSWORDLESS_COUNTRY_KEY, countryData)
                .putInt(LAST_PASSWORDLESS_MODE_KEY, mode)
                .apply();
    }

    @Nullable
    public Country getLastCountry() {
        Country country = null;
        String countryInfo = sp.getString(LAST_PASSWORDLESS_COUNTRY_KEY, null);
        if (countryInfo != null) {
            String isoCode = countryInfo.split(COUNTRY_DATA_DIV)[0];
            String dialCode = countryInfo.split(COUNTRY_DATA_DIV)[1];
            country = new Country(isoCode, dialCode);
        }
        return country;
    }

    @NonNull
    public String getLastIdentity() {
        String identity = sp.getString(LAST_PASSWORDLESS_IDENTITY_KEY, "");
        Country country = getLastCountry();
        //noinspection ConstantConditions
        if (country != null && identity.startsWith(country.getDialCode())) {
            identity = identity.substring(country.getDialCode().length());
        }
        //noinspection ConstantConditions
        return identity;
    }

    public boolean hasLoggedInBefore() {
        @PasswordlessMode
        int lastMode = sp.getInt(LAST_PASSWORDLESS_MODE_KEY, DISABLED);
        return lastMode != DISABLED && hasSameConnection(lastMode);
    }

    private boolean hasSameConnection(@PasswordlessMode int lastMode) {
        if (lastMode == mode) {
            return true;
        }
        boolean sms = (lastMode == SMS_CODE || lastMode == SMS_LINK) && (mode == SMS_CODE || mode == SMS_LINK);
        boolean email = (lastMode == EMAIL_CODE || lastMode == EMAIL_LINK) && (mode == EMAIL_CODE || mode == EMAIL_LINK);
        return sms || email;
    }
}
