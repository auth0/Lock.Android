package com.auth0.android.lock.views;

import android.annotation.SuppressLint;
import android.content.Context;
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
    private static final String STRATEGY_GOOGLE_OAUTH2 = "google-oauth2";

    private boolean smallSize;
    private ImageView icon;
    private TextView title;

    public SocialButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SocialButton(Context context, boolean smallSize) {
        super(context);
        this.smallSize = smallSize;
        init();
    }

    private void init() {
        inflate(getContext(), smallSize ? R.layout.com_auth0_lock_btn_social_small : R.layout.com_auth0_lock_btn_social_large, this);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        title = smallSize ? null : (TextView) findViewById(R.id.com_auth0_lock_text);
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
        ShapeDrawable normalBackground = ViewUtils.getRoundedBackground(this, pressedColor, corners);
        normalBackground.getPaint().setAlpha(NORMAL_STATE_ALPHA);
        Drawable pressedBackground = ViewUtils.getRoundedBackground(this, pressedColor, corners);

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
        Drawable touchBackground = getTouchFeedbackBackground(backgroundColor, smallSize ? ViewUtils.Corners.ALL : ViewUtils.Corners.ONLY_RIGHT);

        /*
         * Branding guidelines command we remove the padding for Google logo.
         * Since it's the only exception to the rule, handle it this way.
         *
         * Source: https://developers.google.com/identity/branding-guidelines
         */
        if (STRATEGY_GOOGLE_OAUTH2.equalsIgnoreCase(config.getConnection().getStrategy())) {
            icon.setPadding(0, 0, 0, 0);
        }
        icon.setImageDrawable(logo);
        if (smallSize) {
            ViewUtils.setBackground(icon, touchBackground);
        } else {
            final String name = config.getName(getContext());
            ShapeDrawable leftBackground = ViewUtils.getRoundedBackground(this, backgroundColor, ViewUtils.Corners.ONLY_LEFT);
            final String prefixFormat = getResources().getString(mode == AuthMode.LOG_IN ? R.string.com_auth0_lock_social_log_in : R.string.com_auth0_lock_social_sign_up);
            title.setText(String.format(prefixFormat, name));
            ViewUtils.setBackground(icon, leftBackground);
            ViewUtils.setBackground(title, touchBackground);
        }
    }

}
