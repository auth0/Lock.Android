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

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.utils.Strategy;
import com.auth0.android.lock.views.interfaces.LockWidgetSocial;

import java.util.List;

import static android.support.v7.widget.RecyclerView.LayoutManager;

public class SocialView extends LinearLayout implements SocialViewAdapter.ConnectionAuthenticationListener {

    private LockWidgetSocial lockWidget;

    public SocialView(LockWidgetSocial lockWidget, boolean smallButtons) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init(smallButtons);
    }

    private void init(boolean smallButtons) {
        int maxWidth = getResources().getDimensionPixelOffset(R.dimen.com_auth0_lock_max_widget_width);
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        RecyclerView recycler = new RecyclerView(getContext());
        List<Strategy> socialStrategies = lockWidget.getConfiguration().getSocialStrategies();
        SocialViewAdapter adapter = new SocialViewAdapter(getContext(), socialStrategies);
        adapter.setButtonSize(smallButtons);
        adapter.setCallback(this);
        LayoutManager lm = new GridLayoutManager(getContext(), 1, smallButtons ? HORIZONTAL : VERTICAL, false);
        recycler.setLayoutManager(lm);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);
        recycler.setOverScrollMode(OVER_SCROLL_NEVER);
        LayoutParams recyclerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(recycler, recyclerParams);
        setLayoutParams(new ViewGroup.LayoutParams(maxWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onConnectionClicked(String connectionName) {
        lockWidget.onSocialLogin(new SocialConnectionEvent(connectionName));
    }
}
