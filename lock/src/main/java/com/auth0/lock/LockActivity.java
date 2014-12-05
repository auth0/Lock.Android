package com.auth0.lock;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.auth0.lock.fragments.LoadingFragment;


public class LockActivity extends FragmentActivity {

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
