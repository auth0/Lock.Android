package com.auth0.lock.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.auth0.api.APIClient;
import com.auth0.api.callback.BaseCallback;
import com.auth0.core.Application;
import com.auth0.lock.Lock;
import com.auth0.lock.R;
import com.squareup.otto.Bus;

public class LoadingFragment extends Fragment {

    private APIClient client;
    private Bus bus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Lock lock = Lock.getLock(getActivity());
        client = lock.getAPIClient();
        bus = lock.getBus();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.com_auth0_fragment_loading, container, false);
        TextView titleView = (TextView) rootView.findViewById(R.id.com_auth0_title_textView);
        titleView.setText(R.string.com_auth0_loading_title);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
        client.fetchApplicationInfo(new BaseCallback<Application>() {
            @Override
            public void onSuccess(Application application) {
                Log.i(LoadingFragment.class.getName(), "Fetched app info for tenant " + application.getTenant());
                bus.post(application);
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
        bus.unregister(this);
    }

}
