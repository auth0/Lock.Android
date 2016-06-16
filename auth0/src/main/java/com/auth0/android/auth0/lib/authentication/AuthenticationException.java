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

import java.util.Map;

public class AuthenticationException extends Auth0Exception implements AuthenticationError {

    private String error;
    private String description;
    private Map<String, Object> values;

    public AuthenticationException(Auth0Exception exception) {
        super(exception.getMessage(), exception);
    }

    public AuthenticationException(APIException exception) {
        super(null, exception);
        this.error = (String) exception.getResponseError().remove(ERROR_KEY);
        this.description = (String) exception.getResponseError().remove(ERROR_DESCRIPTION_KEY);
        this.values = exception.getResponseError();
    }

    @Override
    public String getError() {
        return error;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Map<String, Object> getValues() {
        return values;
    }

    @Override
    public String getMessage() {
        if (super.getMessage() != null) {
            return super.getMessage();
        }
        //TODO: Parse error and generate a message
        throw new RuntimeException("Not implemented");
    }
}
