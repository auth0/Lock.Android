Lock for Android
============
[![CircleCI](https://img.shields.io/circleci/project/github/auth0/Lock.Android.svg?style=flat-square)](https://circleci.com/gh/auth0/Lock.Android/tree/master)
[![License](http://img.shields.io/:license-mit-blue.svg?style=flat)](http://doge.mit-license.org)
[![Maven Central](https://img.shields.io/maven-central/v/com.auth0.android/lock.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.auth0.android%22%20AND%20a%3A%22lock%22)
[![Download](https://api.bintray.com/packages/auth0/android/lock/images/download.svg)](https://bintray.com/auth0/android/lock/_latestVersion)

[Auth0](https://auth0.com) is an authentication broker that supports social identity providers as well as enterprise identity providers such as Active Directory, LDAP, Google Apps and Salesforce.

## Key features

* **Integrates** your Android app with **Auth0**.
* Provides a **beautiful native UI** to log your users in.
* Provides support for **Social Providers** (Facebook, Twitter, etc.), **Enterprise Providers** (AD, LDAP, etc.) and **Username & Password**.
* Passwordless authentication using **SMS** and **Email**.


## Notes On Embedded Authentication

Since June 2017 new Applications no longer have the **Password Grant Type*** enabled by default. If you are using a Database Connection in Lock then you will need to enable the Password Grant Type, please follow [this guide](https://auth0.com/docs/clients/client-grant-types#how-to-edit-the-client-grant_types-property). The reasons for this change are listed in this [embedded vs native mobile article](https://auth0.com/docs/tutorials/browser-based-vs-native-experience-on-mobile).


## Requirements

Android API Level 15+ is required in order to use Lock's UI.

## Install

Lock is available both in [Maven Central](http://search.maven.org) and [JCenter](https://bintray.com/bintray/jcenter). To start using *Lock* add these lines to your `build.gradle` dependencies file:

```gradle
compile 'com.auth0.android:lock:2.8.5'
```

## Usage

If you haven't done yet, go to [Auth0](https://auth0.com) and create an Account, it's free! Then create a new [Application](https://manage.auth0.com/#/applications) of type *Native* and add a URL in *Allowed Callback URLs* with the following format:

```
https://{YOUR_AUTH0_DOMAIN}/android/{YOUR_APP_PACKAGE_NAME}/callback
```

The *package name* value required in the Callback URL can be found in your app's `build.gradle` file in the `applicationId` property. Both the *domain* and *client id* values can be found at the top of your Application's settings. You're going to use them to setup the SDK so let's add them to the `strings.xml` file:

```xml
<resources>
    <string name="com_auth0_client_id">YOUR_AUTH0_CLIENT_ID</string>
    <string name="com_auth0_domain">YOUR_AUTH0_DOMAIN</string>
</resources>
```

In your app/build.gradle file add the **Manifest Placeholders** for the Auth0 Domain and Auth0 Scheme properties which are going to be used internally by the library to register an **intent-filter**. You can also add the intent-filter manually to the corresponding Lock activity in the Android Manifest as described later.

```groovy
apply plugin: 'com.android.application'

android {
    compileSdkVersion 25
    defaultConfig {
        applicationId "com.auth0.samples"
        minSdkVersion 15
        targetSdkVersion 25
        //...

        //---> Add the next line
        manifestPlaceholders = [auth0Domain: "@string/com_auth0_domain", auth0Scheme: "https"]
        //<---
    }
    //...
}
```

The next step is to create an instance of `Auth0` with your applications information:

```java
Auth0 account = new Auth0("{YOUR_AUTH0_CLIENT_ID}", "{YOUR_AUTH0_DOMAIN}");
```

Another way to create it is using the values defined previously in the `string.xml` file, by passing an Android Context. The name of the keys must match the ones listed above or the constructor will throw an exception.

```java
Auth0 account = new Auth0(context);
```

## OIDC Conformant Mode

It is strongly encouraged that Lock be used in OIDC Conformant mode. When this mode is enabled, it will force Lock to use Auth0's current authentication pipeline and will prevent it from reaching legacy endpoints. By default is `false`.

```java
Auth0 account = new Auth0("{YOUR_AUTH0_CLIENT_ID}", "{YOUR_AUTH0_DOMAIN}");
//Configure the account in OIDC conformant mode
account.setOIDCConformant(true);
//Use the account to launch Lock
```

Passwordless Lock *cannot be used* with this flag set to `true`. For more information, please see the [OIDC adoption guide](https://auth0.com/docs/api-auth/tutorials/adoption).


### Email/Password, Enterprise & Social authentication

Modify the `AndroidManifest.xml` file, to include the Internet permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Next, add the `LockActivity` inside the `application` tag:

```xml
<activity
  android:name="com.auth0.android.lock.LockActivity"
  android:label="@string/app_name"
  android:launchMode="singleTask"
  android:screenOrientation="portrait"
  android:theme="@style/Lock.Theme"/>
```

> In versions 2.5.0 or lower of Lock.Android you had to define an **intent-filter** inside the `LockActivity` to make possible to the library to capture a social provider's authentication result. This intent-filter declaration is no longer required for versions greater than 2.5.0, as it's now done internally by the library for you.

In case you are using an older version of Lock for **Social** Authentication, the **intent-filter** must be added to the `LockActivity` by you.

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
        android:host="@string/com_auth0_domain"
        android:pathPrefix="/android/${applicationId}/callback"
        android:scheme="https" />
    </intent-filter>
</activity>
```


Make sure the Activity's `launchMode` is declared as `singleTask` or the result won't come back in the authentication.


Then in any of your Activities you need to initialize **Lock**. If your `auth0Scheme` value is not `https` you need to use `withScheme()` as shown below.

```java
// This activity will show Lock
public class HomeActivity extends Activity {

  private Lock lock;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Your own Activity code
    Auth0 account = new Auth0("YOUR_AUTH0_CLIENT_ID", "YOUR_AUTH0_DOMAIN");
    account.setOIDCConformant(true);
    lock = Lock.newBuilder(account, callback)
      //Customize Lock
      //.withScheme("myapp")
      .build(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // Your own Activity code
    lock.onDestroy(this);
    lock = null;
  }

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
     public void onError(LockException error) {
        //Exception occurred
     }
  };
}
```

Start `LockActivity` from inside your `Activity`.

```java
startActivity(lock.newIntent(this));
```

### Passwordless & Social authentication

The Passwordless feature requires your Application to have the *Resource Owner* Legacy Grant Type enabled. Check [this article](https://auth0.com/docs/clients/client-grant-types) to learn how to enable it. Note that Passwordless authentication *cannot be used* with the [OIDC Conformant Mode](#oidc-conformant-mode) enabled.

`PasswordlessLockActivity` authenticates users by sending them an Email or SMS (similar to how WhatsApp authenticates you). In order to be able to authenticate the user, your application must have the SMS/Email connection enabled and configured in your [dashboard](https://manage.auth0.com/#/connections/passwordless).


Modify the `AndroidManifest.xml` file, to include the Internet permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Next, add the `PasswordlessLockActivity` inside the `application` tag:

```xml
<activity
  android:name="com.auth0.android.lock.PasswordlessLockActivity"
  android:label="@string/app_name"
  android:launchMode="singleTask"
  android:screenOrientation="portrait"
  android:theme="@style/Lock.Theme">
    <intent-filter>
      <action android:name="android.intent.action.VIEW" />

      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />

      <data
        android:host="@string/com_auth0_domain"
        android:pathPrefix="/android/${applicationId}/email"
        android:scheme="https" />
    </intent-filter>
</activity>
```

The `data` attribute of the intent-filter defines which syntax of "Callback URI" your app is going to capture. In the above case it's going to capture calls from `email` passwordless connections. In case you're using the `sms` passwordless connection, the `pathPrefix` would end in `sms`.

> In versions 2.5.0 or lower of Lock.Android you had to define an **intent-filter** inside the `PasswordlessLockActivity` to make possible to the library to capture a **Social** provider's authentication result. This intent-filter declaration is no longer required for versions greater than 2.5.0, as it's now done internally by the library for you.

In case you are using an older version of Lock for **Social** Authentication, the **data** attribute inside the intent-filter must be added to the `PasswordlessLockActivity` by you.

```xml
<activity
  android:name="com.auth0.android.lock.PasswordlessLockActivity"
  android:label="@string/app_name"
  android:launchMode="singleTask"
  android:screenOrientation="portrait"
  android:theme="@style/Lock.Theme">
    <intent-filter>
      <action android:name="android.intent.action.VIEW" />

      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />

      <data
        android:host="@string/com_auth0_domain"
        android:pathPrefix="/android/${applicationId}/email"
        android:scheme="https" />

      <data
        android:host="@string/com_auth0_domain"
        android:pathPrefix="/android/${applicationId}/callback"
        android:scheme="https" />
    </intent-filter>
</activity>
```

Make sure the Activity's `launchMode` is declared as `singleTask` or the result won't come back in the authentication.

When the Passwordless connection is SMS you must also add the `CountryCodeActivity` to allow the user to change the **Country Code** prefix of the phone number.

```xml
<activity
  android:name="com.auth0.android.lock.CountryCodeActivity"
  android:theme="@style/Lock.Theme.ActionBar" />
```


Then in any of your Activities you need to initialize **PasswordlessLock**. If your `auth0Scheme` value is not `https` you need to use `withScheme()` as shown below.

```java
// This activity will show Lock
public class HomeActivity extends Activity {

  private PasswordlessLock lock;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Your own Activity code
    Auth0 account = new Auth0("YOUR_AUTH0_CLIENT_ID", "YOUR_AUTH0_DOMAIN");
    account.setOIDCConformant(true);
    lock = PasswordlessLock.newBuilder(account, callback)
      //Customize Lock
      //.withScheme("myapp")
      .build(this);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    // Your own Activity code
    lock.onDestroy(this);
    lock = null;
  }

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
     public void onError(LockException error) {
        //Exception occurred
     }
  };
}
```

Start `PasswordlessLockActivity` from inside your `Activity`.

```java
startActivity(lock.newIntent(this));
```

#### Android App Links - Custom Scheme
The current default scheme used by the library to generate the Redirect URL for Web Authentication is `https`. This works best for Android Marshmallow (API 23) or newer if you're using [Android App Links](https://developer.android.com/training/app-links/index.html). However, in previous Android versions this may show the intent chooser dialog prompting the user to choose either your application or the browser to resolve the intent. You can change this behavior by using a custom unique scheme, so that the OS opens the link directly with your app.

1. Update the `auth0Scheme` Manifest Placeholder value in the `app/build.gradle` file or the Intent Filter definition in the `AndroidManifest.xml` file by changing the existing scheme to the new one.
2. Update the Allowed Callback URLs in your [Auth0 Dashboard](https://manage.auth0.com/#/clients) Applications's settings to match URLs that begin with the new scheme.
3. Call `withScheme()` in the Lock.Builder/PasswordlessLock.Builder passing the scheme you want to use.

> The scheme value **must** be lowercase. A warning message will be logged if this is not the case.

## Proguard
The rules should be applied automatically if your application is using `minifyEnabled = true`. If you want to include them manually check the [proguard directory](proguard).
By default you should at least use the following files:
* `proguard-gson.pro`
* `proguard-otto.pro`
* `proguard-lock-2.pro`

As this library depends on `Auth0.Android`, you should keep the files up to date with the proguard rules defined in the [repository](https://github.com/auth0/Auth0.Android).

## What is Auth0?

Auth0 helps you to:

* Add authentication with [multiple authentication sources](https://docs.auth0.com/identityproviders), either social like **Google, Facebook, Microsoft Account, LinkedIn, GitHub, Twitter, Box, Salesforce, among others**, or enterprise identity systems like **Windows Azure AD, Google Apps, Active Directory, ADFS or any SAML Identity Provider**.
* Add authentication through more traditional **[username/password databases](https://docs.auth0.com/mysql-connection-tutorial)**.
* Add support for **[linking different user accounts](https://docs.auth0.com/link-accounts)** with the same user.
* Support for generating signed [Json Web Tokens](https://docs.auth0.com/jwt) to call your APIs and **flow the user identity** securely.
* Analytics of how, when and where users are logging in.
* Pull data from other sources and add it to the user profile, through [JavaScript rules](https://docs.auth0.com/rules).

## Create a free account in Auth0

1. Go to [Auth0](https://auth0.com) and click Sign Up.
2. Use Google, GitHub or Microsoft Account to login.

## Issue Reporting

If you have found a bug or if you have a feature request, please report them at this repository issues section. Please do not report security vulnerabilities on the public GitHub issue tracker. The [Responsible Disclosure Program](https://auth0.com/whitehat) details the procedure for disclosing security issues.

## Author

[Auth0](https://auth0.com)

## License

This project is licensed under the MIT license. See the [LICENSE](LICENSE) file for more info.
