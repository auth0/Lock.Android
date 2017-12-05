package com.auth0.android.lock.views.next.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.ServiceLocator;
import com.auth0.android.lock.views.next.configuration.internal.IdentityStyle;
import com.auth0.android.lock.views.next.events.AuthenticationEvent;
import com.auth0.android.lock.views.next.events.NavigationEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lbalmaceda on 29/11/2017.
 */

public class UsernamePasswordView extends LinearLayout {

    private InputView identityField;
    private InputView passwordField;
    private List<InputView> fields;
    private TextView forgotPasswordButton;

    public UsernamePasswordView(Context context) {
        super(context);
        init();
    }

    private void init() {
        ActionButton actionButton = new ActionButton(getContext());
        actionButton.setText("Log in");
        forgotPasswordButton = new TextView(getContext());
        forgotPasswordButton.setTextColor(ContextCompat.getColor(getContext(), R.color.a0LinkText));
        forgotPasswordButton.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.a0_font_14));
        forgotPasswordButton.setGravity(Gravity.CENTER_HORIZONTAL);
        forgotPasswordButton.setText(R.string.a0_button_forgot_password);
        forgotPasswordButton.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
        identityField = InputView.forIdentity(getContext(), IdentityStyle.USERNAME_AND_EMAIL);//TODO: Get IdentityStyle from configuration
        passwordField = InputView.forPassword(getContext(), true);//TODO: Allow to change the show password feature. Maybe set the fields from outside

        int px16 = getResources().getDimensionPixelSize(R.dimen.a0_size_16);
        LinearLayout.LayoutParams params16 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params16.setMargins(0, px16, 0, 0);

        setOrientation(VERTICAL);
        addField(identityField, false);
        addField(passwordField, false);
        addView(actionButton, params16);
        addView(forgotPasswordButton, params16);

        if (isInEditMode()) {
            return;
        }

        fields = new ArrayList<>(0);
        actionButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                validateFields();
            }
        });
        forgotPasswordButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ServiceLocator.getBus().post(new NavigationEvent(NavigationEvent.NAVIGATE_TO_FORGOT_PASSWORD));
            }
        });
    }

    private void validateFields() {
        int counter = 2 + fields.size();
        if (identityField.isValid(false)) {
            counter--;
        }
        if (passwordField.isValid(false)) {
            counter--;
        }
        for (InputView f : fields) {
            if (f.isValid(false)) {
                counter--;
            }
        }
        if (counter > 0) {
            //Invalid fields
            return;
        }

        //FIXME: Should block the UI with some progress bar
        ServiceLocator.getBus().post(AuthenticationEvent.realmLogin(identityField.getText(), identityField.getText()));
    }

    private void addField(@NonNull InputView inputView, boolean fromTheTop) {
        //2 because actionButton + forgotPasswordButton
        int pos = fromTheTop ? 0 : getChildCount() - 2; //Add it above the submit button
        int px = pos == 0 ? 0 : getResources().getDimensionPixelSize(R.dimen.a0_size_8);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, px, 0, 0);

        addView(inputView, pos, params);
    }

    //InputView instances could be created from some given parcelable options
    public void addExtraField(@NonNull InputView inputView, boolean fromTheTop) {
        addField(inputView, fromTheTop);
        fields.add(inputView);
    }

    public void setForgotPasswordEnabled(boolean enabled) {
        forgotPasswordButton.setVisibility(enabled ? View.VISIBLE : View.GONE);
    }

}
