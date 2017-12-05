package com.auth0.android.lock.views.next.forms;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.auth0.android.lock.R;
import com.auth0.android.lock.ServiceLocator;
import com.auth0.android.lock.views.next.configuration.internal.Configuration;
import com.auth0.android.lock.views.next.events.NavigationEvent;
import com.auth0.android.lock.views.next.ui.HeaderView;
import com.auth0.android.lock.views.next.ui.OAuthView;
import com.auth0.android.lock.views.next.ui.SeparatorView;
import com.auth0.android.lock.views.next.ui.UsernamePasswordView;

/**
 * Created by lbalmaceda on 29/11/2017.
 */

/**
 * This class will hold all the small forms related to Log In like {@link UsernamePasswordView} or future Social list views.
 */
public class LogInScreen extends LinearLayout {
    private static final String TAG = LogInScreen.class.getSimpleName();
    private Configuration configuration;

    public LogInScreen(Context context) {
        super(context);
        initProgress();
        //TODO: Probably it shouldn't be instantiated if log in is disabled
    }

    private void initProgress() {
        //Show some progress while configuration is being fetched
    }

    private void init(Configuration configuration) {
        setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        HeaderView headerView = new HeaderView(getContext());
        addView(headerView, params);

        int px24 = getResources().getDimensionPixelSize(R.dimen.a0_size_24);
        params.setMargins(0, px24, 0, 0);
        boolean allowsDatabase = configuration.getDatabaseConnection() != null;
        boolean allowsOAuth = !configuration.getSocialConnections().isEmpty();
        if (allowsDatabase) {
            UsernamePasswordView usernamePasswordView = new UsernamePasswordView(getContext());
            addView(usernamePasswordView, params);
        }

        if (allowsDatabase && allowsOAuth) {
            SeparatorView orSeparator = new SeparatorView(getContext());
            addView(orSeparator, params);
        }

        if (allowsOAuth) {
            OAuthView oAuthView = new OAuthView(getContext(), configuration.getSocialConnections());
            addView(oAuthView, params);
        }

        if (allowsDatabase && configuration.allowSignUp()) {
            LinearLayout signUpInvitation = new LinearLayout(getContext());
            signUpInvitation.setGravity(Gravity.CENTER_HORIZONTAL);
            LayoutParams wrapParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            TextView dontHaveAccount = new TextView(getContext());
            dontHaveAccount.setTextColor(ContextCompat.getColor(getContext(), R.color.a0Night));
            dontHaveAccount.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.a0_font_14));
            dontHaveAccount.setText(R.string.a0_dont_have_an_account);
            TextView signUpLink = new TextView(getContext());
            signUpLink.setTextColor(ContextCompat.getColor(getContext(), R.color.a0LinkText));
            signUpLink.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.a0_font_14));
            signUpLink.setText(R.string.a0_button_sign_up);
            signUpLink.setTypeface(Typeface.create("sans-serif-medium", Typeface.NORMAL));
            int px8 = getResources().getDimensionPixelSize(R.dimen.a0_size_8);
            signUpLink.setPadding(px8, 0, px8, 0);

            signUpInvitation.addView(dontHaveAccount, wrapParams);
            signUpInvitation.addView(signUpLink, wrapParams);
            signUpLink.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavigationEvent event = new NavigationEvent(NavigationEvent.NAVIGATE_TO_SIGN_UP);
                    ServiceLocator.getBus().post(event);
                }
            });
            int px16 = getResources().getDimensionPixelSize(R.dimen.a0_size_16);
            params.setMargins(0, px16, 0, 0);
            addView(signUpInvitation, params);
        }
    }


    /**
     * Setup the widget using the given configuration. Calling this method after a successful configuration will do nothing.
     *
     * @param configuration The configuration to use in this widget setup. Setting a null configuration would show a retry screen
     */
    @UiThread
    public void setConfiguration(@Nullable Configuration configuration) {
        if (this.configuration != null) {
            Log.w(TAG, "Configuration can only be set once");
            return;
        }
        this.configuration = configuration;
        init(configuration);
    }
}
