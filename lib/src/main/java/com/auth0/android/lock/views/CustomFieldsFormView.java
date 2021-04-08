/*
 * CustomFieldsFormView.java
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

import android.annotation.SuppressLint;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.events.DatabaseSignUpEvent;
import com.auth0.android.lock.utils.CustomField;
import com.auth0.android.lock.utils.HiddenField;
import com.auth0.android.lock.views.interfaces.LockWidgetForm;

import java.util.HashMap;
import java.util.List;

import static com.auth0.android.lock.utils.CustomField.Storage;

@SuppressLint("ViewConstructor")
public class CustomFieldsFormView extends FormView implements TextView.OnEditorActionListener {

    private static final String TAG = CustomFieldsFormView.class.getSimpleName();

    @NonNull
    private final String email;
    @NonNull
    private final String password;
    @Nullable
    private final String username;
    private final LockWidgetForm lockWidget;
    private final List<CustomField> visibleSignUpFields;
    private final List<HiddenField> hiddenSignUpFields;
    private LinearLayout fieldContainer;
    private LinearLayout.LayoutParams fieldParams;

    public CustomFieldsFormView(@NonNull LockWidgetForm lockWidget, @NonNull String email, @NonNull String password, @Nullable String username) {
        super(lockWidget.getContext());
        this.lockWidget = lockWidget;
        this.email = email;
        this.username = username;
        this.password = password;
        this.visibleSignUpFields = lockWidget.getConfiguration().getVisibleSignUpFields();
        this.hiddenSignUpFields = lockWidget.getConfiguration().getHiddenSignUpFields();
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_custom_fields_form_view, this);
        fieldContainer = findViewById(R.id.com_auth0_lock_container);
        fieldParams = defineFieldParams();
        addCustomFields();
    }

    private LinearLayout.LayoutParams defineFieldParams() {
        int horizontalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_horizontal_margin);
        int verticalMargin = getResources().getDimensionPixelSize(R.dimen.com_auth0_lock_widget_vertical_margin_field);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(horizontalMargin, verticalMargin / 2, horizontalMargin, verticalMargin / 2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            params.setMarginStart(horizontalMargin);
            params.setMarginEnd(horizontalMargin);
        }
        return params;
    }

    private void addCustomFields() {
        Log.d(TAG, String.format("Adding %d custom fields.", visibleSignUpFields.size()));

        for (CustomField data : visibleSignUpFields) {
            ValidatedInputView field = new ValidatedInputView(getContext());
            data.configureField(field);
            field.setLayoutParams(fieldParams);
            field.setOnEditorActionListener(this);
            fieldContainer.addView(field);
        }
    }

    static void setEventRootProfileAttributes(DatabaseSignUpEvent event, List<CustomField> visibleFields, List<HiddenField> hiddenFields, ViewGroup container) {
        HashMap<String, Object> rootMap = new HashMap<>();
        HashMap<String, String> userMetadataMap = new HashMap<>();

        for (CustomField data : visibleFields) {
            String value = data.findValue(container);
            if (data.getStorage() == Storage.USER_METADATA) {
                userMetadataMap.put(data.getKey(), value);
            } else {
                rootMap.put(data.getKey(), value);
            }
        }
        for (HiddenField hf : hiddenFields) {
            if (hf.getStorage() == Storage.USER_METADATA) {
                userMetadataMap.put(hf.getKey(), hf.getValue());
            } else {
                rootMap.put(hf.getKey(), hf.getValue());
            }
        }
        if (!rootMap.isEmpty()) {
            event.setRootAttributes(rootMap);
        }
        if (!userMetadataMap.isEmpty()) {
            event.setExtraFields(userMetadataMap);
        }
    }

    @NonNull
    @Override
    public Object getActionEvent() {
        DatabaseSignUpEvent event = new DatabaseSignUpEvent(email, password, username);
        setEventRootProfileAttributes(event, visibleSignUpFields, hiddenSignUpFields, fieldContainer);
        return event;
    }

    @Override
    public boolean validateForm() {
        boolean valid = true;
        for (int i = 0; i < fieldContainer.getChildCount(); i++) {
            ValidatedInputView input = (ValidatedInputView) fieldContainer.getChildAt(i);
            if (input.isEnabled()) {
                valid = input.validate() && valid;
            }
        }
        Log.d(TAG, "Is form data valid? " + valid);
        return valid;
    }

    @Nullable
    @Override
    public Object submitForm() {
        return validateForm() ? getActionEvent() : null;
    }

    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId, @NonNull KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            lockWidget.onFormSubmit();
        }
        return false;
    }
}
