package com.auth0.android.lock.utils;

import com.google.gson.reflect.TypeToken;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

public class TypeTokenMatcher<T> extends BaseMatcher<T> {
    private final TypeToken<T> typeToken;
    private final boolean shouldBeNull;

    private TypeTokenMatcher(TypeToken<T> typeToken, boolean shouldBeNull) {
        this.typeToken = typeToken;
        this.shouldBeNull=shouldBeNull;
    }

    public static <T> TypeTokenMatcher<T> isA(TypeToken<T> typeToken) {
        return new TypeTokenMatcher<>(typeToken, false);
    }

    public static <T> TypeTokenMatcher<T> isNull(TypeToken<T> typeToken) {
        return new TypeTokenMatcher<>(typeToken, true);
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("isA(" + typeToken.toString() + ")");
    }

    @Override
    public boolean matches(Object item) {
        if (shouldBeNull){
            return item == null;
        }
        return item != null && typeToken.getRawType().isAssignableFrom(item.getClass());
    }
}