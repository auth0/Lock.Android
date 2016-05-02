/*
 * ValidatedUserInputView.java
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
import android.util.AttributeSet;

import com.auth0.android.lock.Configuration;

import static com.auth0.android.lock.enums.UsernameStyle.DEFAULT;
import static com.auth0.android.lock.enums.UsernameStyle.EMAIL;
import static com.auth0.android.lock.enums.UsernameStyle.USERNAME;

public class ValidatedUsernameInputView extends ValidatedInputView {

    public ValidatedUsernameInputView(Context context) {
        super(context);
    }

    public ValidatedUsernameInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ValidatedUsernameInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * Choose username or email DataType, according to the Configuration
     *
     * @param configuration of the instance
     */
    public void chooseDataType(Configuration configuration) {
        @ValidatedInputView.DataType
        int type = 0;
        switch (configuration.getUsernameStyle()) {
            case EMAIL:
                type = DataType.EMAIL;
                break;
            case USERNAME:
                type = DataType.USERNAME;
                break;
            case DEFAULT:
                if (configuration.isUsernameRequired()) {
                    type = DataType.USERNAME_OR_EMAIL;
                } else {
                    type = DataType.EMAIL;
                }
                break;
        }
        setDataType(type);
    }
}
