# Lock.Android v2

## Parcelables
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

## Descriptor classes
-keep public class com.auth0.android.lock.events.*
-keep public class com.auth0.android.lock.adapters.Country
-keep public interface com.auth0.android.lock.internal.configuration.OAuthConnection
-keep public interface com.auth0.android.lock.views.interfaces.IdentityListener