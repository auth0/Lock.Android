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
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.auth0.android.lock.enums.AuthMode;
import com.auth0.android.lock.utils.json.Strategy;

import java.util.List;

class SocialViewAdapter extends RecyclerView.Adapter<SocialViewAdapter.ViewHolder> {
    private static final String TAG = SocialViewAdapter.class.getSimpleName();

    private final Context context;
    private final List<Strategy> strategyList;
    private boolean useSmallButtons;
    private ConnectionAuthenticationListener callback;
    @AuthMode
    private int buttonMode;

    public SocialViewAdapter(Context context, @NonNull List<Strategy> strategyList) {
        this.context = context;
        this.strategyList = strategyList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = new SocialButton(context, useSmallButtons);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Strategy item = strategyList.get(i);
        viewHolder.socialButton.setSocialConfig(new SocialConfig(context, item), buttonMode);
    }

    @Override
    public int getItemCount() {
        return strategyList.size();
    }

    /**
     * Sets the size of the buttons this list will hold. Use large buttons if you want it to
     * scroll vertically, or small buttons if you want it to scroll horizontally.
     *
     * @param useSmall whether to use small or large buttons.
     */
    public void setButtonSize(boolean useSmall) {
        this.useSmallButtons = useSmall;
    }

    /**
     * Sets the button mode to Sign Up or Log In. This will prefix the mode text before the title.
     *
     * @param mode the mode to use on the current button list.
     */
    public void setButtonMode(@AuthMode int mode) {
        this.buttonMode = mode;
    }

    /**
     * Sets the callback to notify when the user clicks a SocialButton.
     *
     * @param callback the callback
     */
    public void setCallback(ConnectionAuthenticationListener callback) {
        this.callback = callback;
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
                Log.w(TAG, "No callback was configured");
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
