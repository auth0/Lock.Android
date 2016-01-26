package com.auth0.android.lock.views;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.utils.Configuration;
import com.squareup.otto.Bus;

/**
 * Created by lbalmaceda on 1/22/16.
 */
public class SocialView extends RecyclerView implements SocialViewAdapter.ConnectionAuthenticationListener {

    private Bus bus;

    public enum Mode {
        Grid, List
    }

    public SocialView(Context context, Bus bus, Configuration configuration, Mode mode) {
        super(context);
        this.bus = bus;
        init(configuration, mode);
    }

    private void init(Configuration configuration, Mode mode) {
        SocialViewAdapter adapter = new SocialViewAdapter(getContext(), configuration.getSocialStrategies());
        LayoutManager lm = mode == Mode.Grid ? new GridLayoutManager(getContext(), 3) : new LinearLayoutManager(getContext());
        setLayoutManager(lm);
        setHasFixedSize(true);
        adapter.setCallback(this);
        setAdapter(adapter);
    }

    @Override
    public void onConnectionClicked(String connectionName) {
        bus.post(new SocialConnectionEvent(connectionName));
    }
}
