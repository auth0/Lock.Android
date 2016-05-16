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

-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontskipnonpubliclibraryclassmembers
-dontpreverify

#DEBUG
-renamesourcefileattribute SourceFile
-keepattributes SourceFile, LineNumberTable
-printmapping proguard-mapping.txt
-verbose


# For auth0-java
## Jackson 2.x
-keepattributes *Annotation*,EnclosingMethod
-keep class com.fasterxml.jackson.databind.ObjectMapper {
    public <methods>;
    protected <methods>;
}
-keep class com.fasterxml.jackson.databind.ObjectWriter {
    public ** writeValueAsString(**);
}
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry

## OkHttp 2.x
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

## Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**

# For Lock.Android
## Support AppCompat v7
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }

-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

## Support Design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

## Models
-keep class com.auth0.android.lock.** { *; }
-keep class com.auth0.android.lock.utils.Application.** { *; }
-keep class com.auth0.android.lock.utils.Strategy.** { *; }
-keep class com.auth0.android.lock.utils.Connection.** { *; }

## Parcelables
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}