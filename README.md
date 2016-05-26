# Lock V2

**Lock.Android** configuration and usage has changed from v1.


## Configuration


### Dependencies

Until a beta/stable release is published on JCenter repository, you need to add the library as a Git Submodule. Follow the steps:

In a Terminal window run:
```sh
# Change directory to your Android project folder
cd my/android/project/folder

# Clone and init the submodule
git submodule add git@github.com:auth0/Lock.Android.git lock

# Change directory to the submodule
cd lock

# Checkout the v2 development branch
git checkout v2
```

Open your project's `settings.gradle` file and add the submodule:

```java
include ':lock'
project(':lock').projectDir = new File(rootProject.projectDir, '/lock/lib')
```

Open your apps's `build.gradle` file and add the library dependency:

```java
compile project(':lock')
```

Finally, sync your Gradle project to update the dependencies.




### AndroidManifest.xml

Add the following permissions to the Manifest

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

The first one allows **Lock** to make requests to the Auth0 API.
The second one _is not mandatory_, but allows **Lock** to check for network availability before making any further requests.

Add `LockActivity` to your Manifest, replacing the `AUTH0_DOMAIN` in the host attribute with your `tenant.auth0.com`.

```xml
<activity
  android:name="com.auth0.android.lock.LockActivity"
  android:label="@string/app_name"
  android:launchMode="singleTask"
  android:screenOrientation="portrait"
  android:theme="@style/MyLock.Theme">
    <intent-filter>
      <action android:name="android.intent.action.VIEW" />

      <category android:name="android.intent.category.DEFAULT" />
      <category android:name="android.intent.category.BROWSABLE" />

      <data
        android:host="AUTH0_DOMAIN"
        android:pathPrefix="/android/com.auth0.android.lock.app/callback"
        android:scheme="https" />
    </intent-filter>
</activity>
```

#### Some restrictions

If you're going to use our `OAuth2WebProvider` with Browser instead of WebView, be sure to specify in the manifest that `LockActivity` should be launched in _singleTask_ mode.

If you forget this mode, and the code is running on devices with Android version above KITKAT, an error will raise in the console and the Activity won't launch. This is to sort the way Android handles calling an existing Activity with a result. Previous versions of Android are also affected by this issue, but won't get the warning and can crash if it's not properly handled.

Also note that you can't use Browser and launch the `LockActivity` calling `startActivityForResult` at the same time.

### Lock instance

In the previous version of **Lock**, you were asked to create a custom `Application` class and initialize the `Lock.Context` there. Now this is no longer needed. To create a new `Lock` instance and configure it, use the `Lock.Builder` class.

#### Auth0
Create an `Auth0` instance to hold your account details, which are the `AUTH0_CLIENT_ID` and the `AUTH0_DOMAIN`.

```java
Auth0 auth0 = new Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN);
```

#### Authentication Callback
You'll also need a `LockCallback` implementation. We suggest you to extend the `AuthenticationCallback` class and override the `onAuthentication`, `onError` and `onCanceled` methods. Keep in mind that this implementation only notifies you about _Authentication_ events (logins), not _User Sign Ups_ (without login) nor _Password Resets_. If you feel adventurous, you can implement the `LockCallback` interface yourself an override the `onError` and `onEvent` methods. Take a look at the source code to see which kind of events you'll receive and how to get information from them.

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

#### Lock.Builder
Call the static method `Lock.newBuilder(Auth0, AuthenticationCallback)` passing the account details and the callback implementation, and start configuring the [Options](#options). After you're done, build the `Lock` instance and use it to start the `LockActivity`.

This is how your activity should look like.

```java
public class MainActivity extends Activity {
  public static final int AUTH_REQUEST = 333;
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

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == AUTH_REQUEST) {
      lock.onActivityResult(this, resultCode, data);
      return;
    }

    super.onActivityResult(requestCode, resultCode, data);
  }

  private void performLogin(boolean useBrowser) {
    if (useBrowser) {
      startActivity(lock.newIntent(this));
    } else {
      startActivityForResult(lock.newIntent(this), AUTH_REQUEST);
    }
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

>Remember to notify the `LockActivity` on every `OnCreate`, `OnDestroy` and `OnActivityResult` call on your Activity, as it helps to keep the Lock state.

That's it! **Lock** will handle the rest for you.


### Options

As in the previous version, `Lock` can be configured with extra options. Check below if the behavior changed or if they only got renamed.

1. `shouldUseWebView` has been refactored. Now it's called `useBrowser` as **Lock** will use the WebView by default instead of the Android Browser, to request calls to the `/authorize` endpoint. Using the Browser has some [restrictions](#some-restrictions).
1. `shouldUseEmail` has been refactored. Now it's called `withUsernameStyle` and defines if it should ask for email only, username only, or both of them. By default, it'll respect the Dashboard configuration of the parameter `requires_username`.
1. `isClosable` has been renamed to `closable` By default, it's not closable.
1. `setFullscreen` has been renamed to `fullscreen`. By default, it's not fullscreen.
1. `shouldLoginAfterSignUp` has been renamed to `loginAfterSignUp`.
1. `disableSignupAction` has been renamed to `disableSignUp`.
1. `disableResetAction` has been renamed to `disableChangePassword`.
1. `defaultUserPasswordConnection` has been renamed to `setDefaultDatabaseConnection`.
1. `setConnections` has been renamed to `onlyUseConnections`.
1. `setAuthenticationParameters` has been renamed to `withAuthenticationParameters`.
