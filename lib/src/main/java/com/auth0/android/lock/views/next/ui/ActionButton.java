package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;


/**
 * Created by lbalmaceda on 24/11/2017.
 */

public class ActionButton extends LinearLayout {

    private TextView title;

    public ActionButton(Context context) {
        super(context);
        init();
    }

    public ActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.a0_action_button, this);
        setBackgroundResource(R.drawable.bg_action_button);
        title = (TextView) findViewById(R.id.a0_title);
    }

    public void setText(@NonNull String text) {
        title.setText(text);
    }

}
