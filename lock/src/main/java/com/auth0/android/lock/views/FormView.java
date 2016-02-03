/*
 * FormView.java
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
import android.widget.RelativeLayout;

import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.UsernameStyle;

public class FormView extends RelativeLayout {

    private ValidatedInputView topInput;
    private ValidatedInputView bottomInput;


    public FormView(Context context) {
        super(context);
        init();
    }

    public FormView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FormView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_form_view, this);
        topInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_top_input);
        topInput.setDataType(ValidatedInputView.DataType.USERNAME_OR_EMAIL);
        bottomInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_bottom_input);
        bottomInput.setDataType(ValidatedInputView.DataType.PASSWORD);

        if (isInEditMode()) {
            return;
        }
    }

    public void setUsernameStyle(UsernameStyle style) {
        switch (style) {
            default:
            case DEFAULT:
                topInput.setDataType(ValidatedInputView.DataType.USERNAME_OR_EMAIL);
                break;
            case EMAIL:
                topInput.setDataType(ValidatedInputView.DataType.EMAIL);
                break;
            case USERNAME:
                topInput.setDataType(ValidatedInputView.DataType.USERNAME);
                break;
        }
    }

    public String getUsernameOrEmail() {
        return topInput.getText();
    }

    public String getPassword() {
        return bottomInput.getText();
    }

    public boolean hasValidData() {
        return topInput.validate() && bottomInput.validate();
    }
}
