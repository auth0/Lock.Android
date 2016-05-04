/*
 * DbSignUpEvent.java
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

package com.auth0.android.lock.events;


import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;

public class DatabaseSignUpEvent {

    @NonNull
    private String email;
    @Nullable
    private String username;
    @Nullable
    private String password;
    private boolean loginAfterSignUp;
    private Map<String, String> extraFields;

    public DatabaseSignUpEvent(@NonNull String email, @Nullable String username, @Nullable String password, boolean loginAfterSignUp) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.loginAfterSignUp = loginAfterSignUp;
    }

    @NonNull
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    @Nullable
    public String getPassword() {
        return password;
    }

    public boolean loginAfterSignUp() {
        return loginAfterSignUp;
    }

    @Nullable
    public Map<String, String> extraFields() {
        return extraFields;
    }

    public void setExtraFields(@NonNull Map<String, String> customFields) {
        this.extraFields = customFields;
    }

    public void setPassword(@NonNull String password) {
        this.password = password;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }
}
