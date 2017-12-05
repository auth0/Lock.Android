package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.views.next.configuration.internal.OAuthConnection;


/**
 * Created by lbalmaceda on 24/11/2017.
 */

public class OAuthButton extends LinearLayout {

    private ImageView icon;
    private TextView title;

    private OAuthConnection connection;

    public OAuthButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        inflate(getContext(), R.layout.a0_oauth_button, this);
        setBackgroundResource(R.drawable.bg_oauth_button);
        icon = (ImageView) findViewById(R.id.a0_icon);
        title = (TextView) findViewById(R.id.a0_title);
    }

    public void setConnection(OAuthConnection connection) {
        this.connection = connection;
        title.setText(getResources().getString(R.string.a0_oauth_login_with, connection.getName()));
        icon.setImageResource(R.drawable.com_auth0_lock_ic_social_amazon);
        //TODO: Use connection color
        getBackground().mutate().setColorFilter(ContextCompat.getColor(getContext(), R.color.a0LinkText), PorterDuff.Mode.SCREEN);
    }

    public OAuthConnection getConnection() {
        return connection;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int px48 = getResources().getDimensionPixelSize(R.dimen.a0_size_48);
        setMeasuredDimension(getMeasuredWidth(), px48);
    }
}
