package com.auth0.lock;

import android.os.Bundle;

import com.auth0.lock.fragments.LoadingFragment;

import roboguice.activity.RoboFragmentActivity;


public class LockActivity extends RoboFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new LoadingFragment())
                    .commit();
        }
    }
    
}
