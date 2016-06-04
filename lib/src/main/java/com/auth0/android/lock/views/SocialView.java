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
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.auth0.android.lock.events.SocialConnectionEvent;
import com.auth0.android.lock.utils.Strategy;
import com.auth0.android.lock.views.interfaces.LockWidgetSocial;

import java.util.List;

import static android.support.v7.widget.RecyclerView.LayoutManager;

public class SocialView extends LinearLayout implements SocialViewAdapter.ConnectionAuthenticationListener {

    private static final String TAG = SocialView.class.getSimpleName();
    private LockWidgetSocial lockWidget;
    private RecyclerView recycler;

    public SocialView(LockWidgetSocial lockWidget, boolean smallButtons) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        Log.v(TAG, "New instance created. Using small buttons: " + smallButtons);
        init(smallButtons);
    }

    private void init(boolean smallButtons) {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        recycler = new RecyclerView(getContext());
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
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    public void onConnectionClicked(String connectionName) {
        lockWidget.onSocialLogin(new SocialConnectionEvent(connectionName));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int recyclerHeight = ViewUtils.measureViewHeight(recycler);
        setMeasuredDimension(getMeasuredWidth(), recyclerHeight);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode){
            case MeasureSpec.UNSPECIFIED:
                setMeasuredDimension(getMeasuredWidth(), recyclerHeight);
                break;
            case MeasureSpec.AT_MOST:
                setMeasuredDimension(getMeasuredWidth(), Math.min(recyclerHeight, parentHeight));
                break;
            case MeasureSpec.EXACTLY:
                setMeasuredDimension(getMeasuredWidth(), parentHeight);
                break;
        }
    }
}
