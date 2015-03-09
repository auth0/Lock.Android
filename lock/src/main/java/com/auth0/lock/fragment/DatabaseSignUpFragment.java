/*
 * DatabaseSignUpFragment.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.lock.fragment;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.auth0.api.callback.AuthenticationCallback;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Token;
import com.auth0.core.UserProfile;
import com.auth0.lock.R;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.AuthenticationError;
import com.auth0.lock.event.AuthenticationEvent;
import com.auth0.lock.event.NavigationEvent;
import com.auth0.lock.event.SignUpEvent;
import com.auth0.lock.validation.SignUpValidator;
import com.auth0.lock.widget.CredentialField;

public class DatabaseSignUpFragment extends BaseTitledFragment {

    public static final String LOGIN_AFTER_SIGNUP_ARGUMENT = "LOGIN_AFTER_SIGN_UP";

    private boolean loginAfterSignUp;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle arguments = getArguments();
        loginAfterSignUp = arguments == null || arguments.getBoolean(LOGIN_AFTER_SIGNUP_ARGUMENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_database_sign_up, container, false);
        final FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.db_signup_form_container, SignUpFormFragment.newFragment(useEmail, loginAfterSignUp, authenticationParameters))
                .commit();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button cancelButton = (Button) view.findViewById(R.id.db_signup_cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bus.post(NavigationEvent.BACK);
            }
        });
    }

    @Override
    protected int getTitleResource() {
        return R.string.database_signup_title;
    }

}
