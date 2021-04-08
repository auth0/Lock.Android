package com.auth0.android.lock.views;

import android.content.Context;
import androidx.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;

import com.auth0.android.lock.internal.configuration.PasswordComplexity;
import com.auth0.android.lock.internal.configuration.PasswordStrength;


public class ValidatedPasswordInputView extends ValidatedInputView {
    private static final String TAG = ValidatedPasswordInputView.class.getSimpleName();
    private PasswordStrengthView strengthView;
    private boolean hasValidInput = true;

    public ValidatedPasswordInputView(@NonNull Context context) {
        super(context);
        init();
    }

    public ValidatedPasswordInputView(@NonNull Context context, @NonNull AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public ValidatedPasswordInputView(@NonNull Context context, @NonNull AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        strengthView = new PasswordStrengthView(getContext());
        addView(strengthView, 0);  //Add it above the field
    }

    @Override
    protected boolean validate(boolean validateEmptyFields) {
        String value = getText();
        //Run strength validation to update ui
        hasValidInput = strengthView.isValid(value) || !validateEmptyFields && value.isEmpty();
        Log.v(TAG, "Field validation results: Is valid? " + hasValidInput);
        return hasValidInput;
    }

    @Override
    protected void updateBorder() {
        super.updateBorder();
        if (strengthView != null && strengthView.isEnabled()) {
            strengthView.setVisibility(!hasValidInput || !getText().isEmpty() ? VISIBLE : GONE);
        }
    }

    /**
     * Sets the Password Strength Policy level for this view.
     *
     * @param strength the new Policy level.
     * @deprecated use {@link #setPasswordComplexity(PasswordComplexity)} method
     */
    @Deprecated
    public void setPasswordPolicy(@PasswordStrength int strength) {
        //noinspection deprecation
        strengthView.setStrength(strength);
    }

    /**
     * Sets the Password Complexity for this view.
     *
     * @param complexity the new Password complexity to use
     */
    public void setPasswordComplexity(@NonNull PasswordComplexity complexity) {
        strengthView.setPasswordComplexity(complexity);
    }
}
