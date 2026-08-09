# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ── Hilt ──
-dontwarn dagger.hilt.**
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.lifecycle.HiltViewModel

# ── Room ──
-keep class * extends androidx.room.RoomDatabase
-keep class com.kian.mahmoudi.vegang.data.database.model.** { *; }

# ── Gson / JSON ──
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ── OkHttp ──
-dontwarn okhttp3.**
-dontwarn okio.**

# ── V2Ray (Native JNI) ──
-keep class dev.dev7.lib.v2ray.** { *; }
-keep class com.kian.mahmoudi.vegang.data.config.** { *; }

# ── Toasty ──
-dontwarn me.toasty.**

