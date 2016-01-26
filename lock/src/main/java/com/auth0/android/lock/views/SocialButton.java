package com.auth0.android.lock.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.DrawableRes;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.auth0.android.lock.R;

/**
 * Created by lbalmaceda on 1/22/16.
 */
class SocialButton extends FrameLayout {

    private ImageView icon;
    private TextView title;

    public SocialButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        setClickable(true);
        setFocusable(true);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        setBackgroundResource(backgroundResource);
        typedArray.recycle();

        icon = new ImageView(getContext());
        title = new TextView(getContext());

        addView(icon, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        addView(title, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setIcon(@DrawableRes int iconRes) {
        this.icon.setImageResource(iconRes);
    }
}
