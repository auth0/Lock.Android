/*
 * SocialListAdapter.java
 *
 * Copyright (c) 2014 Auth0 (http://auth0.com)
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

package com.auth0.lock.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.auth0.lock.R;

/**
 * Created by hernan on 12/16/14.
 */
public class SocialListAdapter extends ArrayAdapter<String> {

    public SocialListAdapter(Context context, String[] objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String social = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_social_big, parent, false);
        }

        TextView iconLabel = (TextView) convertView.findViewById(R.id.social_icon_label);
        iconLabel.setText(social.substring(0, 1));

        TextView textLabel = (TextView) convertView.findViewById(R.id.social_title_label);
        textLabel.setText(titleForSocialService(social));

        final float scale = getContext().getResources().getDisplayMetrics().density;
        GradientDrawable normalState = new GradientDrawable();
        normalState.setCornerRadius(5.0f * scale);
        normalState.setColor(colorForSocialService(social));

        convertView.setBackgroundDrawable(normalState);
        return convertView;
    }

    private int colorForSocialService(String service) {
        String colorIdentifier = "social_" + service.replace('-', '_');
        int resId = getContext().getResources().getIdentifier(colorIdentifier, "color", getContext().getPackageName());
        return getContext().getResources().getColor(resId);
    }

    private int titleForSocialService(String service) {
        String titleIdentifier = "social_" + service.replace('-', '_');
        int resId = getContext().getResources().getIdentifier(titleIdentifier, "string", getContext().getPackageName());
        return resId;
    }

}
