/*
 * SmallSocialListFragment.java
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.auth0.lock.LockContext;
import com.auth0.lock.R;
import com.auth0.lock.adapter.SocialListAdapter;
import com.auth0.lock.event.IdentityProviderAuthenticationRequestEvent;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

public class SmallSocialListFragment extends android.support.v4.app.Fragment {

    private static final String SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT = "strategies";
    private static final String TAG = SmallSocialListFragment.class.getName();

    GridView gridView;
    Bus bus;

    public SmallSocialListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bus = LockContext.getLock(getActivity()).getBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.com_auth0_fragment_small_social_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        List<String> services = bundle.getStringArrayList(SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT);
        Log.d(TAG, "About to display " + services.size() + " services");
        gridView = (GridView) view.findViewById(R.id.com_auth0_social_grid_view);
        final SocialListAdapter adapter = new SocialListAdapter(getActivity(), services.toArray(new String[services.size()]), true);
        gridView.setAdapter(adapter);
        int maxItemCount = getResources().getInteger(R.integer.com_auth0_social_grid_max_elements);
        gridView.setNumColumns(services.size() > maxItemCount ? maxItemCount : services.size());
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String serviceName = (String) parent.getItemAtPosition(position);
                Log.d(SocialFragment.class.getName(), "Selected service " + serviceName);
                bus.post(new IdentityProviderAuthenticationRequestEvent(serviceName));
            }
        });
    }

    public static SmallSocialListFragment newFragment(ArrayList<String> services) {
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(SOCIAL_FRAGMENT_STRATEGIES_ARGUMENT, services);
        SmallSocialListFragment fragment = new SmallSocialListFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

}
