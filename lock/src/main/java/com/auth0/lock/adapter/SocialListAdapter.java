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
import android.graphics.Typeface;
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

    private static final String TEXT_COLOR_KEY_FORMAT = "color/social_%s_text";
    private static final String COLOR_KEY_FORMAT = "color/social_%s";
    private static final String TEXT_KEY_FORMAT = "string/social_%s";
    private static final String ICON_KEY_FORMAT = "string/social_icon_%s";
    private static final String SOCIAL_FONT_FILE_NAME = "z-social.ttf";

    public SocialListAdapter(Context context, String[] objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String social = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_social_big, parent, false);
        }

        int textColor = textColorForSocialService(social);
        TextView iconLabel = (TextView) convertView.findViewById(R.id.social_icon_label);
        iconLabel.setText(iconForSocialService(social));
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), SOCIAL_FONT_FILE_NAME);
        iconLabel.setTypeface(font);
        iconLabel.setTextColor(textColor);

        TextView textLabel = (TextView) convertView.findViewById(R.id.social_title_label);
        textLabel.setText(titleForSocialService(social));
        textLabel.setTextColor(textColor);

        final float scale = getContext().getResources().getDisplayMetrics().density;
        GradientDrawable normalState = new GradientDrawable();
        normalState.setCornerRadius(5.0f * scale);
        normalState.setColor(colorForSocialService(social));

        convertView.setBackgroundDrawable(normalState);
        return convertView;
    }

    private int textColorForSocialService(String service) {
        String colorIdentifier = String.format(TEXT_COLOR_KEY_FORMAT, normalizeServiceName(service));
        int resId = resourceFromIdentifier(colorIdentifier);
        return resId == 0 ? Color.BLACK : getContext().getResources().getColor(resId);
    }

    private int colorForSocialService(String service) {
        String colorIdentifier = String.format(COLOR_KEY_FORMAT, normalizeServiceName(service));
        int resId = resourceFromIdentifier(colorIdentifier);
        return resId == 0 ? Color.BLACK : getContext().getResources().getColor(resId);
    }

    private int titleForSocialService(String service) {
        String titleIdentifier = String.format(TEXT_KEY_FORMAT, normalizeServiceName(service));
        return resourceFromIdentifier(titleIdentifier);
    }

    private int iconForSocialService(String service) {
        String iconIdentifier = String.format(ICON_KEY_FORMAT, normalizeServiceName(service));
        return resourceFromIdentifier(iconIdentifier);
    }

    private String normalizeServiceName(String name) {
        return name.replace('-', '_');
    }

    private int resourceFromIdentifier(String identifier) {
        return getContext().getResources().getIdentifier(identifier, null, getContext().getPackageName());
    }
}
