package com.auth0.android.lock.social;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.auth0.android.lock.Configuration;

/**
 * Created by lbalmaceda on 1/22/16.
 */
public class SocialView extends RecyclerView {

    public enum Mode {
        Grid, List
    }

    public SocialView(Context context, Configuration configuration, Mode mode) {
        super(context);
        init(configuration, mode);
    }

    private void init(Configuration configuration, Mode mode) {
        SocialViewAdapter adapter = new SocialViewAdapter(getContext(), configuration.getSocialStrategies());
        LayoutManager lm = mode == Mode.Grid ? new GridLayoutManager(getContext(), 3) : new LinearLayoutManager(getContext());
        setLayoutManager(lm);
        setHasFixedSize(true);
        setAdapter(adapter);
    }
}
