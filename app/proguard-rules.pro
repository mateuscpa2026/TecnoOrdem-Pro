# ============================================================================
# Google Play Production ProGuard and R8 Optimization & Obfuscation Rules
# App: TecnoOrdem Pro (com.tecnoordem.pro)
# ============================================================================

# 1. Aggressive Obfuscation & Repackaging
# Flatten package hierarchy into the root package to maximize DEX obfuscation (>25% target)
-repackageclasses ''
-allowaccessmodification

# Strip developer / debug logs in production release builds
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int d(...);
    public static int i(...);
}

# Preserve stack traces for Google Play Console crash deobfuscation
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# 2. Android Core Components
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class androidx.core.content.FileProvider

# 3. Room Database
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-keepclassmembers class * extends androidx.room.RoomDatabase {
    public abstract <methods>;
}
-dontwarn androidx.room.paging.**

# 4. Jetpack DataStore Preferences
-dontwarn androidx.datastore.**
-keep class com.example.data.preferences.** { *; }

# 5. Jetpack Compose Runtime
-keepclassmembers class * {
    @androidx.compose.runtime.Composable *;
}
-dontwarn androidx.compose.**

# 6. Coroutines & Flow
-dontwarn kotlinx.coroutines.**

# 7. Coil Image Loader
-keep class coil.compose.** { *; }
-dontwarn coil.**
