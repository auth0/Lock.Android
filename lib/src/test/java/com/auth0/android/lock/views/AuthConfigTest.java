package com.auth0.android.lock.views;

import android.graphics.drawable.Drawable;
import android.os.Build;

import com.auth0.android.lock.BuildConfig;
import com.auth0.android.lock.R;
import com.auth0.android.lock.internal.configuration.OAuthConnection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;
import org.robolectric.util.ReflectionHelpers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21, manifest = Config.NONE)
public class AuthConfigTest {

    private AuthConfig authConfig;
    private OAuthConnection connection;

    @Before
    public void setUp() throws Exception {
        connection = mock(OAuthConnection.class);
        when(connection.getName()).thenReturn("facebook-prod");
        when(connection.getStrategy()).thenReturn("facebook");
        authConfig = new AuthConfig(connection, R.style.Lock_Theme_AuthStyle_Facebook);
    }

    @Test
    public void shouldGetConnection() throws Exception {
        Assert.assertThat(authConfig.getConnection(), is(connection));
    }

    @Test
    public void shouldGetName() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN);
        final String string = RuntimeEnvironment.application.getResources().getString(R.string.com_auth0_lock_social_facebook);
        Assert.assertThat(authConfig.getName(RuntimeEnvironment.application), is(equalTo(string)));
    }

    @Test
    public void shouldGetLogo() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN);
        final Drawable drawable = RuntimeEnvironment.application.getResources().getDrawable(R.drawable.com_auth0_lock_ic_social_facebook);
        Assert.assertThat(authConfig.getLogo(RuntimeEnvironment.application), is(equalTo(drawable)));
    }

    @Test
    public void shouldGetBackgroundColor() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN);
        final int color = RuntimeEnvironment.application.getResources().getColor(R.color.com_auth0_lock_social_facebook);
        Assert.assertThat(authConfig.getBackgroundColor(RuntimeEnvironment.application), is(equalTo(color)));
    }

    @Test
    public void shouldHaveValidDefaultName() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN);
        AuthConfig defaultConfig = new AuthConfig(connection, R.style.Lock_Theme);
        Assert.assertThat(defaultConfig.getName(RuntimeEnvironment.application), is(equalTo("facebook")));
    }

    @Test
    public void shouldHaveValidDefaultLogo() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN);
        AuthConfig defaultConfig = new AuthConfig(connection, R.style.Lock_Theme);
        final Drawable drawable = RuntimeEnvironment.application.getResources().getDrawable(R.drawable.com_auth0_lock_ic_social_facebook);
        Assert.assertThat(defaultConfig.getLogo(RuntimeEnvironment.application), is(equalTo(drawable)));
    }

    @Test
    public void shouldHaveValidDefaultColor() throws Exception {
        ReflectionHelpers.setStaticField(Build.VERSION.class, "SDK_INT", Build.VERSION_CODES.JELLY_BEAN);
        AuthConfig defaultConfig = new AuthConfig(connection, R.style.Lock_Theme);
        final int color = RuntimeEnvironment.application.getResources().getColor(R.color.com_auth0_lock_social_unknown);
        Assert.assertThat(defaultConfig.getBackgroundColor(RuntimeEnvironment.application), is(equalTo(color)));
    }

    @Test
    public void shouldGetStyleForStrategy() throws Exception {
        Assert.assertThat(AuthConfig.styleForStrategy("amazon"), is(R.style.Lock_Theme_AuthStyle_Amazon));
        Assert.assertThat(AuthConfig.styleForStrategy("aol"), is(R.style.Lock_Theme_AuthStyle_AOL));
        Assert.assertThat(AuthConfig.styleForStrategy("bitbucket"), is(R.style.Lock_Theme_AuthStyle_BitBucket));
        Assert.assertThat(AuthConfig.styleForStrategy("dropbox"), is(R.style.Lock_Theme_AuthStyle_Dropbox));
        Assert.assertThat(AuthConfig.styleForStrategy("yahoo"), is(R.style.Lock_Theme_AuthStyle_Yahoo));
        Assert.assertThat(AuthConfig.styleForStrategy("linkedin"), is(R.style.Lock_Theme_AuthStyle_LinkedIn));
        Assert.assertThat(AuthConfig.styleForStrategy("google-oauth2"), is(R.style.Lock_Theme_AuthStyle_GoogleOAuth2));
        Assert.assertThat(AuthConfig.styleForStrategy("twitter"), is(R.style.Lock_Theme_AuthStyle_Twitter));
        Assert.assertThat(AuthConfig.styleForStrategy("facebook"), is(R.style.Lock_Theme_AuthStyle_Facebook));
        Assert.assertThat(AuthConfig.styleForStrategy("box"), is(R.style.Lock_Theme_AuthStyle_Box));
        Assert.assertThat(AuthConfig.styleForStrategy("evernote"), is(R.style.Lock_Theme_AuthStyle_Evernote));
        Assert.assertThat(AuthConfig.styleForStrategy("evernote-sandbox"), is(R.style.Lock_Theme_AuthStyle_EvernoteSandbox));
        Assert.assertThat(AuthConfig.styleForStrategy("exact"), is(R.style.Lock_Theme_AuthStyle_Exact));
        Assert.assertThat(AuthConfig.styleForStrategy("github"), is(R.style.Lock_Theme_AuthStyle_GitHub));
        Assert.assertThat(AuthConfig.styleForStrategy("instagram"), is(R.style.Lock_Theme_AuthStyle_Instagram));
        Assert.assertThat(AuthConfig.styleForStrategy("miicard"), is(R.style.Lock_Theme_AuthStyle_MiiCard));
        Assert.assertThat(AuthConfig.styleForStrategy("paypal"), is(R.style.Lock_Theme_AuthStyle_Paypal));
        Assert.assertThat(AuthConfig.styleForStrategy("paypal-sandbox"), is(R.style.Lock_Theme_AuthStyle_PaypalSandbox));
        Assert.assertThat(AuthConfig.styleForStrategy("salesforce"), is(R.style.Lock_Theme_AuthStyle_Salesforce));
        Assert.assertThat(AuthConfig.styleForStrategy("salesforce-community"), is(R.style.Lock_Theme_AuthStyle_SalesforceCommunity));
        Assert.assertThat(AuthConfig.styleForStrategy("salesforce-sandbox"), is(R.style.Lock_Theme_AuthStyle_SalesforceSandbox));
        Assert.assertThat(AuthConfig.styleForStrategy("soundcloud"), is(R.style.Lock_Theme_AuthStyle_SoundCloud));
        Assert.assertThat(AuthConfig.styleForStrategy("windowslive"), is(R.style.Lock_Theme_AuthStyle_WindowsLive));
        Assert.assertThat(AuthConfig.styleForStrategy("yammer"), is(R.style.Lock_Theme_AuthStyle_Yammer));
        Assert.assertThat(AuthConfig.styleForStrategy("baidu"), is(R.style.Lock_Theme_AuthStyle_Baidu));
        Assert.assertThat(AuthConfig.styleForStrategy("fitbit"), is(R.style.Lock_Theme_AuthStyle_Fitbit));
        Assert.assertThat(AuthConfig.styleForStrategy("planningcenter"), is(R.style.Lock_Theme_AuthStyle_PlanningCenter));
        Assert.assertThat(AuthConfig.styleForStrategy("renren"), is(R.style.Lock_Theme_AuthStyle_RenRen));
        Assert.assertThat(AuthConfig.styleForStrategy("thecity"), is(R.style.Lock_Theme_AuthStyle_TheCity));
        Assert.assertThat(AuthConfig.styleForStrategy("thecity-sandbox"), is(R.style.Lock_Theme_AuthStyle_TheCitySandbox));
        Assert.assertThat(AuthConfig.styleForStrategy("thirtysevensignals"), is(R.style.Lock_Theme_AuthStyle_ThirtySevenSignals));
        Assert.assertThat(AuthConfig.styleForStrategy("vkontakte"), is(R.style.Lock_Theme_AuthStyle_Vkontakte));
        Assert.assertThat(AuthConfig.styleForStrategy("weibo"), is(R.style.Lock_Theme_AuthStyle_Weibo));
        Assert.assertThat(AuthConfig.styleForStrategy("wordpress"), is(R.style.Lock_Theme_AuthStyle_Wordpress));
        Assert.assertThat(AuthConfig.styleForStrategy("yandex"), is(R.style.Lock_Theme_AuthStyle_Yandex));
        Assert.assertThat(AuthConfig.styleForStrategy("shopify"), is(R.style.Lock_Theme_AuthStyle_Shopify));
        Assert.assertThat(AuthConfig.styleForStrategy("dwolla"), is(R.style.Lock_Theme_AuthStyle_Dwolla));
    }

    @Test
    public void shouldGetDefaultStyleForUnknownStrategy() throws Exception {
        Assert.assertThat(AuthConfig.styleForStrategy("unknown-strategy"), is(R.style.Lock_Theme_AuthStyle));
    }

}