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
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;

import com.auth0.android.lock.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.auth0.android.lock.views.ModeSelectionView.Mode.LOG_IN;
import static com.auth0.android.lock.views.ModeSelectionView.Mode.SIGN_UP;

public class ModeSelectionView extends RelativeLayout implements RadioGroup.OnCheckedChangeListener {

    private final ModeSelectedListener callback;
    private RadioGroup modeGroup;

    @IntDef({LOG_IN, SIGN_UP})
    @Retention(RetentionPolicy.SOURCE)
    public @interface Mode {
        int LOG_IN = 0;
        int SIGN_UP = 1;
    }

    public ModeSelectionView(Context context, @NonNull ModeSelectedListener listener) {
        super(context);
        this.callback = listener;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.com_auth0_lock_mode_selection_layout, this);
        modeGroup = (RadioGroup) findViewById(R.id.com_auth0_lock_form_radio_mode_group);
        modeGroup.setOnCheckedChangeListener(this);
    }

    /**
     * Manually change the selected mode.
     *
     * @param mode the new DatabaseForm mode
     */
    public void changeMode(FormLayout.DatabaseForm mode) {
        switch (mode) {
            case LOG_IN:
                modeGroup.check(R.id.com_auth0_lock_mode_log_in);
                break;
            case SIGN_UP:
                modeGroup.check(R.id.com_auth0_lock_mode_sign_up);
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (checkedId == R.id.com_auth0_lock_mode_log_in) {
            callback.onModeSelected(LOG_IN);
        } else if (checkedId == R.id.com_auth0_lock_mode_sign_up) {
            callback.onModeSelected(SIGN_UP);
        }
    }

    public interface ModeSelectedListener {
        void onModeSelected(@Mode int mode);
    }
}
