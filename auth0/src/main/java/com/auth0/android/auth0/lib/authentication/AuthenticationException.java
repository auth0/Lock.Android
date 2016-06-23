/*
 * AuthenticationException.java
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

package com.auth0.android.auth0.lib.authentication;

import com.auth0.android.auth0.lib.APIException;
import com.auth0.android.auth0.lib.Auth0Exception;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationException extends Auth0Exception {

    private static final String ERROR_KEY = "error";
    private static final String ERROR_DESCRIPTION_KEY = "error_description";
    private static final String DEFAULT_MESSAGE = "Error while trying to authenticate with Auth0.";

    private String error;
    private String description;
    private Map<String, Object> rawValues;

    public AuthenticationException(String message, Auth0Exception exception) {
        super(message, exception);
    }

    public AuthenticationException(String message) {
        this(message, null);
    }

    public AuthenticationException(Map<String, Object> values) {
        this(DEFAULT_MESSAGE);
        this.rawValues = values;

        final HashMap<String, Object> valuesCopy = new HashMap<>(values);
        this.error = (String) valuesCopy.remove(ERROR_KEY);
        this.description = (String) valuesCopy.remove(ERROR_DESCRIPTION_KEY);
    }

    public AuthenticationException(APIException exception) {
        super(null, exception);
    }
}
