package com.auth0.android.lock.views.next;

import android.support.v7.app.AppCompatActivity;

import com.auth0.android.lock.ServiceLocator;

/**
 * Created by lbalmaceda on 06/12/2017.
 */

public class BusActivity extends AppCompatActivity {
    @Override
    protected void onStart() {
        super.onStart();
        ServiceLocator.getBus().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ServiceLocator.getBus().unregister(this);
    }
}
