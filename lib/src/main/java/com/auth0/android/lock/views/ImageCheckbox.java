package com.auth0.android.lock.views;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.view.AccessibilityDelegateCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.accessibility.AccessibilityEventCompat;
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat;
import androidx.appcompat.widget.AppCompatImageButton;
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

    public ImageCheckbox(@NonNull Context context) {
        this(context, null);
    }

    public ImageCheckbox(@NonNull Context context, @NonNull AttributeSet attrs) {
        this(context, attrs, R.attr.imageButtonStyle);
    }

    public ImageCheckbox(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
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
        setOnClickListener(v -> toggle());
        ViewUtils.setBackground(this, null);
        focusedBackground = ContextCompat.getDrawable(getContext(), R.drawable.com_auth0_lock_link_background);
    }


    @Override
    public void setChecked(boolean checked) {
        if (mChecked != checked) {
            mChecked = checked;
            refreshDrawableState();
            //noinspection deprecation
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

    @NonNull
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

