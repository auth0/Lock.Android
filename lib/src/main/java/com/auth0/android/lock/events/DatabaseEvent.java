/*
 * EmailEvent.java
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

import com.auth0.android.lock.views.ValidatedInputView;

public class DatabaseEvent {
    private String username;
    private String email;

    public DatabaseEvent(@NonNull String identity) {
        if (isEmail(identity)) {
            this.email = identity;
        } else if (isUsername(identity)) {
            this.username = identity;
        }
    }

    public DatabaseEvent(@NonNull String email, @Nullable String username) {
        this.email = email;
        this.username = username;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    @Nullable
    public String getUsername() {
        return username;
    }

    public void setUsername(@Nullable String username) {
        this.username = username;
    }

    private boolean isUsername(String input) {
        return input != null && input.matches(ValidatedInputView.USERNAME_REGEX);
    }

    private boolean isEmail(String input) {
        return input != null && input.matches(ValidatedInputView.EMAIL_REGEX);
    }
}
