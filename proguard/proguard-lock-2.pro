# Lock.Android v2

## Parcelables
-keepnames class * implements android.os.Parcelable {
    public static final ** CREATOR;
}