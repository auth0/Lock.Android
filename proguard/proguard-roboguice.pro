-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
-renamesourcefileattribute SourceFile
-keepattributes SourceFile,LineNumberTable,*Annotation*,Signature

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.app.Fragment
-keep public class * extends android.support.v4.app.Fragment
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends com.google.inject.AbstractModule
-keep public class com.android.vending.licensing.ILicensingService
-keep class com.google.inject.** { *; }
-keep class javax.inject.** { *; }
-keep class javax.annotation.** { *; }
-keep class roboguice.** { *; }
-keepclasseswithmembers class * { native <methods>; }
-keepclasseswithmembers class * {  
    public <init> (android.content.Context, android.util.AttributeSet); 
}
-keepclasseswithmembers class * { 
    public <init> (android.content.Context, android.util.AttributeSet, int); 
}
-keepclassmembers class * implements android.os.Parcelable { static android.os.Parcelable$Creator *; }
-keepclassmembers class **.R$* { public static <fields>; }
-keepclasseswithmembernames class * { native <methods>; }
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keepclassmembers class * extends android.app.Activity { 
	public void *(android.view.View); 
}
-keepclassmembers class * { 
	@com.google.inject.Inject <init>(...);
	@com.google.inject.Inject <fields>;
	@javax.annotation.Nullable <fields>;
}
-keepclassmembers class * {
	void *(net.eworldui.videouploader.events.*);
}
-keepclassmembers class * {
	void *(roboguice.activity.event.*);
}