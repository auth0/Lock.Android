package com.auth0.android.lock.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

/**
 * Created by lbalmaceda on 1/22/16.
 */
class SocialButton extends RelativeLayout {

    private static final int BTN_CORNER_RADIUS = 5;
    private boolean configured;

    @ColorInt
    private int backgroundColor = 0;
    @ColorInt
    private int textColor = 0;
    @StringRes
    private int titleRes;
    @DrawableRes
    private int iconRes;

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

        setClickable(true);
        setFocusable(true);

        int[] attrs = new int[]{R.attr.selectableItemBackground};
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs);
        int backgroundResource = typedArray.getResourceId(0, 0);
        setBackgroundResource(backgroundResource);
        typedArray.recycle();

        ImageView icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        TextView title = (TextView) findViewById(R.id.com_auth0_lock_text);
        if (!configured) {
            return;
        }

        icon.setImageResource(iconRes);
        title.setTextColor(textColor);
        title.setText(titleRes);

        Drawable leftBackground = getRoundedBackground(backgroundColor, true);
        Drawable rightBackground = getRoundedBackground(backgroundColor, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            icon.setBackground(leftBackground);
            title.setBackground(rightBackground);
        } else {
            icon.setBackgroundDrawable(leftBackground);
            title.setBackgroundDrawable(rightBackground);
        }
    }

    private ShapeDrawable getRoundedBackground(@ColorInt int color, boolean leftBorder) {
        float r = ViewUtils.dipToPixels(getResources(), BTN_CORNER_RADIUS);
        float[] outerR = leftBorder ? new float[]{r, r, 0, 0, 0, 0, r, r} : new float[]{0, 0, r, r, r, r, 0, 0};

        RoundRectShape rr = new RoundRectShape(outerR, null, null);
        ShapeDrawable drawable = new ShapeDrawable(rr);
        drawable.getPaint().setColor(color);
        return drawable;
    }

    public void setSocialConfig(SocialButtonConfig config) {
        this.configured = true;
        this.titleRes = config.getTitle();
        this.iconRes = config.getIcon();
        this.backgroundColor = config.getBackgroundColor();
        this.textColor = config.getTextColor();
        init();
    }
}
