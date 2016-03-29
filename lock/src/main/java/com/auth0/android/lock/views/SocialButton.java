package com.auth0.android.lock.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

class SocialButton extends RelativeLayout {

    @ColorInt
    private int backgroundColor = 0;
    @ColorInt
    private int textColor = 0;
    @StringRes
    private int titleRes;
    @DrawableRes
    private int iconRes;
    private View touchArea;
    private final boolean smallSize;
    private boolean configured;

    public SocialButton(Context context, boolean smallSize) {
        super(context);
        this.configured = false;
        this.smallSize = smallSize;
        init();
    }

    private void init() {
        inflate(getContext(), smallSize ? R.layout.com_auth0_lock_btn_social_small : R.layout.com_auth0_lock_btn_social_large, this);
        ImageView icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        touchArea = smallSize ? (View) icon.getParent() : findViewById(R.id.com_auth0_lock_touch_area);

        if (isInEditMode() || !configured) {
            return;
        }
        setClickable(false);

        ShapeDrawable leftBackground = ViewUtils.getRoundedBackground(getResources(), backgroundColor, smallSize ? ViewUtils.Corners.ALL : ViewUtils.Corners.ONLY_LEFT);
        if (!smallSize) {
            TextView title = (TextView) findViewById(R.id.com_auth0_lock_text);
            title.setTextColor(textColor);
            title.setText(titleRes);

            ShapeDrawable rightBackground = ViewUtils.getRoundedBackground(getResources(), backgroundColor, ViewUtils.Corners.ONLY_RIGHT);
            rightBackground.getPaint().setAlpha(230);
            ViewUtils.setBackground(title, rightBackground);
        } else {
            leftBackground.getPaint().setAlpha(230);
        }

        Drawable touchBackground = getTouchFeedbackBackground(backgroundColor);
        ViewUtils.setBackground(touchArea, touchBackground);
        ViewUtils.setBackground(icon, leftBackground);
        icon.setImageResource(iconRes);
    }

    private StateListDrawable getTouchFeedbackBackground(@ColorInt int color) {
        Drawable background = ViewUtils.getRoundedBackground(getResources(), color, ViewUtils.Corners.ALL);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed},
                background);
        states.addState(new int[]{android.R.attr.state_focused},
                background);
        states.addState(new int[]{},
                new ColorDrawable(Color.TRANSPARENT));
        return states;
    }

    /**
     * Configures the button with the given connection information.
     *
     * @param config contains the connection information.
     */
    public void setSocialConfig(SocialConfig config) {
        this.configured = true;
        this.titleRes = config.getTitle();
        this.iconRes = config.getIcon();
        this.backgroundColor = config.getBackgroundColor();
        this.textColor = config.getTextColor();
        init();
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        touchArea.setOnClickListener(l);
    }
}
