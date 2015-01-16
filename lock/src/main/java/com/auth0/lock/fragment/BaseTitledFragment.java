/*
 * BaseTitledFragment.java
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
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.TextView;

import com.auth0.api.APIClient;
import com.auth0.lock.Lock;
import com.auth0.lock.LockProvider;
import com.auth0.lock.R;
import com.squareup.otto.Bus;

import java.util.Map;

public abstract class BaseTitledFragment extends Fragment {

    public static final String AUTHENTICATION_PARAMETER_ARGUMENT = "AUTHENTICATION_PARAMETER_ARGUMENT";
    public static final String AUTHENTICATION_USES_EMAIL_ARGUMENT = "AUTHENTICATION_USES_EMAIL_ARGUMENT";

    protected APIClient client;
    protected Bus bus;
    protected Map<String, Object> authenticationParameters;
    boolean useEmail;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Lock lock = getLock();
        client = lock.getAPIClient();
        bus = lock.getBus();
        final Bundle arguments = getArguments();
        authenticationParameters = arguments != null ? (Map<String, Object>) arguments.getSerializable(AUTHENTICATION_PARAMETER_ARGUMENT) : null;
        useEmail = arguments == null || arguments.getBoolean(AUTHENTICATION_USES_EMAIL_ARGUMENT);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView titleView = (TextView) view.findViewById(R.id.title_textView);
        titleView.setText(getTitleResource());
    }

    protected abstract int getTitleResource();

    private Lock getLock() {
        LockProvider provider = (LockProvider) getActivity().getApplication();
        return provider.getLock();
    }

}
