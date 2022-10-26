# Examples using Lock.Android

- [Examples using Lock.Android](#examples-using-lockandroid)
  - [Passwordless & Social authentication](#passwordless--social-authentication)
  - [Android App Links - Custom Scheme](#android-app-links---custom-scheme)
  - [Using Proguard](#using-proguard)


## Passwordless & Social authentication

The Passwordless feature requires your Application to have the *Passwordless OTP* Grant Type enabled first. Check [this article](https://auth0.com/docs/clients/client-grant-types) to learn how to enable it. 

`PasswordlessLockActivity` authenticates users by sending them an Email or SMS (similar to how WhatsApp authenticates you). In order to be able to authenticate the user, your application must have the SMS/Email connection enabled and configured in your [dashboard](https://manage.auth0.com/#/connections/passwordless).

Initialize **PasswordlessLock** and handle the release of its resources appropriately after you're doing using it. 

```kotlin
// This activity will show PasswordlessLock
class MyActivity : AppCompatActivity() {
    
    private lateinit var lock: PasswordlessLock
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    
        val account = Auth0(this)
        // Instantiate Lock once
        lock = PasswordlessLock.newBuilder(account, callback)
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

Start `PasswordlessLockActivity` from inside your `Activity`. For this, create a new intent and launch it.

```kotlin
startActivity(lock.newIntent(this))
```

## Android App Links - Custom Scheme

The default scheme used by the library to generate the Callback URL for Web Authentication is `https`. This works best for Android Marshmallow (API 23) or newer if you're using [Android App Links](https://developer.android.com/training/app-links/index.html). However, in previous Android versions, this may show the disambiguation dialog prompting the user to choose either your application or the browser to resolve the intent. You can change this behavior by using a unique custom scheme so that the OS opens the link directly with your app.

1. Update the `auth0Scheme` Manifest Placeholder value in the `app/build.gradle` file or directly in the Intent Filter definition in the `AndroidManifest.xml` file by changing the existing scheme to the new one.
2. Update the "Allowed Callback URLs" in your [Auth0 Dashboard](https://manage.auth0.com/#/clients) Application's settings to match URLs that begin with the new scheme.
3. Call `withScheme()` in the Lock.Builder/PasswordlessLock.Builder passing the scheme you want to use.

> The scheme value **must** be all lowercase. A warning message will be logged if this is not the case and authentication will never complete.

## Using Proguard

The rules should be applied automatically if your application is using `minifyEnabled = true`. If you want to include them manually check the [proguard directory](proguard).
By default you should at least use the following files:
* `proguard-gson.pro`
* `proguard-otto.pro`
* `proguard-lock-2.pro`

As this library depends on `Auth0.Android`, you should keep the files up to date with the proguard rules defined in the SDK [repository](https://github.com/auth0/Auth0.Android).