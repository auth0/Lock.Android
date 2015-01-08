-keep class com.auth0.core.** { *; }
-dontwarn javax.annotation.**
-dontwarn sun.misc.Unsafe
-dontwarn org.w3c.dom.bootstrap.DOMImplementationRegistry
-dontwarn com.actionbarsherlock.**
-dontwarn com.google.android.maps.MapActivity
-dontwarn roboguice.fragment.RoboSherlock*
-dontwarn roboguice.activity.RoboSherlock*
-dontwarn roboguice.activity.RoboMapActivity
-dontwarn roboguice.activity.Sherlock*

-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}