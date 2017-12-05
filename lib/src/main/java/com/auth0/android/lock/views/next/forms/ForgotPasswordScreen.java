package com.auth0.android.lock.views.next.forms;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.auth0.android.lock.R;
import com.auth0.android.lock.ServiceLocator;
import com.auth0.android.lock.views.next.configuration.internal.IdentityStyle;
import com.auth0.android.lock.views.next.events.AuthenticationEvent;
import com.auth0.android.lock.views.next.ui.ActionButton;
import com.auth0.android.lock.views.next.ui.InputView;

/**
 * Created by lbalmaceda on 29/11/2017.
 */

/**
 * This class holds the email input to sent a reset password link
 */
public class ForgotPasswordScreen extends LinearLayout {
    private static final String TAG = ForgotPasswordScreen.class.getSimpleName();

    public ForgotPasswordScreen(Context context) {
        super(context);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        final InputView emailInputView = InputView.forIdentity(getContext(), IdentityStyle.EMAIL);
        ActionButton actionButton = new ActionButton(getContext());
        actionButton.setText("Send Link");

        addView(emailInputView);
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int px16 = getResources().getDimensionPixelSize(R.dimen.a0_size_16);
        params.setMargins(0, px16, 0, 0);
        addView(actionButton, params);

        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //FIXME: Should block the UI with some progress bar
                if (emailInputView.isValid(false)) {
                    AuthenticationEvent event = AuthenticationEvent.forgotPassword(emailInputView.getText());
                    ServiceLocator.getBus().post(event);
                }
            }
        });
    }

}
