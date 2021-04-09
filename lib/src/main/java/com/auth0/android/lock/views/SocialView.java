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

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.OAuthLoginEvent;
import com.auth0.android.lock.internal.configuration.AuthMode;
import com.auth0.android.lock.internal.configuration.OAuthConnection;
import com.auth0.android.lock.views.interfaces.LockWidgetOAuth;

import java.util.ArrayList;
import java.util.List;

import static androidx.recyclerview.widget.RecyclerView.LayoutManager;

@SuppressLint("ViewConstructor")
public class SocialView extends LinearLayout implements SocialViewAdapter.OAuthListener {

    private static final String TAG = SocialView.class.getSimpleName();
    private final LockWidgetOAuth lockWidget;
    private SocialViewAdapter adapter;

    /**
     * Creates a new SocialView widget
     *
     * @param lockWidget   the main widget context
     * @param smallButtons Deprecated and no longer used. All SocialView widgets will display using large buttons.
     */
    @SuppressLint("LambdaLast")
    public SocialView(@NonNull LockWidgetOAuth lockWidget, boolean smallButtons) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        Log.v(TAG, "New instance created. Using small buttons: " + smallButtons);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        RecyclerView recycler = new RecyclerView(getContext());
        List<OAuthConnection> connections = lockWidget.getConfiguration().getSocialConnections();
        adapter = new SocialViewAdapter(getContext(), generateAuthConfigs(connections));
        adapter.setCallback(this);
        LayoutManager lm = new GridLayoutManager(getContext(), 1, GridLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(lm);
        recycler.setHasFixedSize(true);
        recycler.setAdapter(adapter);
        recycler.setOverScrollMode(OVER_SCROLL_NEVER);
        final SpacesItemDecoration spaceDecoration = new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_vertical_margin_social), LinearLayoutCompat.VERTICAL);
        recycler.addItemDecoration(spaceDecoration);
        LayoutParams recyclerParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(recycler, recyclerParams);
        setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    private List<AuthConfig> generateAuthConfigs(List<OAuthConnection> connections) {
        List<AuthConfig> configs = new ArrayList<>();
        for (OAuthConnection c : connections) {
            int style = lockWidget.getConfiguration().authStyleForConnection(c.getStrategy(), c.getName());
            configs.add(new AuthConfig(c, style));
        }
        return configs;
    }

    @Override
    public void onAuthenticationRequest(@NonNull OAuthConnection connection) {
        lockWidget.onOAuthLoginRequest(new OAuthLoginEvent(connection));
    }

    /**
     * Updates the Authentication mode for all the SocialButtons on this view.
     *
     * @param mode the new AuthMode.
     */
    public void setCurrentMode(@AuthMode int mode) {
        adapter.setButtonMode(mode);
        adapter.notifyDataSetChanged();
    }
}
