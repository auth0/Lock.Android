package com.auth0.android.lock.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.AccessibilityDelegateCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityEventCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v7.widget.AppCompatImageButton;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Checkable;
import android.widget.ImageButton;

import com.auth0.android.lock.R;

/**
 * A custom CheckBox implementation that doesn't have a TextView next to the ImageView, and changes the background drawable when focused.
 */
public class ImageCheckbox extends AppCompatImageButton implements Checkable {

    private static final int[] DRAWABLE_STATE_CHECKED = new int[]{android.R.attr.state_checked};
    private final Drawable focusedBackground;

    private boolean mChecked;
    private OnCheckedChangeListener checkedChangedListener;

    public ImageCheckbox(Context context) {
        this(context, null);
    }

    public ImageCheckbox(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.imageButtonStyle);
    }

    public ImageCheckbox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        ViewCompat.setAccessibilityDelegate(this, new AccessibilityDelegateCompat() {
            @Override
            public void onInitializeAccessibilityEvent(View host, AccessibilityEvent event) {
                super.onInitializeAccessibilityEvent(host, event);
                event.setChecked(isChecked());
            }

            @Override
            public void onInitializeAccessibilityNodeInfo(View host, AccessibilityNodeInfoCompat info) {
                super.onInitializeAccessibilityNodeInfo(host, info);
                info.setCheckable(true);
                info.setChecked(isChecked());
            }
        });
        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggle();
            }
        });
        ViewUtils.setBackground(this, null);
        focusedBackground = ContextCompat.getDrawable(getContext(), R.drawable.com_auth0_lock_link_background);
    }


    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
            sendAccessibilityEvent(AccessibilityEventCompat.TYPE_WINDOW_CONTENT_CHANGED);
            if (checkedChangedListener != null) {
                checkedChangedListener.onCheckedChanged(this, checked);
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }

    @Override
    public int[] onCreateDrawableState(int extraSpace) {
        if (mChecked) {
            return mergeDrawableStates(super.onCreateDrawableState(extraSpace + DRAWABLE_STATE_CHECKED.length), DRAWABLE_STATE_CHECKED);
        } else {
            return super.onCreateDrawableState(extraSpace);
        }
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        ViewUtils.setBackground(this, gainFocus ? focusedBackground : null);
    }

    void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        checkedChangedListener = listener;
    }

    interface OnCheckedChangeListener {
        void onCheckedChanged(ImageButton view, boolean isChecked);
    }

}

