# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.

# Keep Kotlin metadata
-keep class kotlin.Metadata { *; }
-keepattributes *Annotation*

# Keep Hilt
-keep class dagger.hilt.** { *; }
-keep class javax.inject.** { *; }
-keep class * extends dagger.hilt.internal.GeneratedComponent { *; }

# Keep Room entities
-keep class com.hermes.android.data.db.** { *; }

# Keep serializers
-keepclassmembers class * extends kotlinx.serialization.Serializable {
    <fields>;
}

# Keep WebSocket/OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# Keep Markwon
-dontwarn io.noties.markwon.**
