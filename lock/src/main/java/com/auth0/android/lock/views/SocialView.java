/*
 * SocialView.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
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

package com.auth0.android.lock.views;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.utils.Configuration;
import com.squareup.otto.Bus;

public class SocialView extends RecyclerView implements SocialViewAdapter.ConnectionAuthenticationListener {

    private Bus bus;

    public enum Mode {
        Grid, List
    }

    public SocialView(Context context, @NonNull Bus bus, @NonNull Configuration configuration, @NonNull Mode mode) {
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
