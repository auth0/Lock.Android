package com.auth0.android.lock.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.AuthMode;

@SuppressLint("Instantiatable")
class SocialButton extends RelativeLayout {

    private static final int NORMAL_STATE_ALPHA = 230;
    private static final float FOCUSED_STATE_ALPHA = 0.64f;

    private ImageView icon;
    private TextView title;

    public SocialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SocialButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_btn_social_large, this);
        icon = findViewById(R.id.com_auth0_lock_icon);
        title = findViewById(R.id.com_auth0_lock_text);
        setFocusableInTouchMode(false);
        setFocusable(true);
        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                v.setAlpha(hasFocus ? FOCUSED_STATE_ALPHA : 1f);
            }
        });
    }

    private StateListDrawable getTouchFeedbackBackground(@ColorInt int pressedColor, @ViewUtils.Corners int corners) {
        final ShapeDrawable normalBackground;
        final ShapeDrawable pressedBackground;

        boolean shouldDrawOutline = pressedColor == Color.WHITE;
        if (shouldDrawOutline) {
            int outlineColor = getResources().getColor(R.color.com_auth0_lock_social_light_background_focused);
            normalBackground = ViewUtils.getRoundedOutlineBackground(getResources(), outlineColor);
            pressedBackground = ViewUtils.getRoundedBackground(this, outlineColor, corners);
        } else {
            normalBackground = ViewUtils.getRoundedBackground(this, pressedColor, corners);
            pressedBackground = ViewUtils.getRoundedBackground(this, pressedColor, corners);
        }

        pressedBackground.getPaint().setAlpha(NORMAL_STATE_ALPHA);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, pressedBackground);
        states.addState(new int[]{}, normalBackground);
        return states;
    }

    /**
     * Configures the button with the given connection information.
     *
     * @param config contains the connection information.
     * @param mode   the current button mode. Used to prefix the title with "Log In" or "Sign Up".
     */
    public void setStyle(AuthConfig config, @AuthMode int mode) {
        final Drawable logo = config.getLogo(getContext());
        final int backgroundColor = config.getBackgroundColor(getContext());
        Drawable touchBackground = getTouchFeedbackBackground(backgroundColor, ViewUtils.Corners.ALL);

        // When background is white, change default text color
        boolean shouldUseDarkText = backgroundColor == Color.WHITE;
        if (shouldUseDarkText) {
            int textColor = getResources().getColor(R.color.com_auth0_lock_social_text_light);
            title.setTextColor(textColor);
        }
        icon.setImageDrawable(logo);
        final String name = config.getName(getContext());
        final String prefixFormat = getResources().getString(mode == AuthMode.LOG_IN ? R.string.com_auth0_lock_social_log_in : R.string.com_auth0_lock_social_sign_up);
        title.setText(String.format(prefixFormat, name));
        ViewUtils.setBackground(this, touchBackground);
    }

}
