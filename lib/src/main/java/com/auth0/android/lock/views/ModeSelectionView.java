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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.AuthMode;

public class ModeSelectionView extends LinearLayout implements TabLayout.OnTabSelectedListener {

    private final ModeSelectedListener callback;
    private TabLayout tabLayout;

    public ModeSelectionView(Context context, @NonNull ModeSelectedListener listener) {
        super(context);
        this.callback = listener;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_tab_layout, this);
        tabLayout = (TabLayout) findViewById(R.id.com_auth0_lock_tab_layout);
        tabLayout.addTab(tabLayout.newTab()
                .setCustomView(R.layout.com_auth0_lock_tab)
                .setText(R.string.com_auth0_lock_mode_log_in));
        tabLayout.addTab(tabLayout.newTab()
                .setCustomView(R.layout.com_auth0_lock_tab)
                .setText(R.string.com_auth0_lock_mode_sign_up));
        tabLayout.setOnTabSelectedListener(this);
    }

    public void setSelectedMode(@AuthMode int mode) {
        TabLayout.Tab tab = tabLayout.getTabAt(mode);
        tab.select();
        toggleBoldText(tab, true);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        toggleBoldText(tab, true);
        //noinspection WrongConstant
        callback.onModeSelected(getCurrentMode(tab));
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        toggleBoldText(tab, false);
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        //noinspection WrongConstant
        callback.onModeSelected(getCurrentMode(tab));
    }

    private void toggleBoldText(TabLayout.Tab tab, boolean bold) {
        final TextView text = (TextView) tab.getCustomView().findViewById(android.R.id.text1);
        text.setTypeface(bold ? text.getTypeface() : null, bold ? Typeface.BOLD : Typeface.NORMAL);
    }

    public interface ModeSelectedListener {
        void onModeSelected(@AuthMode int mode);

        @AuthMode
        int getSelectedMode();
    }

    @AuthMode
    private int getCurrentMode(TabLayout.Tab tab) {
        return tab.getPosition() == 1 ? AuthMode.SIGN_UP : AuthMode.LOG_IN;
    }
}
