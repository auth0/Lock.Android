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
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.views.ValidatedInputView.DataType.DATE;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.EMAIL;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.MOBILE_PHONE;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.NUMBER;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.PASSWORD;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.PHONE_NUMBER;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.USERNAME;
import static com.auth0.android.lock.views.ValidatedInputView.DataType.USERNAME_OR_EMAIL;

public class ValidatedInputView extends LinearLayout implements View.OnFocusChangeListener {

    private static final String TAG = ValidatedInputView.class.getSimpleName();
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_USERNAME_LENGTH = 6;
    private static final int MIN_PHONE_NUMBER_LENGTH = 10;

    private EditText input;
    private ImageView icon;
    private int inputIcon;

    @IntDef({USERNAME, EMAIL, USERNAME_OR_EMAIL, NUMBER, PHONE_NUMBER, PASSWORD, MOBILE_PHONE, DATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DataType {
        int USERNAME = 0;
        int EMAIL = 1;
        int USERNAME_OR_EMAIL = 2;
        int NUMBER = 3;
        int PHONE_NUMBER = 4;
        int PASSWORD = 5;
        int MOBILE_PHONE = 6;
        int DATE = 7;
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
        icon = (ImageView) findViewById(R.id.com_auth0_lock_icon);
        input = (EditText) findViewById(R.id.com_auth0_lock_input);

        Drawable leftBackground = ViewUtils.getRoundedBackground(getResources(), ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_input_field_border_normal), ViewUtils.Corners.ONLY_LEFT);
        Drawable rightBackground = ViewUtils.getRoundedBackground(getResources(), ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_input_field_background), ViewUtils.Corners.ONLY_RIGHT);
        ViewUtils.setBackground(icon, leftBackground);
        ViewUtils.setBackground(input, rightBackground);

        if (attrs == null || isInEditMode()) {
            return;
        }

        input.setOnFocusChangeListener(this);
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Lock_ValidatedInput);
        //noinspection WrongConstant
        dataType = a.getInt(R.styleable.Lock_ValidatedInput_Auth0_InputDataType, 0);
        a.recycle();

        setupInputValidation();
        updateBorder(false);
    }

    private void setupInputValidation() {
        String hint = "";
        input.setTransformationMethod(null);
        Log.v(TAG, "Setting up validation for field of type " + dataType);
        switch (dataType) {
            case EMAIL:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputIcon = R.drawable.com_auth0_lock_ic_email;
                hint = getResources().getString(R.string.com_auth0_lock_hint_email);
                break;
            case PASSWORD:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                input.setTransformationMethod(PasswordTransformationMethod.getInstance());
                input.setTypeface(Typeface.DEFAULT);
                inputIcon = R.drawable.com_auth0_lock_ic_password;
                hint = getResources().getString(R.string.com_auth0_lock_hint_password);
                break;
            case USERNAME_OR_EMAIL:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                inputIcon = R.drawable.com_auth0_lock_ic_username;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username_or_email);
                break;
            case USERNAME:
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                inputIcon = R.drawable.com_auth0_lock_ic_username;
                hint = getResources().getString(R.string.com_auth0_lock_hint_username);
                break;
            case NUMBER:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputIcon = R.drawable.com_auth0_lock_ic_password;
                hint = getResources().getString(R.string.com_auth0_lock_hint_code);
                break;
            case MOBILE_PHONE:
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                inputIcon = R.drawable.com_auth0_lock_ic_mobile;
                hint = getResources().getString(R.string.com_auth0_lock_hint_phone_number);
                break;
            case PHONE_NUMBER:
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                inputIcon = R.drawable.com_auth0_lock_ic_phone;
                hint = getResources().getString(R.string.com_auth0_lock_hint_phone_number);
                break;
            case DATE:
                input.setInputType(InputType.TYPE_CLASS_DATETIME | InputType.TYPE_DATETIME_VARIATION_DATE);
                inputIcon = R.drawable.com_auth0_lock_ic_clock;
                break;
        }
        input.setHint(hint);
        icon.setImageResource(inputIcon);
    }

    private void updateBorder(boolean showError) {
        ViewGroup parent = ((ViewGroup) input.getParent());
        Drawable bg = parent.getBackground();
        GradientDrawable gd = bg == null ? new GradientDrawable() : (GradientDrawable) bg;
        gd.setCornerRadius(getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_corner_radius));
        int strokeColor = showError ? R.color.com_auth0_lock_input_field_border_error : R.color.com_auth0_lock_input_field_border_normal;
        gd.setStroke((int) getResources().getDimension(R.dimen.com_auth0_lock_input_field_stroke_width), ViewUtils.obtainColor(getContext(), strokeColor));
        gd.setColor(ViewUtils.obtainColor(getContext(), R.color.com_auth0_lock_input_field_border_normal));
        ViewUtils.setBackground(parent, gd);
    }

    /**
     * Changes the type of input this view will validate.
     *
     * @param type a valid DataType
     */
    public void setDataType(@DataType int type) {
        dataType = type;
        updateBorder(false);
        setupInputValidation();
    }

    /**
     * Validates the input data and updates the icon. DataType must be set.
     *
     * @return whether the data is valid or not.
     */
    public boolean validate(boolean validateEmptyFields) {
        //also called on EditText focus change
        String value = getText();
        boolean isValid = false;
        if (!validateEmptyFields && value.isEmpty()) {
            updateBorder(false);
            return true;
        }

        switch (dataType) {
            case EMAIL:
                isValid = !value.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(value).matches();
                break;
            case PASSWORD:
                isValid = !value.isEmpty() && value.length() >= MIN_PASSWORD_LENGTH;
                break;
            case USERNAME:
                String withoutSpaces = value.replace(" ", "");
                isValid = !withoutSpaces.isEmpty() && withoutSpaces.length() >= MIN_USERNAME_LENGTH;
                break;
            case USERNAME_OR_EMAIL:
                isValid = !value.isEmpty() && (Patterns.EMAIL_ADDRESS.matcher(value).matches() || value.length() >= MIN_USERNAME_LENGTH);
                break;
            case MOBILE_PHONE:
            case PHONE_NUMBER:
                value = value.replace(" ", "");
                isValid = !value.isEmpty() && value.length() >= MIN_PHONE_NUMBER_LENGTH;
                break;
            case NUMBER:
                isValid = !value.isEmpty();
                break;
        }

        updateBorder(!isValid);
        Log.v(TAG, "Field validation results: Is valid? " + isValid);
        return isValid;
    }

    /**
     * Gets the current text from the input field, without spaces at the end.
     *
     * @return the current text
     */
    public String getText() {
        return input.getText().toString().trim();
    }

    /**
     * Updates the input field text.
     *
     * @param text the new text to set.
     */
    public void setText(String text) {
        input.setText("");
        input.append(text);
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
        updateBorder(false);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            Log.v(TAG, "Field validation running because of focus change");
            validate(false);
        }
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
}
