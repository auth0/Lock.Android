/*
 * ModeSelectionView.java
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
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.auth0.android.lock.Configuration;
import com.auth0.android.lock.R;

public class ModeSelectionView extends RelativeLayout implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = ModeSelectionView.class.getSimpleName();
    private final FormModeChangedListener callback;

    public ModeSelectionView(Context context, @NonNull FormModeChangedListener callback) {
        super(context);
        this.callback = callback;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_mode_selection_layout, this);
        RadioGroup modeGroup = (RadioGroup) findViewById(R.id.com_auth0_lock_form_radio_mode_group);
        modeGroup.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.com_auth0_lock_mode_log_in) {
            callback.onFormModeChanged(FormLayout.FormMode.LOG_IN);
        } else if (checkedId == R.id.com_auth0_lock_mode_sign_up) {
            callback.onFormModeChanged(FormLayout.FormMode.SIGN_UP);
        }
    }

    public interface FormModeChangedListener {
        void onFormModeChanged(FormLayout.FormMode mode);
    }
}
