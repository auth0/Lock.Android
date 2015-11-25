Lock for Android
============
[![CI Status](http://img.shields.io/travis/auth0/Lock.Android.svg?style=flat)](https://travis-ci.org/auth0/Lock.Android)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)
[![Maven Central](https://img.shields.io/maven-central/v/com.auth0.android/lock.svg)](http://search.maven.org/#artifactdetails%7Ccom.auth0.android%7Clock%7C1.8.0%7Caar)
[ ![Download](https://api.bintray.com/packages/auth0/lock-android/lock/images/download.svg) ](https://bintray.com/auth0/lock-android/lock/_latestVersion)

[Auth0](https://auth0.com) is an authentication broker that supports social identity providers as well as enterprise identity providers such as Active Directory, LDAP, Google Apps and Salesforce.

## Key features

* **Integrates** your Android app with **Auth0**.
* Provides a **beautiful native UI** to log your users in.
* Provides support for **Social Providers** (Facebook, Twitter, etc.), **Enterprise Providers** (AD, LDAP, etc.) and **Username & Password**.
* Passwordless authentication using **SMS** and **Email**.

## Requirements

Android API level 15+ is required in order to use Lock's UI.
If you'll create your own API and just call Auth0 API via the `com.auth0.android:core:1.12.+`, the minimum required API level is 9.

##Install

Lock is available both in [Maven Central](http://search.maven.org) and [JCenter](https://bintray.com/bintray/jcenter). To start using *Lock* add these lines to your `build.gradle` dependencies file:

```gradle
compile 'com.auth0.android:lock:1.12.+'
```

Once it's installed, you'll need to configure LockActivity in your`AndroidManifest.xml`, inside the `application` tag:

```xml
<!--Auth0 Lock-->
<activity
  android:name="com.auth0.lock.LockActivity"
  android:theme="@style/Lock.Theme"
  android:screenOrientation="portrait"
  android:launchMode="singleTask">
  <intent-filter>
    <action android:name="android.intent.action.VIEW"/>
    <category android:name="android.intent.category.DEFAULT"/>
    <category android:name="android.intent.category.BROWSABLE"/>
    <data android:scheme="a0<INSERT_YOUR_APP_CLIENT_ID>" android:host="@string/auth0_domain"/>
  </intent-filter>
</activity>
<meta-data android:name="com.auth0.lock.client-id" android:value="@string/auth0_client_id"/>
<meta-data android:name="com.auth0.lock.domain-url" android:value="@string/auth0_domain"/>
<!--Auth0 Lock End-->
```

> The value `@string/auth0_client_id` is your application's clientID and `@string/auth0_domain` is your tenant's domain in Auth0, both values can be found in your app's settings.
> The final value of `android:scheme` must be in lowercase

Also, you'll need to add *Internet* permission to your application:
```xml
<uses-permission android:name="android.permission.INTERNET"/>
```

Finally, before you use any Lock functionality you need to configure the Lock instance. 
One way to do it would be in your Application subclass (the one that extends from `android.app.Application`), as seen in the following example:

```java
public class MyApplication extends Application {

  public void onCreate() {
    super.onCreate();

    /** Set up Lock */
    LockContext.configureLock(
      new Lock.Builder()
        .loadFromApplication(this)
        /** Other configuration goes here */
        .closable(true)
      );
  }

}
```

> You can check [here](https://auth0.com/docs/libraries/lock-android#lockbuilder) for more configuration options

## Usage

### Email/Password, Enterprise & Social authentication

`LockActivity` will handle Email/Password, Enterprise & Social authentication based on your Application's connections enabled in your Auth0's Dashboard.

When a user authenticates successfully, LockActivity will send an Action using LocalBroadcaster manager and then finish itself (by calling finish()). The activity that is interested in receiving this Action (In this case the one that will show Lock) needs to register a listener in the LocalBroadcastManager:

```java
// This activity will show Lock
public class HomeActivity extends Activity {

  private LocalBroadcastManager broadcastManager;

  private BroadcastReceiver authenticationReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      UserProfile profile = intent.getParcelableExtra("profile");
      Token token = intent.getParcelableExtra("token");
      Log.i(TAG, "User " + profile.getName() + " logged in");
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Customize your activity

    broadcastManager = LocalBroadcastManager.getInstance(this);
    broadcastManager.registerReceiver(authenticationReceiver, new IntentFilter(Lock.AUTHENTICATION_ACTION));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    broadcastManager.unregisterReceiver(authenticationReceiver);
  }
}
```

Then just start `LockActivity`

```java
Intent lockIntent = new Intent(this, LockActivity.class);
startActivity(lockIntent);
```
And you'll see our native login screen

[![Lock.png](http://blog.auth0.com.s3.amazonaws.com/Lock-Widget-Android-Screenshot.png)](https://auth0.com)

> By default all social authentication will be done using an external browser, if you want native integration please check [our docs](https://auth0.com/docs/libraries/lock-android/native-social-authentication).

### Passwordless

`LockPasswordlessActivity` authenticates users by sending them an Email or SMS (similar to how WhatsApp authenticates you). In order to be able to authenticate the user, your application must have the SMS/Email connection enabled and configured in your [dashboard](https://manage.auth0.com/#/connections/passwordless).

`LockPasswordlessActivity` is part of the library `lock-passwordless` and you can add it with this line in your `build.gradle`:
```gradle
compile 'com.auth0.android:lock-passwordless:1.12.+'
```

Then in your `AndroidManifest.xml` register the following activities:
```xml
    <!--Auth0 Lock Passwordless-->
    <activity
      android:name="com.auth0.lock.passwordless.LockPasswordlessActivity"
      android:theme="@style/Lock.Theme"
      android:label="@string/app_name"
      android:screenOrientation="portrait"
      android:launchMode="singleTask"/>
    <activity android:name="com.auth0.lock.passwordless.CountryCodeActivity" android:theme="@style/Lock.Theme"/>
    <!--Auth0 Lock Passwordless End-->
```

Just like `LockActivity`, when a user authenticates successfully, `LockPasswordlessActivity` will send an Action using `LocalBroadcastManager` and then finish itself (by calling `finish()`). The activity that is interested in receiving this `Action` (In this case the one that will show Lock) needs to register a listener in the `LocalBroadcastManager`:

```java
// This activity will show Lock
public class HomeActivity extends Activity {
  private LocalBroadcastManager broadcastManager;

  private BroadcastReceiver authenticationReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      UserProfile profile = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_PROFILE_PARAMETER);
      Token token = intent.getParcelableExtra(Lock.AUTHENTICATION_ACTION_TOKEN_PARAMETER);
      Log.i(TAG, "User " + profile.getName() + " logged in");
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    //Customize your activity

    broadcastManager = LocalBroadcastManager.getInstance(this);
    broadcastManager.registerReceiver(authenticationReceiver, new IntentFilter(Lock.AUTHENTICATION_ACTION));
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    broadcastManager.unregisterReceiver(authenticationReceiver);
  }
}
```

Then just start `LockPasswordlessActivity` especifying the passwordless type you want to use:

```java
LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.MODE_EMAIL_CODE);
```

And you'll see the Email login screen

[![Lock.png](http://blog.auth0.com.s3.amazonaws.com/Lock-Email-Android-Screenshot.png)](https://auth0.com)

Or 

```java
LockPasswordlessActivity.showFrom(MyActivity.this, LockPasswordlessActivity.MODE_SMS_CODE);
```

And you'll see the SMS login screen

[![Lock.png](http://blog.auth0.com.s3.amazonaws.com/Lock-SMS-Android-Screenshot.png)](https://auth0.com)

##Proguard
In the [proguard directory](https://github.com/auth0/Lock.Android/tree/master/proguard) you can find the *Proguard* configuration for Lock and its dependencies.
By default you should at least use the following files:
* `proguard-square-okhttp.pro`
* `proguard-jackson-2.pro`
* `proguard-square-otto.pro`
* `proguard-lock.pro`

and if you use Facebook or Google+ native integration, you'll need `proguard-facebook.pro` and `proguard-google-play-services.pro` respectively.

You specify several files in you application's `build.gradle` like this:

```gradle
buildTypes {
  release {
    minifyEnabled true
    proguardFile '../proguard/proguard-facebook.pro' //facebook native auth
    proguardFile '../proguard/proguard-google-play-services.pro' //G+ native auth
    proguardFile '../proguard/proguard-square-okhttp.pro' //Auth0 core
    proguardFile '../proguard/proguard-jackson-2.pro' //Auth0 core
    proguardFile '../proguard/proguard-square-otto.pro' //Lock
    proguardFile '../proguard/proguard-lock.pro' //Lock
    //Add your app's specific proguard rules
  }  
}
```

## Issue Reporting

If you have found a bug or if you have a feature request, please report them at this repository issues section. Please do not report security vulnerabilities on the public GitHub issue tracker. The [Responsible Disclosure Program](https://auth0.com/whitehat) details the procedure for disclosing security issues.

## What is Auth0?

Auth0 helps you to:

* Add authentication with [multiple authentication sources](https://docs.auth0.com/identityproviders), either social like **Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce, amont others**, or enterprise identity systems like **Windows Azure AD, Google Apps, Active Directory, ADFS or any SAML Identity Provider**.
* Add authentication through more traditional **[username/password databases](https://docs.auth0.com/mysql-connection-tutorial)**.
* Add support for **[linking different user accounts](https://docs.auth0.com/link-accounts)** with the same user.
* Support for generating signed [Json Web Tokens](https://docs.auth0.com/jwt) to call your APIs and **flow the user identity** securely.
* Analytics of how, when and where users are logging in.
* Pull data from other sources and add it to the user profile, through [JavaScript rules](https://docs.auth0.com/rules).

## Create a free account in Auth0

1. Go to [Auth0](https://auth0.com) and click Sign Up.
2. Use Google, GitHub or Microsoft Account to login.

## Author

Auth0

## License

Lock is available under the MIT license. See the [LICENSE file](LICENSE) for more info.
