package com.auth0.android.lock.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by lbalmaceda on 1/26/16.
 */
public class LockProgress extends RelativeLayout {

    private static final String TAG = LockProgress.class.getSimpleName();
    private ProgressBar progress;
    private TextView message;

    public LockProgress(Context context) {
        super(context);
        init();
    }

    public LockProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LockProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        Log.i(TAG, "Init view");
        progress = new ProgressBar(getContext());
        message = new TextView(getContext());
        progress.setIndeterminate(true);
        progress.setVisibility(View.VISIBLE);
        message.setVisibility(View.GONE);
        message.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        addView(progress, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        LayoutParams messageParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        messageParams.addRule(BELOW, progress.getId());
        addView(message, messageParams);
        if (isInEditMode()) {
            return;
        }

    }

    public void showResult(String error) {
        Log.i(TAG, "Showing result: " + error);
        if (error == null || error.isEmpty()) {
            message.setText("");
            message.setVisibility(View.GONE);
        } else {
            message.setText(error);
            message.setVisibility(View.VISIBLE);
        }
        progress.setVisibility(View.GONE);
    }
}
