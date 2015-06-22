package com.auth0.lock.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

    private static final String TAG = LoadingFragment.class.getName();
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
        loadInfo();
        return rootView;
    }

    private void loadInfo() {
        if (!isConnectionAvailable()) {
            Log.e(TAG, "No Connection to Internet");
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.com_auth0_application_load_failed_title)
                    .setMessage(R.string.com_auth0_application_load_no_connection_message)
                    .setCancelable(false)
                    .setPositiveButton(R.string.com_auth0_application_retry_button_text, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            loadInfo();
                        }
                    })
                    .show();
            return;
        }
        client.fetchApplicationInfo(new BaseCallback<Application>() {
            @Override
            public void onSuccess(Application application) {
                Log.i(TAG, "Fetched app info for tenant " + application.getTenant());
                bus.post(application);
            }

            @Override
            public void onFailure(Throwable throwable) {
                Log.e(TAG, "Failed to fetch app info", throwable);
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.com_auth0_application_load_failed_title)
                        .setMessage(R.string.com_auth0_application_load_failed_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.com_auth0_application_retry_button_text, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadInfo();
                            }
                        })
                        .show();
            }
        });
    }

    private boolean isConnectionAvailable() {
        if (getActivity().checkCallingOrSelfPermission("android.permission.ACCESS_NETWORK_STATE") == PackageManager.PERMISSION_GRANTED) {
            ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            return activeNetwork != null &&
                    activeNetwork.isConnectedOrConnecting();
        }
        return true;
    }
}
