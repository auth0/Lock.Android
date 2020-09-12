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

import com.auth0.android.lock.internal.configuration.AuthMode;
import com.auth0.android.lock.internal.configuration.OAuthConnection;

import java.util.List;

class SocialViewAdapter extends RecyclerView.Adapter<SocialViewAdapter.ViewHolder> {
    private static final String TAG = SocialViewAdapter.class.getSimpleName();

    private final Context context;
    private final List<AuthConfig> authConfigs;
    private OAuthListener callback;
    @AuthMode
    private int buttonMode;

    public SocialViewAdapter(Context context, @NonNull List<AuthConfig> authConfigs) {
        this.context = context;
        this.authConfigs = authConfigs;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = new SocialButton(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.socialButton.setStyle(authConfigs.get(i), buttonMode);
    }

    @Override
    public int getItemCount() {
        return authConfigs.size();
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
    public void setCallback(OAuthListener callback) {
        this.callback = callback;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final SocialButton socialButton;

        public ViewHolder(@NonNull View v) {
            super(v);
            socialButton = (SocialButton) v;
            socialButton.setOnClickListener(this);
        }

        @Override
        public void onClick(@NonNull View view) {
            if (callback != null) {
                final AuthConfig item = authConfigs.get(getAdapterPosition());
                callback.onAuthenticationRequest(item.getConnection());
            } else {
                Log.w(TAG, "No callback was configured");
            }
        }

    }

    public interface OAuthListener {
        /**
         * Called when a SocialButton is clicked.
         *
         * @param connection the connection associated to the button.
         */
        void onAuthenticationRequest(@NonNull OAuthConnection connection);
    }
}
