package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.views.next.configuration.internal.OAuthConnection;

import java.util.ArrayList;


/**
 * Created by lbalmaceda on 24/11/2017.
 */

public class OAuthAdapter extends ArrayAdapter<OAuthConnection> {
    //TODO: Refactor into a RecyclerView Adapter

    public OAuthAdapter(Context context, ArrayList<OAuthConnection> connections) {
        super(context, R.layout.a0_oauth_item, connections);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.a0_oauth_item, parent, false);
            vh = new ViewHolder();
            vh.title = (TextView) convertView.findViewById(R.id.a0_title);
            vh.icon = (ImageView) convertView.findViewById(R.id.a0_icon);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }
        OAuthConnection connection = getItem(position);
        vh.title.setText(getContext().getResources().getString(R.string.a0_oauth_login_with, connection.getName()));
        vh.icon.setImageResource(R.drawable.com_auth0_lock_ic_social_github);//TODO: Get from connection
        vh.icon.getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.a0LinkText), PorterDuff.Mode.SCREEN);
        return convertView;
    }

    @Nullable
    @Override
    public OAuthConnection getItem(int position) {
        return super.getItem(position);
    }

    private class ViewHolder {
        public TextView title;
        public ImageView icon;
    }
}
