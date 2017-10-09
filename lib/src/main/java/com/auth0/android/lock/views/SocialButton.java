package com.auth0.android.lock.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.StateListDrawable;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
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

    private boolean smallSize;
    private ImageView icon;
    private View focusArea;
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
        focusArea = findViewById(R.id.com_auth0_lock_container);
        setClickable(false);
    }

    private StateListDrawable getTouchFeedbackBackground(@ColorInt int pressedColor, @ViewUtils.Corners int corners) {
        ShapeDrawable normalBackground = ViewUtils.getRoundedBackground(getResources(), pressedColor, corners);
        normalBackground.getPaint().setAlpha(NORMAL_STATE_ALPHA);
        Drawable pressedBackground = ViewUtils.getRoundedBackground(getResources(), pressedColor, corners);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_pressed}, pressedBackground);
        states.addState(new int[]{}, normalBackground);
        return states;
    }

    private StateListDrawable getFocusFeedbackBackground() {
        int focusedColor = ContextCompat.getColor(getContext(), R.color.com_auth0_lock_social_button_focused);
        Drawable outline = ViewUtils.getRoundedOutlineBackground(getResources(), focusedColor);

        StateListDrawable states = new StateListDrawable();
        states.addState(new int[]{android.R.attr.state_focused}, outline);
        states.addState(new int[]{}, new ColorDrawable(Color.TRANSPARENT));
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
        Drawable focusBackground = getFocusFeedbackBackground();

        ViewUtils.setBackground(focusArea, focusBackground);
        icon.setImageDrawable(logo);
        if (smallSize) {
            ViewUtils.setBackground(icon, touchBackground);
        } else {
            final String name = config.getName(getContext());
            ShapeDrawable leftBackground = ViewUtils.getRoundedBackground(getResources(), backgroundColor, ViewUtils.Corners.ONLY_LEFT);
            final String prefixFormat = getResources().getString(mode == AuthMode.LOG_IN ? R.string.com_auth0_lock_social_log_in : R.string.com_auth0_lock_social_sign_up);
            title.setText(String.format(prefixFormat, name));
            ViewUtils.setBackground(icon, leftBackground);
            ViewUtils.setBackground(title, touchBackground);
        }
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        focusArea.setOnClickListener(l);
    }
}
