![Lock for Android](https://cdn.auth0.com/website/sdks/banners/lock-android-banner.png)

[![CircleCI](https://circleci.com/gh/auth0/Lock.Android.svg?style=shield)](https://circleci.com/gh/auth0/Lock.Android)
[![License](https://img.shields.io/dub/l/vibe-d.svg?style=flat)](https://opensource.org/licenses/MIT)
[![Maven Central](https://img.shields.io/maven-central/v/com.auth0.android/lock.svg)](https://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.auth0.android%22%20AND%20a%3A%22lock%22)

📚 [Documentation](#documentation) • 🚀 [Getting Started](#getting-started) • ⏭️ [Next Steps](#next-steps) • 💬 [Feedback](#feedback)

## Documentation

- [Examples](https://github.com/auth0/auth0.android/blob/main/EXAMPLES.md)
- [Docs Site](https://auth0.com/docs/libraries/lock-android)
- [API Reference](https://javadoc.io/doc/com.auth0.android/lock/latest/index.html)

## Getting Started

### Requirements

Android API Level 21+ & Java version 8 or above is required in order to use Lock's UI.

Here’s what you need in build.gradle to target Java 8 byte code for the Android and Kotlin plugins respectively.

```groovy
android {
    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = '1.8'
    }
}
```

### Installation

Lock is available in [Maven Central](http://search.maven.org). To start using *Lock* add this line to the dependencies of your `build.gradle` file:

```groovy
implementation 'com.auth0.android:lock:3.2.1'
```

## Next Steps

If you haven't done yet, go to [Auth0](https://auth0.com) and create an Account, it's free! Then create a new [Application](https://manage.auth0.com/#/applications) of type *Native* and add a URL in *Allowed Callback URLs* with the following format:

```
https://{YOUR_AUTH0_DOMAIN}/android/{YOUR_APP_PACKAGE_NAME}/callback
```

The *package name* value required in the Callback URL can be found in your app's `build.gradle` file in the `applicationId` property. Both the *domain* and *client id* values can be found at the top of your Auth0 Application's settings. You're going to use them to setup the SDK. It's good practice to add them to the `strings.xml` file as string resources that you can reference later from the code. This guide will follow that practice.

```xml
<resources>
    <string name="com_auth0_client_id">YOUR_AUTH0_CLIENT_ID</string>
    <string name="com_auth0_domain">YOUR_AUTH0_DOMAIN</string>
</resources>
```

In your `app/build.gradle` file add the **Manifest Placeholders** for the Auth0 Domain and Auth0 Scheme properties, which are going to be used internally by the library to declare the Lock activities and register **intent-filters** that will capture the authentication result.

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

Initialize **Lock** and handle the release of its resources appropriately after you're done using it. 

```kotlin
// This activity will show Lock
class MyActivity : AppCompatActivity() {

    private lateinit var lock: Lock

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        val account = Auth0(this)
        // Instantiate Lock once
        lock = Lock.newBuilder(account, callback)
            // Customize Lock
            .build(this)
    }
    
    override fun onDestroy() {
        super.onDestroy()
        // Important! Release Lock and its resources
        lock.onDestroy(this)
    }
    
    private val callback = object : AuthenticationCallback() {
        override fun onAuthentication(credentials: Credentials) {
            // Authenticated
        }
        
        override fun onError(error: AuthenticationException) {
            // An exception occurred
        }
    }
}
```

Start `LockActivity` from inside your `Activity`. For this, create a new intent from the Lock instance and launch it.

```kotlin
startActivity(lock.newIntent(this))
```

### Customizing the widget

When using the `Builder` to instantiate the widget, you can pass different options to customize how it will behave. Some options are only available for **Lock** or **PasswordlessLock**. Below you will find a few of them. You can always explore all the available options with your IDE's auto-complete shortcut. Check the Javadocs to understand the default values.


```kotlin
// Create a new builder from Lock or LockPasswordless classes
newBuilder(account, callback)
    // Shared options
    .closable(true) // Allows the widget to be closed with the back button
    .withScope('new-scope') // Changes the scope to be requested on authentication
    .withAudience('my-api') // Changes the audience to be requested on authentication
    .withScheme('myapp') // Changes the scheme part used to generate the Callback URL (more below)
    
    // Lock specific options
    .initialScreen(InitialScreen.SIGN_UP) // Allows to choose the screen to be displayed first 
    .allowLogIn(false) // Disables the Log In screen
    .allowSignUp(false) // Disables the Sign Up screen
    .allowForgotPassword(false) // Disables the Change Password screen
    .setDefaultDatabaseConnection('my-connection') // When multiple are available, select one
    
    // PasswordlessLock specific options
    .useCode(true)  // Requests to receive a OTP that will need to be filled in your android app to authenticate the user
    .useLink(false) // Requests to receive a link that will open your android app to authenticate the user
    .rememberLastLogin(true) // Saves the email or phone number to avoid re-typing it in the future
    
    // Build the instance
    .build(this)
```

## Feedback

### Contributing

We appreciate feedback and contribution to this repo! Before you get started, please see the following:

- [Auth0's general contribution guidelines](https://github.com/auth0/open-source-template/blob/master/GENERAL-CONTRIBUTING.md)
- [Auth0's code of conduct guidelines](https://github.com/auth0/open-source-template/blob/master/CODE-OF-CONDUCT.md)

### Raise an issue
To provide feedback or report a bug, [please raise an issue on our issue tracker](https://github.com/auth0/Lock.Android/issues).

### Vulnerability Reporting
Please do not report security vulnerabilities on the public Github issue tracker. The [Responsible Disclosure Program](https://auth0.com/whitehat) details the procedure for disclosing security issues.

---

<p align="center">
  <picture>
    <source media="(prefers-color-scheme: light)" srcset="https://cdn.auth0.com/website/sdks/logos/auth0_light_mode"   width="150">
    <source media="(prefers-color-scheme: dark)" srcset="https://cdn.auth0.com/website/sdks/logos/auth0_dark_mode.png" width="150">
    <img alt="Auth0 Logo" src="https://cdn.auth0.com/website/sdks/logos/auth0_light_mode.png" width="150">
  </picture>
</p>
<p align="center">Auth0 is an easy to implement, adaptable authentication and authorization platform. To learn more checkout <a href="https://auth0.com/why-auth0">Why Auth0?</a></p>
<p align="center">
This project is licensed under the MIT license. See the <a href="https://github.com/auth0/lock.android/blob/main/LICENSE.md"> LICENSE</a> file for more info.</p>