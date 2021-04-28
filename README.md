Lock for Android
============
[![CircleCI](https://circleci.com/gh/auth0/Lock.Android.svg?style=shield)](https://circleci.com/gh/auth0/Lock.Android)
[![License](https://img.shields.io/dub/l/vibe-d.svg?style=flat)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/com.auth0.android/lock.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.auth0.android%22%20AND%20a%3A%22lock%22)
[![Download](https://api.bintray.com/packages/auth0/android/lock/images/download.svg)](https://bintray.com/auth0/android/lock/_latestVersion)

[Auth0](https://auth0.com) is an authentication broker that supports social identity providers as well as enterprise identity providers such as Active Directory, LDAP, Google Apps and Salesforce.

## Key features

* **Integrates** your Android app with **Auth0**.
* Provides a **beautiful native UI** to log your users in.
* Provides support for **Social Providers** (Facebook, Twitter, etc.), **Enterprise Providers** (AD, LDAP, etc.) and **Username & Password**.
* Provides Passwordless authentication using **SMS** and **Email**.


## Notes On Embedded Authentication

Since June 2017 new Applications no longer have the **Password Grant Type** enabled by default. If you are using a Database Connection in Lock then you will need to enable the Password Grant Type, please follow [this guide](https://auth0.com/docs/clients/client-grant-types#how-to-edit-the-client-grant_types-property). The reasons for this change are listed in this [embedded vs native mobile article](https://auth0.com/docs/tutorials/browser-based-vs-native-experience-on-mobile).


## Requirements

Android API Level 21+ is required in order to use Lock's UI.

## Install

Lock is available in [Maven Central](http://search.maven.org). To start using *Lock* add this line to the dependencies of your `build.gradle` file:

```groovy
implementation 'com.auth0.android:lock:3.0.0'
```

## Usage

If you haven't done yet, go to [Auth0](https://auth0.com) and create an Account, it's free! Then create a new [Application](https://manage.auth0.com/#/applications) of type *Native* and add a URL in *Allowed Callback URLs* with the following format:

```
https://{YOUR_AUTH0_DOMAIN}/android/{YOUR_APP_PACKAGE_NAME}/callback
```

The *package name* value required in the Callback URL can be found in your app's `build.gradle` file in the `applicationId` property. Both the *domain* and *client id* values can be found at the top of your Auth0 Application's settings. You're going to use them to setup the SDK. It's a good practice to add them to the `strings.xml` file as string resources that you can reference later from the code. This guide will follow that practice.

```xml
<resources>
    <string name="com_auth0_client_id">YOUR_AUTH0_CLIENT_ID</string>
    <string name="com_auth0_domain">YOUR_AUTH0_DOMAIN</string>
</resources>
```

In your `app/build.gradle` file add the **Manifest Placeholders** for the Auth0 Domain and Auth0 Scheme properties, which are going to be used internally by the library to register an **intent-filter** that will capture the authentication result.

```groovy
apply plugin: 'com.android.application'

android {
    compileSdkVersion 30
    defaultConfig {
        applicationId "com.auth0.samples"
        minSdkVersion 21
        targetSdkVersion 30
        //...

        //---> Add the next line
        manifestPlaceholders = [auth0Domain: "@string/com_auth0_domain", auth0Scheme: "https"]
        //<---
    }
    //...
}
```

The next step is to create an instance of `Auth0` with your application's information. The easiest way to create it is by using the values defined previously in the `strings.xml` file and passing an Android Context. For this to work, you must have defined the string resources using the same keys as listed above.

```kotlin
val account = Auth0(context)
```

Alternatively, you can directly pass the values.

```kotlin
val account = Auth0("{YOUR_AUTH0_CLIENT_ID}", "{YOUR_AUTH0_DOMAIN}")
```

Or, if you are using _custom domains_ and are required to specify a different URL to fetch the Lock widget configuration from, you can use the constructor that takes 3 parameters:

```kotlin
val account = Auth0("{YOUR_AUTH0_CLIENT_ID}", "{YOUR_AUTH0_DOMAIN}", "{THE_CONFIGURATION_DOMAIN}")
```


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

Make sure the Activity's `launchMode` is declared as `singleTask` or the authentication result won't come back into your application.


Then, in any of your Activities, you need to initialize **Lock** and handle the release of its resources appropriately after you're done using it. 

```kotlin
// This activity will show Lock
class MyActivity : AppCompatActivity() {

  private var lock: Lock? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val account = Auth0(this)
    lock = Lock.newBuilder(account, callback)
      // Customize Lock
      // .withScheme("myapp")
      .build(this)
  }

  override fun onDestroy() {
      super.onDestroy()
      lock?.onDestroy(this)
  }

  private val callback = object : AuthenticationCallback() {
       override fun onAuthentication(credentials: Credentials) {
            // Authenticated
       }

       override fun onCanceled() {
            // User pressed back and closed Lock
       }

       override fun onError(error: LockException) {
            // An exception occurred
       }
  }
}
```

To start `LockActivity` from inside your `Activity`, create a new intent and launch it.

```kotlin
startActivity(lock.newIntent(this))
```

### Passwordless & Social authentication

The Passwordless feature requires your Application to have the *Passwordless OTP* Grant Type enabled first. Check [this article](https://auth0.com/docs/clients/client-grant-types) to learn how to enable it. 

`PasswordlessLockActivity` authenticates users by sending them an Email or SMS (similar to how WhatsApp authenticates you). In order to be able to authenticate the user, your application must have the SMS/Email connection enabled and configured in your [dashboard](https://manage.auth0.com/#/connections/passwordless).


Modify the `AndroidManifest.xml` file, to include the Internet permission:

```xml
<uses-permission android:name="android.permission.INTERNET" />
```

Next, add the `PasswordlessLockActivity` inside the `application` tag. Note that this time, you must define an intent-filter that matches the passwordless callback URL:

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

The `data` attribute of the intent-filter defines which format of "Callback URL" your app is going to capture. In the above case, it's going to capture calls from `email` passwordless connections. In case you're using the `sms` passwordless connection, the `pathPrefix` should end in `sms` instead.

Make sure the Activity's `launchMode` is declared as `singleTask` or the result won't come back in the authentication.

When the Passwordless connection is SMS you must also add the `CountryCodeActivity` to allow the user to change the **Country Code** prefix of the phone number.

```xml
<activity
  android:name="com.auth0.android.lock.CountryCodeActivity"
  android:theme="@style/Lock.Theme.ActionBar" />
```


Then, in any of your Activities, you need to initialize **PasswordlessLock** and handle the release of its resources appropriately after you're doing using it. 

```kotlin
// This activity will show PasswordlessLock
class MyActivity : AppCompatActivity() {

  private var lock: PasswordlessLock? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    val account = Auth0(this)
    lock = PasswordlessLock.newBuilder(account, callback)
      // Customize Lock
      // .withScheme("myapp")
      .build(this)
  }

  override fun onDestroy() {
      super.onDestroy()
      lock?.onDestroy(this)
  }

  private val callback = object : AuthenticationCallback() {
       override fun onAuthentication(credentials: Credentials) {
            // Authenticated
       }

       override fun onCanceled() {
            // User pressed back and closed Lock
       }

       override fun onError(error: LockException) {
            // An exception occurred
       }
  }
}
```

To start `PasswordlessLockActivity` from inside your `Activity`, create a new intent and launch it.

```kotlin
startActivity(lock.newIntent(this))
```

#### Android App Links - Custom Scheme
The default scheme used by the library to generate the Callback URL for Web Authentication is `https`. This works best for Android Marshmallow (API 23) or newer if you're using [Android App Links](https://developer.android.com/training/app-links/index.html). However, in previous Android versions, this may show the disambiguation dialog prompting the user to choose either your application or the browser to resolve the intent. You can change this behavior by using a unique custom scheme so that the OS opens the link directly with your app.

1. Update the `auth0Scheme` Manifest Placeholder value in the `app/build.gradle` file or directly in the Intent Filter definition in the `AndroidManifest.xml` file by changing the existing scheme to the new one.
2. Update the "Allowed Callback URLs" in your [Auth0 Dashboard](https://manage.auth0.com/#/clients) Application's settings to match URLs that begin with the new scheme.
3. Call `withScheme()` in the Lock.Builder/PasswordlessLock.Builder passing the scheme you want to use.

> The scheme value **must** be all lowercase. A warning message will be logged if this is not the case and authentication will never complete.

## Proguard
The rules should be applied automatically if your application is using `minifyEnabled = true`. If you want to include them manually check the [proguard directory](proguard).
By default you should at least use the following files:
* `proguard-gson.pro`
* `proguard-otto.pro`
* `proguard-lock-2.pro`

As this library depends on `Auth0.Android`, you should keep the files up to date with the proguard rules defined in the SDK [repository](https://github.com/auth0/Auth0.Android).

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

This project is licensed under the MIT license. See the [LICENSE](LICENSE.md) file for more info.
