/*
 * DbLayout.java
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
import android.support.annotation.IdRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;
import com.squareup.otto.Bus;

public class DbLayout extends RelativeLayout implements View.OnClickListener {
    private Bus bus;
    private Configuration configuration;

    private static final
    @IdRes
    int FORM_ID = 8;
    private static final
    @IdRes
    int SIGN_UP_BTN_ID = 9;
    private static final
    @IdRes
    int CHANGE_PASSWORD_BTN_ID = 10;
    private static final
    @IdRes
    int GO_BACK_BTN_ID = 11;

    private Button signUpBtn;
    private Button changePasswordBtn;
    private Button goBackBtn;


    public DbLayout(Context context) {
        super(context);
    }

    public DbLayout(Context context, Bus lockBus, Configuration configuration) {
        super(context);
        this.bus = lockBus;
        this.configuration = configuration;
        init();
    }

    private void init() {
        signUpBtn = new Button(getContext());
        signUpBtn.setText(R.string.com_auth0_lock_action_sign_up);
        signUpBtn.setId(SIGN_UP_BTN_ID);
        signUpBtn.setOnClickListener(this);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_LEFT);
        params.addRule(BELOW, FORM_ID);
        this.addView(signUpBtn, params);

        changePasswordBtn = new Button(getContext());
        changePasswordBtn.setText(R.string.com_auth0_lock_action_forgot_password);
        changePasswordBtn.setId(CHANGE_PASSWORD_BTN_ID);
        changePasswordBtn.setOnClickListener(this);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(BELOW, FORM_ID);
        this.addView(changePasswordBtn, params);

        goBackBtn = new Button(getContext());
        goBackBtn.setText(R.string.com_auth0_lock_action_go_back);
        goBackBtn.setId(GO_BACK_BTN_ID);
        goBackBtn.setOnClickListener(this);
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(BELOW, FORM_ID);
        this.addView(goBackBtn, params);

        showLoginForm();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case SIGN_UP_BTN_ID:
                showSignUpForm();
                break;
            case CHANGE_PASSWORD_BTN_ID:
                break;
            case GO_BACK_BTN_ID:
                showLoginForm();
                break;
        }
    }

    private void showSignUpForm() {
        this.removeView(findViewById(FORM_ID));

        SignUpFormView signUpForm = new SignUpFormView(getContext(), this.bus, this.configuration);
        signUpForm.setId(FORM_ID);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(ALIGN_PARENT_TOP);
        signUpBtn.setVisibility(View.GONE);
        changePasswordBtn.setVisibility(View.GONE);
        goBackBtn.setVisibility(View.VISIBLE);
        this.addView(signUpForm, 0, params);
    }

    private void showLoginForm() {
        this.removeView(findViewById(FORM_ID));

        LoginFormView loginForm = new LoginFormView(getContext(), this.bus, this.configuration);
        loginForm.setId(FORM_ID);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(ALIGN_PARENT_TOP);
        changePasswordBtn.setVisibility(View.VISIBLE);
        signUpBtn.setVisibility(configuration.isSignUpEnabled() ? View.VISIBLE : View.GONE);
        goBackBtn.setVisibility(View.GONE);
        this.addView(loginForm, 0, params);
    }

    /**
     * Should be called to update the form layout.
     *
     * @return true if it was consumed, false otherwise.
     */
    public boolean onBackPressed() {
        if (goBackBtn.getVisibility() == VISIBLE) {
            showLoginForm();
            return true;
        }
        return false;
    }

}
