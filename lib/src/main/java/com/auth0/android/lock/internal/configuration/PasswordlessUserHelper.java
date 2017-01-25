package com.auth0.android.lock.internal.configuration;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.auth0.android.lock.adapters.Country;

public class PasswordlessUserHelper {
    private static final String LAST_PASSWORDLESS_IDENTITY_KEY = "last_passwordless_identity";
    private static final String LAST_PASSWORDLESS_COUNTRY_KEY = "last_passwordless_country";
    private static final String LAST_PASSWORDLESS_MODE_KEY = "last_passwordless_mode";
    private static final String LOCK_PREFERENCES_NAME = "Lock";
    private static final String COUNTRY_DATA_DIV = "@";

    private final SharedPreferences sp;
    @PasswordlessMode
    private final int passwordlessMode;

    public PasswordlessUserHelper(@NonNull Context context, @PasswordlessMode int passwordlessMode) {
        sp = context.getSharedPreferences(LOCK_PREFERENCES_NAME, Context.MODE_PRIVATE);
        this.passwordlessMode = passwordlessMode;
    }

    public void saveIdentity(@NonNull String identity, @Nullable Country country) {
        String countryData = country != null ? country.getIsoCode() + COUNTRY_DATA_DIV + country.getDialCode() : null;
        sp.edit()
                .putString(LAST_PASSWORDLESS_IDENTITY_KEY, identity)
                .putString(LAST_PASSWORDLESS_COUNTRY_KEY, countryData)
                .putInt(LAST_PASSWORDLESS_MODE_KEY, passwordlessMode)
                .apply();
    }

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

    public String getLastIdentity() {
        String identity = sp.getString(LAST_PASSWORDLESS_IDENTITY_KEY, "");
        Country country = getLastCountry();
        if (country != null && identity.startsWith(country.getDialCode())) {
            identity = identity.substring(country.getDialCode().length());
        }
        return identity;
    }

    public boolean hadLoggedInBefore() {
        @PasswordlessMode
        int lastMode = sp.getInt(LAST_PASSWORDLESS_MODE_KEY, PasswordlessMode.DISABLED);
        return lastMode != PasswordlessMode.DISABLED && lastMode == passwordlessMode;
    }
}
