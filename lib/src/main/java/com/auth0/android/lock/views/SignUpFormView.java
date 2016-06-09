/*
 * SignUpFormView.java
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
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.CustomField;
import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.views.interfaces.EmailValidationCallback;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SignUpFormView extends FormView implements TextView.OnEditorActionListener, EmailValidationCallback {

    private static final String TAG = SignUpFormView.class.getSimpleName();
    public static final int MAX_FEW_CUSTOM_FIELDS = 2;

    private final LockWidgetForm lockWidget;
    private ValidatedInputView usernameInput;
    private ValidatedInputView emailInput;
    private ValidatedInputView passwordInput;
    private LinearLayout fieldContainer;
    private boolean displayFewCustomFields;

    public SignUpFormView(Context context) {
        super(context);
        this.lockWidget = null;
    }

    public SignUpFormView(LockWidgetForm lockWidget) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        init();
    }

    private void init() {
        Configuration configuration = lockWidget.getConfiguration();
        inflate(getContext(), R.layout.com_auth0_lock_signup_form_view, this);
        fieldContainer = (LinearLayout) findViewById(R.id.com_auth0_lock_container);

        usernameInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.setDataType(ValidatedInputView.DataType.USERNAME);
        usernameInput.setOnEditorActionListener(this);
        emailInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_email);
        emailInput.setDataType(ValidatedInputView.DataType.EMAIL);
        emailInput.setEmailValidationCallback(this);
        emailInput.setOnEditorActionListener(this);
        passwordInput = (ValidatedInputView) findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setDataType(ValidatedInputView.DataType.PASSWORD);
        passwordInput.setOnEditorActionListener(this);

        usernameInput.setVisibility(configuration.isUsernameRequired() ? View.VISIBLE : View.GONE);

        displayFewCustomFields = lockWidget.getConfiguration().getExtraSignUpFields().size() <= MAX_FEW_CUSTOM_FIELDS;
        if (displayFewCustomFields) {
            addCustomFields(configuration.getExtraSignUpFields());
        }
    }

    private void addCustomFields(List<CustomField> customFields) {
        Log.d(TAG, String.format("Adding %d custom fields.", customFields.size()));
        ViewGroup.LayoutParams fieldParams = defineFieldParams();

        for (CustomField data : customFields) {
            ValidatedInputView field = new ValidatedInputView(getContext());
            data.configureField(field);
            field.setLayoutParams(fieldParams);
            field.setOnEditorActionListener(this);
            fieldContainer.addView(field);
        }
    }

    private Map<String, String> getCustomFieldValues() {
        Map<String, String> map = new HashMap<>();
        for (CustomField data : lockWidget.getConfiguration().getExtraSignUpFields()) {
            map.put(data.getKey(), data.findValue(fieldContainer));
        }
        Log.d(TAG, "Custom field values are" + map.values().toString());

        return map;
    }

    private LinearLayout.LayoutParams defineFieldParams() {
        int verticalMargin = (int) getResources().getDimension(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, verticalMargin, 0, 0);
        return params;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
        int usernameHeight = ViewUtils.measureViewHeight(usernameInput);
        int emailHeight = ViewUtils.measureViewHeight(emailInput);
        int passwordHeight = ViewUtils.measureViewHeight(passwordInput);
        int customFields = ViewUtils.measureViewHeight(fieldContainer);
        int sumHeight = usernameHeight + emailHeight + passwordHeight + customFields;

        Log.v(TAG, String.format("Parent height %d, Children height %d (%d + %d + %d)", parentHeight, sumHeight, usernameHeight, emailHeight, passwordHeight));
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        switch (heightMode) {
            case MeasureSpec.UNSPECIFIED:
                setMeasuredDimension(getMeasuredWidth(), sumHeight);
                break;
            case MeasureSpec.AT_MOST:
                setMeasuredDimension(getMeasuredWidth(), Math.min(sumHeight, parentHeight));
                break;
            case MeasureSpec.EXACTLY:
                setMeasuredDimension(getMeasuredWidth(), parentHeight);
                break;
        }
    }

    @Override
    @NonNull
    public Object getActionEvent() {
        Log.d(TAG, String.format("Triggered sign up with email %s and username %s", getEmail(), getUsername()));
        return new DatabaseSignUpEvent(getEmail(), getPassword(), getUsername());
    }

    @Nullable
    private String getUsername() {
        return usernameInput.getVisibility() == VISIBLE ? usernameInput.getText() : null;
    }

    @NonNull
    private String getEmail() {
        return emailInput.getText();
    }

    @NonNull
    private String getPassword() {
        return passwordInput.getText();
    }

    @Override
    public boolean validateForm() {
        boolean valid = true;
        if (usernameInput.getVisibility() == VISIBLE) {
            valid = usernameInput.validate();
        }
        if (emailInput.getVisibility() == VISIBLE) {
            valid = emailInput.validate() && valid;
        }
        if (passwordInput.getVisibility() == VISIBLE) {
            valid = passwordInput.validate() && valid;
        }
        for (int i = 0; displayFewCustomFields && i < fieldContainer.getChildCount(); i++) {
            ValidatedInputView input = (ValidatedInputView) fieldContainer.getChildAt(i);
            valid = input.validate() && valid;
        }
        return valid;
    }

    @Nullable
    @Override
    public Object submitForm() {
        if (validateForm()) {
            DatabaseSignUpEvent event = (DatabaseSignUpEvent) getActionEvent();
            if (displayFewCustomFields) {
                event.setExtraFields(getCustomFieldValues());
                return event;
            }
            if (lockWidget.getConfiguration().hasExtraFields()) {
                lockWidget.showCustomFieldsForm(event);
            }
        }
        return null;
    }

    @Override
    public void onKeyboardStateChanged(boolean isOpen) {
        //Do nothing
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }

    public void setLastEmail(String email) {
        emailInput.setText(email);
        passwordInput.clearInput();
    }

    @Override
    public void onValidOrEmptyEmail(String currentEmail) {
        lockWidget.onEmailChanged(currentEmail);
    }
}
