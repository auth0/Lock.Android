# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Debugging
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable
-verbose


# For auth0-java
## Jackson 2.x
-keepattributes *Annotation*, EnclosingMethod
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

## OkHttp 2.x
-keepattributes *Annotation*, Signature
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

### Enums
-keepclassmembers class * extends java.lang.Enum {
    <fields>;
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

### Parcelables
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}

## Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**


# For Lock.Android
## Otto
-keepattributes *Annotation*
-keepclassmembers class ** {
    @com.squareup.otto.Subscribe public *;
    @com.squareup.otto.Produce public *;
}

## Data Models / POJOs
-keep class com.auth0.android.lock.utils.Strategies { *; }
-keep class com.auth0.android.lock.utils.Strategy { *; }
-keep class com.auth0.android.lock.utils.Connection { *; }
-keep class com.auth0.android.lock.utils.Application { *; }