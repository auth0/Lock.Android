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

package com.auth0.lock.util;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.auth0.core.Connection;
import com.auth0.core.Strategy;
import com.auth0.lock.Configuration;
import com.auth0.lock.Lock;
import com.auth0.lock.fragment.BaseTitledFragment;
import com.auth0.lock.fragment.DatabaseChangePasswordFragment;
import com.auth0.lock.fragment.DatabaseLoginFragment;
import com.auth0.lock.fragment.DatabaseSignUpFragment;
import com.auth0.lock.fragment.SocialDBFragment;
import com.auth0.lock.fragment.SocialFragment;
import com.auth0.lock.fragment.SocialSignUpFragment;

import java.util.ArrayList;
import java.util.HashMap;

public class LockFragmentBuilder {

    private final Lock lock;
    private boolean signUpMode;

    public LockFragmentBuilder(Lock lock) {
        this.lock = lock;
    }

    public Fragment signUp() {
        final DatabaseSignUpFragment fragment = new DatabaseSignUpFragment();
        Bundle arguments = new Bundle();
        arguments.putBoolean(DatabaseSignUpFragment.LOGIN_AFTER_SIGNUP_ARGUMENT, lock.shouldLoginAfterSignUp());
        arguments.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.shouldUseEmail());
        arguments.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
        fragment.setArguments(arguments);
        return fragment;
    }

    public Fragment resetPassword() {
        final DatabaseChangePasswordFragment fragment = new DatabaseChangePasswordFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
        arguments.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.shouldUseEmail());
        arguments.putBoolean(DatabaseChangePasswordFragment.REQUIRE_PASSWORD_NOW, lock.shouldRequirePasswordOnPasswordReset());
        fragment.setArguments(arguments);
        return fragment;
    }

    public Fragment login() {
        final DatabaseLoginFragment fragment = new DatabaseLoginFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
        arguments.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.shouldUseEmail());
        fragment.setArguments(arguments);
        return fragment;
    }


    public Fragment loginWithSocial() {
        final SocialDBFragment fragment = new SocialDBFragment();
        if (lock.getConfiguration() != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(SocialDBFragment.SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT, activeSocialStrategies());
            bundle.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.shouldUseEmail());
            bundle.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public Fragment enterpriseLoginWithSocial(Connection connection) {
        final Fragment fragment = loginWithSocial();
        Bundle bundle = fragment.getArguments() == null ? new Bundle() : fragment.getArguments();
        bundle.putParcelable(DatabaseLoginFragment.DEFAULT_CONNECTION_ARGUMENT, connection);
        fragment.setArguments(bundle);
        return fragment;
    }

    public Fragment social() {
        final SocialFragment fragment = new SocialFragment();
        if (lock.getConfiguration() != null) {
            Bundle bundle = new Bundle();
            bundle.putStringArrayList(SocialFragment.SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT, activeSocialStrategies());
            bundle.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    public Fragment root() {
        Configuration configuration = lock.getConfiguration();
        if (lock.getConfiguration() == null) {
            return login();
        }

        if (signUpMode) {
            return SocialSignUpFragment.newFragment(activeSocialStrategies());
        }

        final boolean hasEnterprise = !configuration.getEnterpriseStrategies().isEmpty();
        final boolean hasSocial = !configuration.getSocialStrategies().isEmpty();
        final boolean hasDB = configuration.getDefaultDatabaseConnection() != null;
        final boolean hasAD = configuration.getActiveDirectoryStrategy() != null;

        if (!hasDB && hasSocial && !hasEnterprise) {
            return social();
        }

        if (!hasDB && hasSocial && hasAD) {
            return enterpriseLoginWithSocial(configuration.getDefaultActiveDirectoryConnection());
        }

        if ((hasDB || hasEnterprise) && hasSocial) {
            return loginWithSocial();
        }

        if (!hasDB && hasAD) {
            return enterpriseLoginWithConnection(configuration.getDefaultActiveDirectoryConnection(), true);
        }

        return login();
    }

    public Fragment enterpriseLoginWithConnection(Connection connection) {
        return enterpriseLoginWithConnection(connection, false);
    }

    public void setSignUpMode(boolean signUpMode) {
        this.signUpMode = signUpMode;
    }

    private Fragment enterpriseLoginWithConnection(Connection connection, boolean isRoot) {
        final DatabaseLoginFragment fragment = new DatabaseLoginFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(BaseTitledFragment.AUTHENTICATION_PARAMETER_ARGUMENT, new HashMap<>(lock.getAuthenticationParameters()));
        arguments.putBoolean(BaseTitledFragment.AUTHENTICATION_USES_EMAIL_ARGUMENT, lock.shouldUseEmail());
        if (connection != null) {
            arguments.putParcelable(DatabaseLoginFragment.AD_ENTERPRISE_CONNECTION_ARGUMENT, connection);
        }
        arguments.putBoolean(DatabaseLoginFragment.IS_MAIN_LOGIN_ARGUMENT, isRoot);
        fragment.setArguments(arguments);
        return fragment;
    }

    private ArrayList<String> activeSocialStrategies() {
        Configuration configuration = lock.getConfiguration();
        ArrayList<String> strategies = new ArrayList<>(configuration.getSocialStrategies().size());
        for (Strategy strategy : configuration.getSocialStrategies()) {
            strategies.add(strategy.getName());
        }
        return strategies;
    }

}
