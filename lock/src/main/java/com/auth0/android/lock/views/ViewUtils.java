/*
 * ViewUtils.java
 *
 * Copyright (c) 2016 Auth0 (http://auth0.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package com.auth0.android.lock.views;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RoundRectShape;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.ColorRes;
import android.support.annotation.IntDef;
import android.util.TypedValue;
import android.view.View;

import com.auth0.android.lock.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.views.ViewUtils.Corners.ALL;
import static com.auth0.android.lock.views.ViewUtils.Corners.ONLY_LEFT;
import static com.auth0.android.lock.views.ViewUtils.Corners.ONLY_RIGHT;

abstract class ViewUtils {

    /**
     * Enum used by the getRoundedBackground method. It defines which corners to set as rounded
     * in the drawable.
     */
    @IntDef({ALL, ONLY_LEFT, ONLY_RIGHT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Corners {
        int ALL = 0;
        int ONLY_LEFT = 1;
        int ONLY_RIGHT = 2;
    }

    /**
     * Converts dp into px.
     *
     * @param resources the context's current resources.
     * @param dip       the dp value to convert to px.
     * @return the result px value.
     */
    static float dipToPixels(Resources resources, int dip) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, resources.getDisplayMetrics());
    }

    /**
     * Gets a color from the given context, safely using the latest available sdk method.
     *
     * @param context  a valid context
     * @param colorRes the color resource to get
     * @return the color int value
     */
    @ColorInt
    static int obtainColor(Context context, @ColorRes int colorRes) {
        int color;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            color = context.getResources().getColor(colorRes, context.getTheme());
        } else {
            //noinspection deprecation
            color = context.getResources().getColor(colorRes);
        }
        return color;
    }

    /**
     * Generates a rounded drawable with the given background color and the specified corners.
     *
     * @param resources the context's current resources.
     * @param color     the color to use as background.
     * @param corners   the rounded corners this drawable will have. Can be one of ONLY_LEFT, ONLY_RIGHT, ALL
     * @return the rounded drawable.
     */
    static ShapeDrawable getRoundedBackground(Resources resources, @ColorInt int color, @Corners int corners) {
        int r = resources.getDimensionPixelSize(R.dimen.com_auth0_lock_widget_corner_radius);
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
        return drawable;
    }

    /**
     * Sets a background drawable to a view, safely using the latest available sdk method.
     *
     * @param view       the view to set the background drawable to.
     * @param background the drawable to use as background.
     */
    static void setBackground(View view, Drawable background) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(background);
        } else {
            //noinspection deprecation
            view.setBackgroundDrawable(background);
        }
    }
}
