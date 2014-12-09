package com.auth0.lock.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auth0.api.APIClient;
import com.auth0.api.BaseCallback;
import com.auth0.core.Application;
import com.auth0.lock.R;
import com.auth0.lock.provider.BusProvider;
import com.google.inject.Inject;

import roboguice.fragment.RoboFragment;

/**
 * Created by hernan on 12/5/14.
 */
public class LoadingFragment extends RoboFragment {

    @Inject private APIClient client;
    @Inject private BusProvider provider;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loading, container, false);
        TextView titleView = (TextView) rootView.findViewById(R.id.title_textView);
        titleView.setText(R.string.loading_title);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        this.provider.getBus().register(this);
        this.client.fetchApplicationInfo(new BaseCallback<Application>() {
            @Override
            public void onSuccess(Application application) {
                Log.i(LoadingFragment.class.getName(), "Fetched app info for tenant " + application.getTenant());
                provider.getBus().post(application);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(LoadingFragment.class.getName(), "Failed to fetch app info", throwable);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        this.provider.getBus().unregister(this);
    }
}
