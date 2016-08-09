package com.auth0.android.lock.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.enums.AuthMode;

class SocialButton extends RelativeLayout {

    private final boolean smallSize;
    private ImageView icon;
    private View touchArea;
    private TextView title;

    public SocialButton(Context context, boolean smallSize) {
        super(context);
        this.smallSize = smallSize;
        init();
    }

    private void init() {
        inflate(getContext(), smallSize ? R.layout.com_auth0_lock_btn_social_small : R.layout.com_auth0_lock_btn_social_large, this);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        title = smallSize ? null : (TextView) findViewById(R.id.com_auth0_lock_text);
        touchArea = smallSize ? (View) icon.getParent() : findViewById(R.id.com_auth0_lock_touch_area);
        setClickable(false);
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
     * @param mode   the current button mode. Used to prefix the title with "Log In" or "Sign Up".
     */
    public void setSocialConfig(SocialConfig config, @AuthMode int mode) {
        String name = config.getName();
        int iconRes = config.getIcon();
        int backgroundColor = config.getBackgroundColor();

        ShapeDrawable leftBackground = ViewUtils.getRoundedBackground(getResources(), backgroundColor, smallSize ? ViewUtils.Corners.ALL : ViewUtils.Corners.ONLY_LEFT);
        if (!smallSize) {
            final String prefixFormat = getResources().getString(mode == AuthMode.LOG_IN ? R.string.com_auth0_lock_social_log_in : R.string.com_auth0_lock_social_sign_up);
            title.setText(String.format(prefixFormat, name));
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

    @Override
    public void setOnClickListener(OnClickListener l) {
        touchArea.setOnClickListener(l);
    }
}
