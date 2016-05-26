# Lock.Android v2

## Enums
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

## Parcelables
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

## Data Models / POJOs
-keep class com.auth0.android.lock.utils.Strategies { *; }
-keep class com.auth0.android.lock.utils.Strategy { *; }
-keep class com.auth0.android.lock.utils.Connection { *; }
-keep class com.auth0.android.lock.utils.Application { *; }