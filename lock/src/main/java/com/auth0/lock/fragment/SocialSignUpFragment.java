/*
 * SocialSignUpFragment.java
 *
 * Copyright (c) 2015 Auth0 (http://auth0.com)
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.lock.Lock;
import com.auth0.lock.LockContext;
import com.auth0.lock.R;

import java.util.ArrayList;

public class SocialSignUpFragment extends BaseTitledFragment {

    private static final String SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT = "strategies";


    public SocialSignUpFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            Bundle bundle = getArguments();
            ArrayList<String> services = bundle.getStringArrayList(SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT);
            Lock lock = LockContext.getLock(getActivity());
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.com_auth0_social_list_container, SmallSocialListFragment.newFragment(services))
                    .replace(R.id.com_auth0_signup_form_container, SignUpFormFragment.newFragment(lock.shouldUseEmail(), lock.shouldLoginAfterSignUp(), lock.getAuthenticationParameters()))
                    .commit();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_fragment_social_sign_up, container, false);
    }

    @Override
    protected int getTitleResource() {
        return R.string.com_auth0_database_signup_title;
    }

    public static SocialSignUpFragment newFragment(ArrayList<String> services) {
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT, services);
        SocialSignUpFragment fragment = new SocialSignUpFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

}
