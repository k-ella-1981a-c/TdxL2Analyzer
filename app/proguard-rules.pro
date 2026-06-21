# TdxL2Analyzer ProGuard Rules

# 基础 Android
-keep class android.** { *; }
-keep class androidx.** { *; }
-keep class com.google.** { *; }

# Kotlin
-keep class kotlin.** { *; }
-keep class kotlinx.** { *; }
-keep class org.jetbrains.** { *; }

# MPAndroidChart
-keep class com.github.mikephil.charting.** { *; }

# OkHttp
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# 数据模型 (keep Serializable/Parcelable)
-keep class com.tdx.l2analyzer.data.model.** { *; }

# 混淆时不移除注解
-keepattributes *Annotation*

# 移除日志
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static int v(...);
    public static int i(...);
    public static int w(...);
    public static int d(...);
    public static int e(...);
}

# 优化
-optimizationpasses 5
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
