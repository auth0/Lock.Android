/*
 * SocialFragment.java
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.auth0.lock.R;
import com.auth0.lock.adapter.SocialListAdapter;
import com.auth0.lock.error.LoginAuthenticationErrorBuilder;
import com.auth0.lock.event.IdentityProviderAuthenticationRequestEvent;

import java.util.List;

public class SocialFragment extends BaseTitledFragment {

    public static final String SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT = "strategies";

    LoginAuthenticationErrorBuilder errorBuilder;

    ListView listView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        errorBuilder = new LoginAuthenticationErrorBuilder();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_social, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        List<String> services = bundle.getStringArrayList(SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT);
        Log.d(SocialFragment.class.getName(), "Obtained " + services.size() + " services");
        final SocialListAdapter adapter = new SocialListAdapter(getActivity(), services.toArray(new String[services.size()]));
        listView = (ListView) view.findViewById(R.id.social_button_list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String serviceName = (String) parent.getItemAtPosition(position);
                Log.d(SocialFragment.class.getName(), "Selected service " + serviceName);
                bus.post(new IdentityProviderAuthenticationRequestEvent(serviceName));
            }
        });
    }

    @Override
    public void onStart() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onStop() {
        super.onPause();
        bus.unregister(this);
    }

    @Override
    protected int getTitleResource() {
        return R.string.social_only_title;
    }

}
