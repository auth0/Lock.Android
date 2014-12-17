/*
 * LockFragmentBuilder.java
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

package com.auth0.lock;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.auth0.core.Application;
import com.auth0.core.Strategy;
import com.auth0.lock.fragment.DatabaseLoginFragment;
import com.auth0.lock.fragment.DatabaseResetPasswordFragment;
import com.auth0.lock.fragment.DatabaseSignUpFragment;
import com.auth0.lock.fragment.SocialFragment;

import java.util.ArrayList;

/**
 * Created by hernan on 12/16/14.
 */
public class LockFragmentBuilder {

    private Application application;

    public Fragment signUp() {
        return new DatabaseSignUpFragment();
    }

    public Fragment resetPassword() {
        return new DatabaseResetPasswordFragment();
    }

    public Fragment login() {
        return new DatabaseLoginFragment();
    }

    public Fragment social() {
        final SocialFragment fragment = new SocialFragment();
        if (application != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(SocialFragment.SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT, activeSocialStrategies());
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public Fragment root() {
        if (application == null) {
            return login();
        }

        if (application.getDatabaseStrategy() == null && application.getSocialStrategies().size() > 0) {
            return social();
        }

        return login();
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public Application getApplication() {
        return application;
    }

    private ArrayList<String> activeSocialStrategies() {
        ArrayList<String> strategies = new ArrayList<>(application.getSocialStrategies().size());
        for (Strategy strategy : application.getSocialStrategies()) {
            strategies.add(strategy.getName());
        }
        return strategies;
    }
}
