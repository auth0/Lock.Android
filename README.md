Lock for Android
============
[![CI Status](http://img.shields.io/travis/auth0/Lock.Android.svg?style=flat)](https://travis-ci.org/auth0/Lock.Android)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.auth0.android/lock/badge.svg?style=flat)](https://maven-badges.herokuapp.com/maven-central/com.auth0.android/lock)

[Auth0](https://auth0.com) is an authentication broker that supports social identity providers as well as enterprise identity providers such as Active Directory, LDAP, Google Apps and Salesforce.

## Key features

* **Integrates** your Android app with **Auth0**.
* Provides a **beautiful native UI** to log your users in.
* Provides support for **Social Providers** (Facebook, Twitter, etc.), **Enterprise Providers** (AD, LDAP, etc.) and **Username & Password**.
* Passwordless authentication using **SMS**.

## Documentation
You can find the full documentation for this library on the [Auth0 documentation site](https://auth0.com/docs/libraries/lock-android).

## Requirements

Android API level 14+ is required in order to use Lock's UI.
If you'll create your own API and just call Auth0 API via the `com.auth0.android:core:1.7.+`, the minimum required API level is 9.

##Install

Lock is available both in [Maven Central](http://search.maven.org) and [JCenter](https://bintray.com/bintray/jcenter). To start using *Lock* add these lines to your `build.gradle` dependencies file:

```gradle
compile 'com.auth0.android:lock:1.7.+'
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
    <data android:scheme="a0INSERT_YOUR_APP_CLIENT_ID" android:host="YOUR_ACCOUNT_NAME.auth0.com"/>
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

Finally, Make your Application class (The one that extends from `android.app.Application`) implement the interface `com.auth0.lock.LockProvider` and add the following code:

```java
public class MyApplication extends Application implements LockProvider {

  private Lock lock;

  public void onCreate() {
    super.onCreate();
    lock = new LockBuilder()
      .loadFromApplication(this)
      /** Other configuration goes here */
      .closable(true)
      .build();
  }

  @Override
  public Lock getLock() {
    return lock;
  }
}
```

> You can check [here](#lockbuilder) for more configuration options

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

> By default all social authentication will be done using an external browser, if you want native integration please check this [wiki page](https://github.com/auth0/Lock.Android/wiki/Native-Social-Authentication).

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
