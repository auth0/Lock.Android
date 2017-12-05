package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.auth0.android.lock.R;


/**
 * Created by lbalmaceda on 24/11/2017.
 */

public class SeparatorView extends LinearLayout {

    public SeparatorView(Context context) {
        super(context);
        init();
    }

    public SeparatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SeparatorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.a0_separator_view, this);
        setOrientation(HORIZONTAL);
    }

}
