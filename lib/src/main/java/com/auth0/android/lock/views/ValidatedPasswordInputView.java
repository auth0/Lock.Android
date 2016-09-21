package com.auth0.android.lock.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import com.auth0.android.lock.internal.configuration.PasswordStrength;


public class ValidatedPasswordInputView extends ValidatedInputView {
    private static final String TAG = ValidatedPasswordInputView.class.getSimpleName();
    private PasswordStrengthView strengthView;

    public ValidatedPasswordInputView(Context context) {
        super(context);
        init();
    }

    public ValidatedPasswordInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ValidatedPasswordInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        strengthView = new PasswordStrengthView(getContext());
        rootView.addView(strengthView, 0);  //Add it above the field
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int strengthHeight = ViewUtils.measureViewHeight(strengthView);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredHeight() + strengthHeight);
    }

    @Override
    protected boolean validate(boolean validateEmptyFields) {
        String value = getText();
        //Run strength validation to update ui
        final boolean valid = strengthView.isValid(value);
        if (!validateEmptyFields && value.isEmpty()) {
            return true;
        }

        Log.v(TAG, "Field validation results: Is valid? " + valid);
        return valid;
    }

    @Override
    protected void updateBorder(boolean isValid) {
        super.updateBorder(isValid);
        if (strengthView != null && strengthView.isEnabled()) {
            strengthView.setVisibility(!isValid || !getText().isEmpty() ? VISIBLE : GONE);
        }
    }

    /**
     * Sets the Password Strength Policy level for this view.
     *
     * @param strength the new Policy level.
     */
    public void setPasswordPolicy(@PasswordStrength int strength) {
        strengthView.setStrength(strength);
    }
}
