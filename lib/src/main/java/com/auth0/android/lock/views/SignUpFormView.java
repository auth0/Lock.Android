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
import android.os.Build;
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

import com.auth0.android.lock.R;
import com.auth0.android.lock.UsernameStyle;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.internal.configuration.Configuration;
import com.auth0.android.lock.utils.CustomField;
import com.auth0.android.lock.utils.HiddenField;
import com.auth0.android.lock.views.interfaces.IdentityListener;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

import java.util.List;

import static com.auth0.android.lock.views.CustomFieldsFormView.setEventRootProfileAttributes;

public class SignUpFormView extends FormView implements TextView.OnEditorActionListener, IdentityListener {

    private static final String TAG = SignUpFormView.class.getSimpleName();

    private final LockWidgetForm lockWidget;
    private ValidatedUsernameInputView usernameInput;
    private ValidatedInputView emailInput;
    private ValidatedPasswordInputView passwordInput;
    private LinearLayout fieldContainer;
    private boolean displayCustomFieldsHere;

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
        fieldContainer = findViewById(R.id.com_auth0_lock_custom_fields_container);

        usernameInput = findViewById(R.id.com_auth0_lock_input_username);
        usernameInput.configureFrom(configuration.getDatabaseConnection());
        usernameInput.setUsernameStyle(UsernameStyle.USERNAME);
        usernameInput.setOnEditorActionListener(this);
        emailInput = findViewById(R.id.com_auth0_lock_input_email);
        emailInput.setDataType(ValidatedInputView.DataType.EMAIL);
        emailInput.setIdentityListener(this);
        emailInput.setOnEditorActionListener(this);
        passwordInput = findViewById(R.id.com_auth0_lock_input_password);
        passwordInput.setPasswordComplexity(configuration.getPasswordComplexity());
        passwordInput.setAllowShowPassword(configuration.allowShowPassword());
        passwordInput.setOnEditorActionListener(this);

        usernameInput.setVisibility(configuration.isUsernameRequired() ? View.VISIBLE : View.GONE);

        displayCustomFieldsHere = lockWidget.getConfiguration().getVisibleSignUpFields().size() <= configuration.getVisibleSignUpFieldsThreshold();
        if (displayCustomFieldsHere) {
            addCustomFields(configuration.getVisibleSignUpFields());
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


    private LinearLayout.LayoutParams defineFieldParams() {
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, verticalMargin, 0, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(0);
            params.setMarginEnd(0);
        }
        return params;
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
        for (int i = 0; displayCustomFieldsHere && i < fieldContainer.getChildCount(); i++) {
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
            List<CustomField> visibleFields = lockWidget.getConfiguration().getVisibleSignUpFields();
            if (displayCustomFieldsHere) {
                List<HiddenField> hiddenFields = lockWidget.getConfiguration().getHiddenSignUpFields();
                setEventRootProfileAttributes(event, visibleFields, hiddenFields, fieldContainer);
                return event;
            }
            if (!visibleFields.isEmpty()) {
                lockWidget.showCustomFieldsForm(event);
            }
        }
        return null;
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
    public void onEmailChanged(String email) {
        lockWidget.onEmailChanged(email);
    }

    public void clearEmptyFieldsError() {
        if (usernameInput.getText().isEmpty()) {
            usernameInput.clearInput();
        }
        if (emailInput.getText().isEmpty()) {
            emailInput.clearInput();
        }
    }
}
