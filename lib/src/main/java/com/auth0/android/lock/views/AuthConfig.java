package com.auth0.android.lock.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.StyleRes;
import androidx.annotation.StyleableRes;
import androidx.core.content.ContextCompat;

import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.OAuthConnection;

public class AuthConfig {

    private final OAuthConnection connection;
    @StyleRes
    private final int styleRes;

    public AuthConfig(@NonNull OAuthConnection connection, @StyleRes int styleRes) {
        this.connection = connection;
        this.styleRes = styleRes;
    }

    @NonNull
    public OAuthConnection getConnection() {
        return connection;
    }

    @NonNull
    public String getName(@NonNull Context context) {
        @SuppressLint("ResourceType")
        int id = getIdForResource(context, R.attr.Auth0_Name);
        return id != -1 ? context.getString(id) : connection.getStrategy();
    }

    @NonNull
    public Drawable getLogo(@NonNull Context context) {
        @SuppressLint("ResourceType")
        int id = getIdForResource(context, R.attr.Auth0_Logo);
        if (id == -1) {
            id = R.drawable.com_auth0_lock_ic_social_auth0;
        }
        //noinspection ConstantConditions
        return ContextCompat.getDrawable(context, id);
    }

    @ColorInt
    public int getBackgroundColor(@NonNull Context context) {
        @SuppressLint("ResourceType")
        int id = getIdForResource(context, R.attr.Auth0_BackgroundColor);
        if (id == -1) {
            id = R.color.com_auth0_lock_social_unknown;
        }
        return ContextCompat.getColor(context, id);
    }

    /**
     * Retrieves the resource id of the given Style index.
     *
     * @param context a valid Context
     * @param index   The index to search on the Style definition.
     * @return the id if found or -1.
     */
    int getIdForResource(@NonNull Context context, @StyleableRes int index) {
        final int[] attrs = new int[]{index};
        final TypedArray typedArray = context.getTheme().obtainStyledAttributes(styleRes, attrs);
        int id = typedArray.getResourceId(0, -1);
        typedArray.recycle();
        return id;
    }

    /**
     * It will resolve the given Strategy Name to a valid Style.
     *
     * @param strategyName to search for.
     * @return a valid Lock.Theme.AuthStyle
     */
    @StyleRes
    public static int styleForStrategy(@NonNull String strategyName) {
        int style = R.style.Lock_Theme_AuthStyle;
        switch (strategyName) {
            case "apple":
                style = R.style.Lock_Theme_AuthStyle_Apple;
                break;
            case "amazon":
                style = R.style.Lock_Theme_AuthStyle_Amazon;
                break;
            case "aol":
                style = R.style.Lock_Theme_AuthStyle_AOL;
                break;
            case "bitbucket":
                style = R.style.Lock_Theme_AuthStyle_BitBucket;
                break;
            case "dropbox":
                style = R.style.Lock_Theme_AuthStyle_Dropbox;
                break;
            case "yahoo":
                style = R.style.Lock_Theme_AuthStyle_Yahoo;
                break;
            case "linkedin":
                style = R.style.Lock_Theme_AuthStyle_LinkedIn;
                break;
            case "google-oauth2":
                style = R.style.Lock_Theme_AuthStyle_GoogleOAuth2;
                break;
            case "twitter":
                style = R.style.Lock_Theme_AuthStyle_Twitter;
                break;
            case "facebook":
                style = R.style.Lock_Theme_AuthStyle_Facebook;
                break;
            case "box":
                style = R.style.Lock_Theme_AuthStyle_Box;
                break;
            case "evernote":
                style = R.style.Lock_Theme_AuthStyle_Evernote;
                break;
            case "evernote-sandbox":
                style = R.style.Lock_Theme_AuthStyle_EvernoteSandbox;
                break;
            case "exact":
                style = R.style.Lock_Theme_AuthStyle_Exact;
                break;
            case "github":
                style = R.style.Lock_Theme_AuthStyle_GitHub;
                break;
            case "instagram":
                style = R.style.Lock_Theme_AuthStyle_Instagram;
                break;
            case "miicard":
                style = R.style.Lock_Theme_AuthStyle_MiiCard;
                break;
            case "paypal":
                style = R.style.Lock_Theme_AuthStyle_Paypal;
                break;
            case "paypal-sandbox":
                style = R.style.Lock_Theme_AuthStyle_PaypalSandbox;
                break;
            case "salesforce":
                style = R.style.Lock_Theme_AuthStyle_Salesforce;
                break;
            case "salesforce-community":
                style = R.style.Lock_Theme_AuthStyle_SalesforceCommunity;
                break;
            case "salesforce-sandbox":
                style = R.style.Lock_Theme_AuthStyle_SalesforceSandbox;
                break;
            case "soundcloud":
                style = R.style.Lock_Theme_AuthStyle_SoundCloud;
                break;
            case "windowslive":
                style = R.style.Lock_Theme_AuthStyle_WindowsLive;
                break;
            case "yammer":
                style = R.style.Lock_Theme_AuthStyle_Yammer;
                break;
            case "baidu":
                style = R.style.Lock_Theme_AuthStyle_Baidu;
                break;
            case "fitbit":
                style = R.style.Lock_Theme_AuthStyle_Fitbit;
                break;
            case "planningcenter":
                style = R.style.Lock_Theme_AuthStyle_PlanningCenter;
                break;
            case "renren":
                style = R.style.Lock_Theme_AuthStyle_RenRen;
                break;
            case "thecity":
                style = R.style.Lock_Theme_AuthStyle_TheCity;
                break;
            case "thecity-sandbox":
                style = R.style.Lock_Theme_AuthStyle_TheCitySandbox;
                break;
            case "thirtysevensignals":
                style = R.style.Lock_Theme_AuthStyle_ThirtySevenSignals;
                break;
            case "vkontakte":
                style = R.style.Lock_Theme_AuthStyle_Vkontakte;
                break;
            case "weibo":
                style = R.style.Lock_Theme_AuthStyle_Weibo;
                break;
            case "wordpress":
                style = R.style.Lock_Theme_AuthStyle_Wordpress;
                break;
            case "yandex":
                style = R.style.Lock_Theme_AuthStyle_Yandex;
                break;
            case "shopify":
                style = R.style.Lock_Theme_AuthStyle_Shopify;
                break;
            case "dwolla":
                style = R.style.Lock_Theme_AuthStyle_Dwolla;
                break;
        }

        return style;
    }
}
