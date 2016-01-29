package com.auth0.android.lock.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

class SocialButton extends RelativeLayout {

    private static final int BTN_CORNER_RADIUS = 5;
    private enum Corners {ONLY_LEFT, ONLY_RIGHT, ALL}

    @ColorInt
    private int backgroundColor = 0;
    @ColorInt
    private int textColor = 0;
    @StringRes
    private int titleRes;
    @DrawableRes
    private int iconRes;
    private View touchArea;
    private boolean configured;

    public SocialButton(Context context) {
        super(context);
        this.configured = false;
        init();
    }

    public SocialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.configured = false;
        init();
    }

    public SocialButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.configured = false;
        init();
    }

    @SuppressWarnings("deprecation")
    private void init() {
        inflate(getContext(), R.layout.com_auth0_btn_social_large, this);
        setClickable(false);

        ImageView icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        TextView title = (TextView) findViewById(R.id.com_auth0_lock_text);
        touchArea = findViewById(R.id.com_auth0_lock_touch_area);

        if (isInEditMode() || !configured) {
            return;
        }

        icon.setImageResource(iconRes);
        title.setTextColor(textColor);
        title.setText(titleRes);

        Drawable leftBackground = getRoundedBackground(backgroundColor, Corners.ONLY_LEFT);
        Drawable rightBackground = getRoundedBackground(backgroundColor, Corners.ONLY_RIGHT);
        Drawable touchBackground = getTouchFeedbackBackground(backgroundColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            icon.setBackground(leftBackground);
            title.setBackground(rightBackground);
            touchArea.setBackground(touchBackground);
        } else {
            icon.setBackgroundDrawable(leftBackground);
            title.setBackgroundDrawable(rightBackground);
            touchArea.setBackgroundDrawable(touchBackground);
        }
    }

    private ShapeDrawable getRoundedBackground(@ColorInt int color, Corners corners) {
        float r = ViewUtils.dipToPixels(getResources(), BTN_CORNER_RADIUS);
        float[] outerR = new float[0];
        switch (corners) {
            case ONLY_LEFT:
                outerR = new float[]{r, r, 0, 0, 0, 0, r, r};
                break;
            case ONLY_RIGHT:
                outerR = new float[]{0, 0, r, r, r, r, 0, 0};
                break;
            case ALL:
                outerR = new float[]{r, r, r, r, r, r, r, r};
                break;
        }

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        if (corners == Corners.ONLY_RIGHT) {
            drawable.getPaint().setAlpha(230);
        }
        return drawable;
    }

    private StateListDrawable getTouchFeedbackBackground(@ColorInt int color) {
        Drawable background = getRoundedBackground(color, Corners.ALL);

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
    public void setSocialConfig(SocialButtonConfig config) {
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
