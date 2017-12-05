package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.ServiceLocator;
import com.auth0.android.lock.views.next.configuration.internal.OAuthConnection;
import com.auth0.android.lock.views.next.events.AuthenticationEvent;
import com.auth0.android.lock.views.next.events.NavigationEvent;

import java.util.List;

/**
 * Created by lbalmaceda on 30/11/2017.
 */

public class OAuthView extends LinearLayout {
    public OAuthView(@NonNull Context context, @NonNull List<OAuthConnection> connections) {
        super(context);
        init(connections);
    }

    private void init(List<OAuthConnection> connections) {
        setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        if (connections.size() == 1) {
            OAuthButton button = new OAuthButton(getContext());
            button.setConnection(connections.get(0));
            button.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    AuthenticationEvent event = AuthenticationEvent.webAuthLogin(((OAuthButton) v).getConnection().getName(), null);
                    ServiceLocator.getBus().post(event);
                }
            });
            addView(button, params);
            return;
        }

        StringBuilder sb = new StringBuilder("Login using")
                .append(" ");
        if (connections.size() < 3) {
            sb.append(connections.get(0).getName())
                    .append(" or ")
                    .append(connections.get(1).getName());
        } else {
            sb.append(connections.get(0).getName())
                    .append(", ")
                    .append(connections.get(1).getName())
                    .append(" or ")
                    .append(connections.size())
                    .append("+ more");
        }
        TextView linkText = new TextView(getContext());
        linkText.setTextColor(ContextCompat.getColor(getContext(), R.color.a0LinkText));
        linkText.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.a0_font_14));
        linkText.setMaxLines(2);
        linkText.setText(sb.toString());
        linkText.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        linkText.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceLocator.getBus().post(new NavigationEvent(NavigationEvent.NAVIGATE_TO_SOCIAL_LIST));
            }
        });
        linkText.setGravity(Gravity.CENTER_HORIZONTAL);
        addView(linkText, params);
    }
}
