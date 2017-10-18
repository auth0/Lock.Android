/*
 * EmailAndPasswordView.java
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
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Handler;
import android.support.annotation.CallSuper;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.views.interfaces.IdentityListener;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.internal.configuration.DatabaseConnection.MAX_USERNAME_LENGTH;
import static com.auth0.android.lock.internal.configuration.DatabaseConnection.MIN_USERNAME_LENGTH;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.EMAIL;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.MFA_CODE;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.MOBILE_PHONE;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.NON_EMPTY_USERNAME;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.NUMBER;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.PASSWORD;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.PHONE_NUMBER;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.TEXT_NAME;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.USERNAME;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.USERNAME_OR_EMAIL;

public class ValidatedInputView extends LinearLayout {

    public static final String USERNAME_REGEX = "^[a-zA-Z0-9_]+$";
    public static final String PHONE_NUMBER_REGEX = "^[0-9]{6,14}$";
    public static final String CODE_REGEX = "^[0-9]{4,12}$";
    public static final String EMAIL_REGEX = Patterns.EMAIL_ADDRESS.pattern();
    private static final String TAG = ValidatedInputView.class.getSimpleName();
    private static final int VALIDATION_DELAY = 500;

    private TextView errorDescription;
    private EditText input;
    private ImageView icon;
    private ImageCheckbox showPasswordToggle;
    private IdentityListener identityListener;
    private int inputIcon;
    private boolean hasValidInput = true;
    private boolean allowShowPassword = true;
    private View outline;
    private ShapeDrawable focusedOutlineBackground;
    private ShapeDrawable normalOutlineBackground;
    private ShapeDrawable errorOutlineBackground;

    @IntDef({USERNAME, EMAIL, USERNAME_OR_EMAIL, MFA_CODE, PHONE_NUMBER, PASSWORD, MOBILE_PHONE, TEXT_NAME, NUMBER, NON_EMPTY_USERNAME})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataType {
        int USERNAME = 0;
        int EMAIL = 1;
        int USERNAME_OR_EMAIL = 2;
        int MFA_CODE = 3;
        int PHONE_NUMBER = 4;
        int PASSWORD = 5;
        int MOBILE_PHONE = 6;
        int TEXT_NAME = 7;
        int NUMBER = 8;
        int NON_EMPTY_USERNAME = 9;
    }

    @DataType
    private int dataType;

    public ValidatedInputView(Context context) {
        super(context);
        init(null);
    }

    public ValidatedInputView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ValidatedInputView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        inflate(getContext(), R.layout.com_auth0_lock_validated_input_view, this);

        outline = findViewById(R.id.com_auth0_lock_outline);
        errorDescription = (TextView) findViewById(R.id.errorDescription);
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        input = (EditText) findViewById(R.id.com_auth0_lock_input);
        showPasswordToggle = (ImageCheckbox) findViewById(R.id.com_auth0_lock_show_password_toggle);
        setOrientation(VERTICAL);

        if (attrs == null || isInEditMode()) {
            return;
        }

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock_ValidatedInput);
        //noinspection WrongConstant
        dataType = a.getInt(R.styleable.Lock_ValidatedInput_Auth0_InputDataType, 0);
        a.recycle();
        createBackground();

        setupInputValidation();
        focusedOutlineBackground = ViewUtils.getRoundedOutlineBackground(getResources(), ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_field_border_focused));
        normalOutlineBackground = ViewUtils.getRoundedOutlineBackground(getResources(), ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_field_border_normal));
        errorOutlineBackground = ViewUtils.getRoundedOutlineBackground(getResources(), ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_field_border_error));
        updateBorder();

        setNextFocusRightId(R.id.com_auth0_lock_show_password_toggle);
        showPasswordToggle.setOnCheckedChangeListener(new ImageCheckbox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ImageButton view, boolean isChecked) {
                if (dataType != PASSWORD) {
                    return;
                }
                String passwordValue = input.getText().toString();
                if (isChecked) {
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    view.setImageResource(R.drawable.com_auth0_lock_ic_password_visible);
                } else {
                    input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    view.setImageResource(R.drawable.com_auth0_lock_ic_password_hidden);
                }
                setText(passwordValue);
                input.setTypeface(Typeface.DEFAULT);
            }
        });
        input.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                updateBorder();
            }
        });
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        input.addTextChangedListener(inputWatcher);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        input.removeTextChangedListener(inputWatcher);
    }

    private TextWatcher inputWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            runValidation();
            if (dataType == EMAIL || dataType == USERNAME_OR_EMAIL) {
                notifyEmailChanged(s.toString());
            }
        }

        private void runValidation() {
            hasValidInput = validate(false);
            Handler handler = getHandler();
            handler.removeCallbacks(uiUpdater);
            handler.postDelayed(uiUpdater, VALIDATION_DELAY);
        }

        private void notifyEmailChanged(String emailInput) {
            boolean validOrEmptyEmail = emailInput.isEmpty() || emailInput.matches(EMAIL_REGEX);
            if (identityListener != null && validOrEmptyEmail) {
                identityListener.onEmailChanged(emailInput);
            }
        }
    };

    private Runnable uiUpdater = new Runnable() {
        @Override
        public void run() {
            updateBorder();
        }
    };

    private void setupInputValidation() {
        String hint = "";
        String error = "";
        input.setTransformationMethod(null);
        Log.v(TAG, "Setting up validation for field of type " + dataType);
        switch (dataType) {
            case EMAIL:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputIcon = R.drawable.com_auth0_lock_ic_email;
                hint = getResources().getString(R.string.com_auth0_lock_hint_email);
                error = getResources().getString(R.string.com_auth0_lock_input_error_email);
                break;
            case PASSWORD:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                input.setTypeface(Typeface.DEFAULT);
                inputIcon = R.drawable.com_auth0_lock_ic_password;
                hint = getResources().getString(R.string.com_auth0_lock_hint_password);
                error = getResources().getString(R.string.com_auth0_lock_input_error_password);
                break;
            case USERNAME_OR_EMAIL:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputIcon = R.drawable.com_auth0_lock_ic_username;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username_or_email);
                error = getResources().getString(R.string.com_auth0_lock_input_error_username_email);
                break;
            case TEXT_NAME:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
                inputIcon = R.drawable.com_auth0_lock_ic_username;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username);
                error = getResources().getString(R.string.com_auth0_lock_input_error_empty);
                break;
            case NON_EMPTY_USERNAME:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                inputIcon = R.drawable.com_auth0_lock_ic_username;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username);
                error = getResources().getString(R.string.com_auth0_lock_input_error_username_empty);
                break;
            case USERNAME:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                inputIcon = R.drawable.com_auth0_lock_ic_username;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username);
                error = String.format(getResources().getString(R.string.com_auth0_lock_input_error_username), MIN_USERNAME_LENGTH, MAX_USERNAME_LENGTH);
                break;
            case NUMBER:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputIcon = R.drawable.com_auth0_lock_ic_password;
                hint = getResources().getString(R.string.com_auth0_lock_hint_code);
                error = getResources().getString(R.string.com_auth0_lock_input_error_empty);
                break;
            case MFA_CODE:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputIcon = R.drawable.com_auth0_lock_ic_password;
                hint = getResources().getString(R.string.com_auth0_lock_hint_code);
                error = getResources().getString(R.string.com_auth0_lock_input_error_code);
                break;
            case MOBILE_PHONE:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputIcon = R.drawable.com_auth0_lock_ic_mobile;
                hint = getResources().getString(R.string.com_auth0_lock_hint_phone_number);
                error = getResources().getString(R.string.com_auth0_lock_input_error_phone_number);
                break;
            case PHONE_NUMBER:
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                inputIcon = R.drawable.com_auth0_lock_ic_phone;
                hint = getResources().getString(R.string.com_auth0_lock_hint_phone_number);
                error = getResources().getString(R.string.com_auth0_lock_input_error_phone_number);
                break;
        }
        showPasswordToggle.setVisibility(dataType == PASSWORD && allowShowPassword ? ViewGroup.VISIBLE : ViewGroup.GONE);
        showPasswordToggle.setChecked(false);
        input.setHint(hint);
        errorDescription.setText(error);
        icon.setImageResource(inputIcon);
    }

    /**
     * Updates the view knowing if the input is valid or not.
     */
    @CallSuper
    protected void updateBorder() {
        boolean isFocused = input.hasFocus() && !input.isInTouchMode();
        ViewUtils.setBackground(outline, hasValidInput ? (isFocused ? focusedOutlineBackground : normalOutlineBackground) : errorOutlineBackground);
        errorDescription.setVisibility(hasValidInput ? GONE : VISIBLE);
        requestLayout();
    }

    private void createBackground() {
        int inputBackgroundColor = ContextCompat.getColor(getContext(), isEnabled() ? R.color.com_auth0_lock_input_field_background : R.color.com_auth0_lock_input_field_background_disabled);
        Drawable leftBackground = ViewUtils.getRoundedBackground(this, ContextCompat.getColor(getContext(), R.color.com_auth0_lock_input_field_border_normal), ViewUtils.Corners.ONLY_LEFT);
        Drawable rightBackground = ViewUtils.getRoundedBackground(this, inputBackgroundColor, ViewUtils.Corners.ONLY_RIGHT);
        ViewUtils.setBackground(icon, leftBackground);
        ViewUtils.setBackground((ViewGroup) input.getParent(), rightBackground);
    }

    /**
     * Changes the type of input this view will validate.
     *
     * @param type a valid DataType
     */
    public void setDataType(@DataType int type) {
        dataType = type;
        hasValidInput = true;
        updateBorder();
        setupInputValidation();
    }

    /**
     * Getter for the DataType associated to this field.
     *
     * @return the DataType associated to this view.
     */
    @DataType
    protected int getDataType() {
        return dataType;
    }

    /**
     * Validates the input data and updates the border. DataType must be set.
     * Empty fields are considered valid.
     *
     * @return whether the data is valid or not.
     */
    public boolean validate() {
        hasValidInput = validate(true);
        updateBorder();
        return hasValidInput;
    }

    /**
     * Validates the input data and updates the icon. DataType must be set.
     *
     * @param validateEmptyFields if an empty input should be considered invalid.
     * @return whether the data is valid or not.
     */
    protected boolean validate(boolean validateEmptyFields) {
        boolean isValid = false;
        String value = dataType == PASSWORD ? getText() : getText().trim();
        if (!validateEmptyFields && value.isEmpty()) {
            return true;
        }

        switch (dataType) {
            case TEXT_NAME:
            case NUMBER:
            case PASSWORD:
            case NON_EMPTY_USERNAME:
                isValid = !value.isEmpty();
                break;
            case EMAIL:
                isValid = value.matches(EMAIL_REGEX);
                break;
            case USERNAME:
                isValid = value.matches(USERNAME_REGEX) && value.length() >= 1 && value.length() <= 15;
                break;
            case USERNAME_OR_EMAIL:
                final boolean validEmail = value.matches(EMAIL_REGEX);
                final boolean validUsername = value.matches(USERNAME_REGEX) && value.length() >= 1 && value.length() <= 15;
                isValid = validEmail || validUsername;
                break;
            case MOBILE_PHONE:
            case PHONE_NUMBER:
                isValid = value.matches(PHONE_NUMBER_REGEX);
                break;
            case MFA_CODE:
                isValid = value.matches(CODE_REGEX);
                break;
        }

        Log.v(TAG, "Field validation results: Is valid? " + isValid);
        return isValid;
    }

    /**
     * Gets the current text from the input field.
     *
     * @return the current text
     */
    public String getText() {
        return input.getText().toString();
    }

    /**
     * Updates the input field text.
     *
     * @param text the new text to set.
     */
    public void setText(String text) {
        input.setText("");
        if (text != null) {
            input.append(text);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        input.setEnabled(enabled);
        showPasswordToggle.setEnabled(enabled);
        createBackground();
    }

    @Override
    public boolean isEnabled() {
        return input.isEnabled();
    }

    /**
     * Whether to display the Show Password toggle or not.
     * Only available for Password data type. Defaults to true.
     *
     * @param allow whether to display a button to toggle between showing or hiding the password or not.
     */
    public void setAllowShowPassword(boolean allow) {
        this.allowShowPassword = allow;
        setupInputValidation();
    }

    /**
     * Updates the input field hint.
     *
     * @param hint the new hint to set.
     */
    public void setHint(@StringRes int hint) {
        input.setHint(hint);
    }

    /**
     * Updates the validation error description.
     *
     * @param error the new error description to set.
     */
    public void setErrorDescription(String error) {
        errorDescription.setText(error);
    }

    /**
     * Updates the input Icon.
     *
     * @param icon the new icon to set.
     */
    public void setIcon(@DrawableRes int icon) {
        this.icon.setImageResource(icon);
    }

    /**
     * Removes any text present on the input field and clears any validation error, if present.
     */
    public void clearInput() {
        Log.v(TAG, "Input cleared and validation errors removed");
        input.setText("");
        hasValidInput = true;
        updateBorder();
        showPasswordToggle.setChecked(false);
    }

    /**
     * Adds the given TextWatcher to this view EditText.
     *
     * @param watcher to add to the EditText.
     */
    public void addTextChangedListener(TextWatcher watcher) {
        input.addTextChangedListener(watcher);
    }

    /**
     * Sets the given OnEditorActionListener to this view EditText.
     *
     * @param listener to set to the EditText.
     */
    public void setOnEditorActionListener(TextView.OnEditorActionListener listener) {
        input.setOnEditorActionListener(listener);
    }

    /**
     * Sets the given IdentityListener to this view EditText.
     *
     * @param listener to set to this view.
     */
    public void setIdentityListener(IdentityListener listener) {
        this.identityListener = listener;
    }
}
