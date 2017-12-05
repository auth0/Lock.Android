package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

/**
 * Created by lbalmaceda on 29/11/2017.
 */

public class HeaderView extends LinearLayout {
    private ImageView logo;
    private TextView title;

    public HeaderView(Context context) {
        super(context);
        init();
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HeaderView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_HORIZONTAL);
        inflate(getContext(), R.layout.a0_header_view, this);
        logo = (ImageView) findViewById(R.id.a0_icon);
        title = (TextView) findViewById(R.id.a0_title);

    }

    public void setTitle(@StringRes int resId) {
        this.title.setText(resId);
    }

    public void setLogo(@DrawableRes int resId) {
        this.logo.setImageResource(resId);
    }
}
