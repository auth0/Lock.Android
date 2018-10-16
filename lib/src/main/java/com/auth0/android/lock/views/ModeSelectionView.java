/*
 * ModeSelectionView.java
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
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.AuthMode;

public class ModeSelectionView extends LinearLayout implements TabLayout.OnTabSelectedListener {

    private final ModeSelectedListener callback;
    private TabLayout tabLayout;
    private View firstTabView;
    private View secondTabView;

    public ModeSelectionView(Context context, @NonNull ModeSelectedListener listener) {
        super(context);
        this.callback = listener;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_tab_layout, this);
        tabLayout = (TabLayout) findViewById(R.id.com_auth0_lock_tab_layout);

        firstTabView = inflate(getContext(), R.layout.com_auth0_lock_tab, null);
        secondTabView = inflate(getContext(), R.layout.com_auth0_lock_tab, null);

        final TabLayout.Tab firstTab = tabLayout.newTab()
                .setCustomView(firstTabView)
                .setText(R.string.com_auth0_lock_mode_log_in);
        final TabLayout.Tab secondTab = tabLayout.newTab()
                .setCustomView(secondTabView)
                .setText(R.string.com_auth0_lock_mode_sign_up);

        firstTabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedMode(AuthMode.LOG_IN);
            }
        });
        secondTabView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedMode(AuthMode.SIGN_UP);
            }
        });

        tabLayout.addTab(firstTab);
        tabLayout.addTab(secondTab);
    }

    public void setSelectedMode(@AuthMode int mode) {
        TabLayout.Tab tab = tabLayout.getTabAt(mode);
        tab.select();
        toggleBoldText(firstTabView, mode == AuthMode.LOG_IN);
        toggleBoldText(secondTabView, mode == AuthMode.SIGN_UP);
        callback.onModeSelected(mode);
    }

    private void toggleBoldText(View tabView, boolean bold) {
        final TextView text = (TextView) tabView.findViewById(android.R.id.text1);
        text.setTypeface(bold ? text.getTypeface() : null, bold ? Typeface.BOLD : Typeface.NORMAL);
    }

    @Deprecated
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        //No-Op
    }

    @Deprecated
    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        //No-Op
    }

    @Deprecated
    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //No-Op
    }

    public interface ModeSelectedListener {
        void onModeSelected(@AuthMode int mode);

        @AuthMode
        int getSelectedMode();
    }
}
