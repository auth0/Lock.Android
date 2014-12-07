package com.auth0.lock.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.api.APIClient;
import com.auth0.api.BaseCallback;
import com.auth0.core.Application;
import com.auth0.lock.R;
import com.google.inject.Inject;

import roboguice.fragment.RoboFragment;

/**
 * Created by hernan on 12/5/14.
 */
public class LoadingFragment extends RoboFragment {

    @Inject private APIClient client;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_loading, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.client.fetchApplicationInfo(new BaseCallback<Application>() {
            @Override
            public void onSuccess(Application application) {
                Log.i(LoadingFragment.class.getName(), "Fetched app info for tenant " + application.getTenant());
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(LoadingFragment.class.getName(), "Failed to fetch app info", throwable);
            }
        });
    }
}
