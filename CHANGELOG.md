# Change Log

## [2.1.1](https://github.com/auth0/Lock.Android/tree/2.1.1) (2016-11-02)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/2.1.0...2.1.1)

**Fixed**
- Fix NPE when connection scope is missing [\#365](https://github.com/auth0/Lock.Android/pull/365) ([lbalmaceda](https://github.com/lbalmaceda))

## [2.1.0](https://github.com/auth0/Lock.Android/tree/2.1.0) (2016-10-24)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/2.0.0...2.1.0)

**Added**
- Support connection_scope for OAuth Connections [\#361](https://github.com/auth0/Lock.Android/pull/361) ([lbalmaceda](https://github.com/lbalmaceda))
- Send LockException if the Theme is invalid [\#358](https://github.com/auth0/Lock.Android/pull/358) ([lbalmaceda](https://github.com/lbalmaceda))
- Add labeled submit button option [\#352](https://github.com/auth0/Lock.Android/pull/352) ([lbalmaceda](https://github.com/lbalmaceda))

**Fixed**
- Non-empty username validation for custom/imported connections [\#360](https://github.com/auth0/Lock.Android/pull/360) ([lbalmaceda](https://github.com/lbalmaceda))
- Fix Theme load from styles.xml [\#357](https://github.com/auth0/Lock.Android/pull/357) ([lbalmaceda](https://github.com/lbalmaceda))

**Breaking changes**
- Use browser by default when using WebAuthProvider. [\#355](https://github.com/auth0/Lock.Android/pull/355) ([lbalmaceda](https://github.com/lbalmaceda))

Since Google will be [blocking webview OAuth request](https://developers.googleblog.com/2016/08/modernizing-oauth-interactions-in-native-apps.html) we switched the default authentication flow for all social connections from WebView to Browser.
Browser authentication requires a little more configuration in your `AndroidManifest.xml` file. 
First make sure `LockActivity` has `singleTask` in `android:launchMode` and then add to it an `intent-filter`:
 
```xml
<intent-filter>
  <action android:name="android.intent.action.VIEW" />

  <category android:name="android.intent.category.DEFAULT" />
  <category android:name="android.intent.category.BROWSABLE" />

  <data
    android:host="{YOUR_AUTH0_DOMAIN}"
    android:pathPrefix="/android/{YOUR_APP_PACKAGE_NAME}/callback"
    android:scheme="https" />
</intent-filter>
```


## [2.0.0](https://github.com/auth0/Lock.Android/tree/2.0.0) (2016-09-21)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/2.0.0-beta.4...2.0.0)

**Changed**
- Merge Enterprise and Social login events [\#347](https://github.com/auth0/Lock.Android/pull/347) ([lbalmaceda](https://github.com/lbalmaceda))
- Show button when only one enterprise connection is available [\#341](https://github.com/auth0/Lock.Android/pull/341) ([lbalmaceda](https://github.com/lbalmaceda))
- Filter social strategies by connection name [\#340](https://github.com/auth0/Lock.Android/pull/340) ([lbalmaceda](https://github.com/lbalmaceda))
- Flatten Strategies into Connections [\#335](https://github.com/auth0/Lock.Android/pull/335) ([lbalmaceda](https://github.com/lbalmaceda))

**Fixed**
- Fix OAuth connection callback for PasswordlessActivity [\#346](https://github.com/auth0/Lock.Android/pull/346) ([lbalmaceda](https://github.com/lbalmaceda))
- Fix non ro-enabled enterprise connections flow. [\#344](https://github.com/auth0/Lock.Android/pull/344) ([lbalmaceda](https://github.com/lbalmaceda))
- Fix wrong telemetry version [\#342](https://github.com/auth0/Lock.Android/pull/342) ([lbalmaceda](https://github.com/lbalmaceda))

**Breaking changes**
- [Breaking Change] Rename builder methods [\#350](https://github.com/auth0/Lock.Android/pull/350) ([lbalmaceda](https://github.com/lbalmaceda))
- Refactor AuthProviderResolver [Breaking Change] [\#333](https://github.com/auth0/Lock.Android/pull/333) ([lbalmaceda](https://github.com/lbalmaceda))

## [2.0.0-beta.4](https://github.com/auth0/Lock.Android/tree/2.0.0-beta.4) (2016-08-24)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/2.0.0-beta.3...2.0.0-beta.4)

**Closed issues**
- Lock SSO Username Fails Validation [\#332](https://github.com/auth0/Lock.Android/issues/332)

**Fixed**
- Change username validation for SSO connections [\#334](https://github.com/auth0/Lock.Android/pull/334) ([lbalmaceda](https://github.com/lbalmaceda))
- Check that requested tokens are present on the result. [\#330](https://github.com/auth0/Lock.Android/pull/330) ([lbalmaceda](https://github.com/lbalmaceda))
- Use first available connection name when authenticating with OAuth [\#320](https://github.com/auth0/Lock.Android/pull/320) ([lbalmaceda](https://github.com/lbalmaceda))

**Added**
- Custom Style for Social Buttons [\#325](https://github.com/auth0/Lock.Android/pull/325) ([lbalmaceda](https://github.com/lbalmaceda))
- Request the user to accept Terms&Policy before Sign Up [\#319](https://github.com/auth0/Lock.Android/pull/319) ([lbalmaceda](https://github.com/lbalmaceda))
- Handle too_many_attempts API error [\#308](https://github.com/auth0/Lock.Android/pull/308) ([lbalmaceda](https://github.com/lbalmaceda))
- Add Service Terms and Privacy Policy dialog [\#307](https://github.com/auth0/Lock.Android/pull/307) ([lbalmaceda](https://github.com/lbalmaceda))

**Changed**
- Force init lock [Breaking Change] [\#329](https://github.com/auth0/Lock.Android/pull/329) ([lbalmaceda](https://github.com/lbalmaceda))
- Update Auth0 lib version to latest [\#327](https://github.com/auth0/Lock.Android/pull/327) ([lbalmaceda](https://github.com/lbalmaceda))
- Hide Theme configuration on the Builder  [Breaking Change] [\#326](https://github.com/auth0/Lock.Android/pull/326) ([lbalmaceda](https://github.com/lbalmaceda))
- Use AuthMode constants when notifying tab change [\#323](https://github.com/auth0/Lock.Android/pull/323) ([lbalmaceda](https://github.com/lbalmaceda))
- Handle wrong Client Type error [\#321](https://github.com/auth0/Lock.Android/pull/321) ([lbalmaceda](https://github.com/lbalmaceda))
- Change SocialButton title when changing the Form mode [\#317](https://github.com/auth0/Lock.Android/pull/317) ([lbalmaceda](https://github.com/lbalmaceda))
- UI Improvements: Bigger buttons/fields  [\#314](https://github.com/auth0/Lock.Android/pull/314) ([lbalmaceda](https://github.com/lbalmaceda))
- New Tab design. [\#313](https://github.com/auth0/Lock.Android/pull/313) ([lbalmaceda](https://github.com/lbalmaceda))
- Use pngs instead of vectorial xml files [\#311](https://github.com/auth0/Lock.Android/pull/311) ([lbalmaceda](https://github.com/lbalmaceda))
- Make PKCE enabled by default [\#310](https://github.com/auth0/Lock.Android/pull/310) ([lbalmaceda](https://github.com/lbalmaceda))
- Always pick defaultDbConnection if available [\#309](https://github.com/auth0/Lock.Android/pull/309) ([lbalmaceda](https://github.com/lbalmaceda))

**Breaking changes**

`Lock` & `PassworlessLock` no longer has the method `onCreate(Activity)` and  it's logic is now part of the method `Lock.Builder.build(Activity)`. So to create a Lock instance you will have

```java
Lock lock = Lock.newBuilder(auth0, callback)
      //Customize Lock
      .build(this);
```

Also now you can create `Lock` by reading your Auth0 account credentials from a strings file

```java
Lock lock = Lock.newBuilder(callback)
      //Customize Lock
      .build(this);
```

and he string file should have

```xml
 <resources>
    <string name="com_auth0_client_id">{CLIENT_ID}</string>
    <string name="com_auth0_domain">{DOMAIN}</string>
</resources>
```

`Lock.Builder` no longers allow to customize Lock's theme using the method `withTheme(Theme)` since using Android themes is preferable.

Also for all non-database authentication will use **Proof Key for Code Exchange** by default so your client type in Auth0 dashboard **must** be `Native`.

## [2.0.0-beta.3](https://github.com/auth0/Lock.Android/tree/2.0.0-beta.3) (2016-07-22)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/2.0.0-beta.2...2.0.0-beta.3)

**Added**

- Locally Configurable Lock sample app [\#298](https://github.com/auth0/Lock.Android/pull/298) ([lbalmaceda](https://github.com/lbalmaceda)
- Password Strength Widget [\#297](https://github.com/auth0/Lock.Android/pull/297) ([lbalmaceda](https://github.com/lbalmaceda)

**Changed**

- Update Proguard rules [\#305](https://github.com/auth0/Lock.Android/pull/305) ([lbalmaceda](https://github.com/lbalmaceda)
- Remove Fullscreen feature [\#302](https://github.com/auth0/Lock.Android/pull/302) ([lbalmaceda](https://github.com/lbalmaceda)
- Add callback setup on dashboard [\#300](https://github.com/auth0/Lock.Android/pull/300) ([lbalmaceda](https://github.com/lbalmaceda)
- Allow to customize Lock's theme programmatically  [\#294](https://github.com/auth0/Lock.Android/pull/294) ([lbalmaceda](https://github.com/lbalmaceda)
- Remove unused User Profile calls. [\#293](https://github.com/auth0/Lock.Android/pull/293) ([lbalmaceda](https://github.com/lbalmaceda)
- Take email input across the forms [\#292](https://github.com/auth0/Lock.Android/pull/292) ([lbalmaceda](https://github.com/lbalmaceda)
- Allow up to 3 Social Big Buttons on Passwordless mode [\#291](https://github.com/auth0/Lock.Android/pull/291) ([lbalmaceda](https://github.com/lbalmaceda)
- Improve Custom Fields flow and layout [\#290](https://github.com/auth0/Lock.Android/pull/290) ([lbalmaceda](https://github.com/lbalmaceda)
- Handle Application without Connections [\#289](https://github.com/auth0/Lock.Android/pull/289) ([lbalmaceda](https://github.com/lbalmaceda)
- Draw the header behind the statusBar in Lollipop or greater [\#288](https://github.com/auth0/Lock.Android/pull/288) ([lbalmaceda](https://github.com/lbalmaceda)

**Fixed**

- Fix sign up request not sending the user metadata [\#303](https://github.com/auth0/Lock.Android/pull/303) ([lbalmaceda](https://github.com/lbalmaceda)
- Fix TextView extra padding. [\#296](https://github.com/auth0/Lock.Android/pull/296) ([lbalmaceda](https://github.com/lbalmaceda)
- Country code selection widget fixes  [\#295](https://github.com/auth0/Lock.Android/pull/295) ([lbalmaceda](https://github.com/lbalmaceda)

**Breaking changes**

**Lock** `Builder` method

```java
public Builder allowSignIn(boolean allow) {...}
```

was renamed to

```java
public Builder allowLogIn(boolean allow) {...}
```

Also this method (and feature) is no longer supported in **Lock**

```java
public Builder fullscreen(boolean fullscreen) {...}
```


## [2.0.0-beta.2](https://github.com/auth0/Lock.Android/tree/2.0.0-beta.2) (2016-06-06)
[Full Changelog](https://github.com/auth0/Lock.Android/compare/2.0.0-beta.1...2.0.0-beta.2)

**Changed**
- Use new version of auth0-java to fix issue with json parsing [\#286](https://github.com/auth0/Lock.Android/pull/286) ([lbalmaceda](https://github.com/lbalmaceda))
- Fix issues with default values of `allow****` and `initialScreen` options [\#286](https://github.com/auth0/Lock.Android/pull/286) ([lbalmaceda](https://github.com/lbalmaceda))

**Breaking changes**

`AuthenticationCallback` no longer returns `UserProfile`, it only returns `Credentials` object with the tokens of the authenticated user:

```java
private LockCallback callback = new AuthenticationCallback() {
     @Override
     public void onAuthentication(Credentials credentials) {
        //Authenticated
     }

     @Override
     public void onCanceled() {
        //User pressed back
     }

     @Override
     public void onError(LockException error)
        //Exception occurred
     }
 };
```

To request the `UserProfile`, just use `AuthenticationAPIClient` from [auth0-java](https://github.com/auth0/auth0-java)

```java
@Override
public void onAuthentication(Credentials credentials) {
    AuthenticationAPIClient client = new AuthenticationAPIClient(new Auth0("YOUR_CLIENT_ID", "YOUR_DOMAIN"));

    client.tokenInfo(credentials.idToken)
        .start(new BaseCallback<UserProfile>() {
            @Override
            public void onSuccess(UserProfile payload) { }

            @Override
            public void onFailure(Auth0Exception error) { }
        });
}
```

## [2.0.0-beta.1](https://github.com/auth0/Lock.Android/tree/2.0.0-beta.1) (2016-06-03)

First beta release of Lock for Android v2

### Declaration in AndroidManifest.xml

Now Lock for Android requires these permisssions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

and this is how `LockActivity` should be declared in your Android Manifest

```xml
<activity
  android:name="com.auth0.android.lock.LockActivity"
  android:label="@string/app_name"
  android:launchMode="singleTask"
  android:screenOrientation="portrait"
  android:theme="@style/Lock.Theme">
    <intent-filter>
      <action android:name="android.intent.action.VIEW" />

      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />

      <data
        android:host="YOUR_AUTH0_DOMAIN"
        android:pathPrefix="/android/YOUR_APP_PACKAGE_NAME/callback"
        android:scheme="https" />
    </intent-filter>
</activity>
```

### Lock instance

In the previous version of **Lock**, you were asked to create a custom `Application` class and initialize the `Lock.Context` there. Now this is no longer needed. To create a new `Lock` instance and configure it, use the `Lock.Builder` class.

#### Auth0
Create an `Auth0` instance to hold your account details, which are the `AUTH0_CLIENT_ID` and the `AUTH0_DOMAIN`.

```java
Auth0 auth0 = new Auth0("YOUR_AUTH0_CLIENT_ID", "YOUR_AUTH0_DOMAIN");
```

### Authentication Callback
You'll also need a `LockCallback` implementation, we provide `AuthenticationCallback` that reports the following events:

* onAuthentication: User successfuly authenticated
* onError: An unrecoverable error ocurred during authentication
* onCanceled: User pressed back (if closable is true)

> If you need a more fine grained control you can implement `LockCallback` full interface.

```java
private LockCallback callback = new AuthenticationCallback() {
     @Override
     public void onAuthentication(Authentication authentication) {
        //Authenticated
     }

     @Override
     public void onCanceled() {
        //User pressed back
     }

     @Override
     public void onError(LockException error)
        //Exception occurred
     }
 };
```

### Lock.Builder
Call the static method `Lock.newBuilder(Auth0, AuthenticationCallback)` passing the account details and the callback implementation, and start configuring the [Options](#options). After you're done, build the `Lock` instance and use it to start the `LockActivity`.

This is how your activity should look like.

```java
public class MainActivity extends Activity {
  private Lock lock;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);
    lock = Lock.newBuilder(auth0, callback)
      // ... Options
      .build();
    lock.onCreate(this);
  }

  @Override
  public void onDestroy() {
    lock.onDestroy(this);
    super.onDestroy();
  }

  private void performLogin(boolean useBrowser) {
    startActivity(lock.newIntent(this));
  }

  private LockCallback callback = new AuthenticationCallback() {
       @Override
       public void onAuthentication(Authentication authentication) {
          //Authenticated
       }

       @Override
       public void onCanceled() {
          //User pressed back
       }

       @Override
       public void onError(LockException error) {
          //Exception occurred
       }
   };
}
```

>Remember to notify the `LockActivity` on every `OnCreate` and `OnDestroy` call on your Activity, as it helps to keep the Lock state.

### Options

As in the previous version, `Lock` can be configured with extra options. Check below if the behavior changed or if they only got renamed.

#### Renamed options from v1

- shouldUseWebView: Renamed to `useBrowser`. Whether to use the WebView or the Browser to request calls to the `/authorize` endpoint. Using the Browser has some [restrictions](#some-restrictions).
- shouldUseEmail: Renamed to `withUsernameStyle`. Defines if it should ask for email only, username only, or both of them. By default, it'll respect the Dashboard configuration of the parameter `requires_username`.
- isClosable: Renamed to `closable`. Defines if the LockActivity can be closed. By default, it's not closable.
- setFullscreen: Renamed to `fullscreen`. Defines if the LockActivity it's displayed in fullscreen. By default, it's not fullscreen.
- shouldLoginAfterSignUp: Renamed to `loginAfterSignUp`. Whether after a SignUp the user should be logged in automatically.
- disableSignupAction: Renamed to `allowSignUp`. Shows the Sign Up form if a Database connection is configured.
- disableResetAction: Renamed to `allowForgotPassword`. Shows a link to the Forgot Password form if a Database connection is configured and it's allowed from the Dashboard.
- defaultUserPasswordConnection: Renamed to `setDefaultDatabaseConnection`. Defines which will be the default Database connection. This is useful if your application has many Database connections configured.
- setConnections: Renamed to `onlyUseConnections`. Filters the allowed connections from the list configured in the Dashboard..
- setAuthenticationParameters: Renamed to `withAuthenticationParameters`. Defines extra authentication parameters, sent on sign up and log in/sign in.


#### New options in v2

- `initialScreen(int)` allows to customize which form will show first when launching **Lock**. The possibles values are LOG_IN, SIGN_UP, and FORGOT_PASSWORD. By default LOG_IN is the initial screen.
- `allowLogIn(boolean)` shows the Log In form if a Database connection is configured. By default, this screen it's enabled.
- `allowSignUp(boolean)` shows the Sign Up form if a Database connection is configured. By default, this screen it's enabled.
- `allowForgotPassword(boolean)` shows the Forgot Password form if a Database connection is configured. By default, this screen it's enabled.
- `withSignUpFields(List<CustomFields>)` shows a second screen with extra fields after completing the sign up fields.
- `withProviderResolver(AuthProviderResolver)` pass your own AuthProviderResolver instance to query for AuthProviders.
- `withSocialButtonStyle(int)` allows to customize the Style of the Social buttons. Possible values are SMALL and BIG. If this is not specified, it will default to SMALL when many Social and Db/Enterprise connections are configured; and BIG on the rest of the cases.
- `usePKCE(boolean)` whether to use the new PKCE flow or the old Token exchange one when authenticating. By default, it won't use PKCE.