/*
 * SocialViewAdapter.java
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
import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.lock.R;
import com.auth0.android.lock.utils.Strategies;
import com.auth0.android.lock.utils.Strategy;

import java.util.List;

class SocialViewAdapter extends RecyclerView.Adapter<SocialViewAdapter.ViewHolder> {
    private static final String TAG = SocialViewAdapter.class.getSimpleName();

    private final Context context;
    private final List<Strategy> strategyList;
    private ConnectionAuthenticationListener callback;

    public SocialViewAdapter(Context context, @NonNull List<Strategy> strategyList) {
        this.context = context;
        this.strategyList = strategyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = new SocialButton(context);
        int width = (int) context.getResources().getDimension(R.dimen.com_auth0_btn_social_big_width);
        int height = (int) context.getResources().getDimension(R.dimen.com_auth0_btn_social_big_height);
        view.setLayoutParams(new RecyclerView.LayoutParams(width, height));
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Strategy item = strategyList.get(i);
        viewHolder.socialButton.setSocialConfig(new SocialButtonConfig(context, item));
    }

    private void getButtonColorForStrategy(Strategy strategy) {
    }

    @Override
    public int getItemCount() {
        return strategyList.size();
    }


    public void setCallback(ConnectionAuthenticationListener callback) {
        this.callback = callback;
    }

    private String getTitleForStrategy(Strategy strategy) {
        return strategy.getName();
    }

    @DrawableRes
    private int getIconForStrategy(Strategy strategy) {
        String name = strategy.getName().toLowerCase();
        if (name.startsWith(Strategies.Facebook.getName())) {
            return R.drawable.com_auth0_social_icon_facebook;
        } else if (name.startsWith(Strategies.Twitter.getName())) {
            return R.drawable.com_auth0_social_icon_twitter;
        } else {
            return R.drawable.com_auth0_social_icon_fitbit;
        }
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        SocialButton socialButton;

        public ViewHolder(View v) {
            super(v);
            socialButton = (SocialButton) v;
            socialButton.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (callback != null) {
                Strategy strategy = strategyList.get(getAdapterPosition());
                callback.onConnectionClicked(strategy.getName());       //returns the name of the first connection element
            } else {
                Log.w(TAG, ConnectionAuthenticationListener.class.getSimpleName() + " not configured");
            }
        }

    }

    public interface ConnectionAuthenticationListener {
        /**
         * Called when a SocialButton is clicked.
         *
         * @param connectionName the connectionName associated to the button.
         */
        void onConnectionClicked(String connectionName);
    }
}
