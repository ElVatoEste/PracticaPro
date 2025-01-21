# Preservar anotaciones necesarias
-keepattributes *Annotation*

# Mantener anotaciones de Retrofit
-keep @retrofit2.http.* class * { *; }

# Mantener anotaciones de Room
-keep @androidx.room.* class * { *; }

# Mantener anotaciones de Gson
-keep class com.google.gson.annotations.** { *; }

# Mantener Retrofit y las interfaces de API
-keep class retrofit2.** { *; }
-keep interface retrofit2.http.* { *; }

# Evitar advertencias de Retrofit
-dontwarn retrofit2.**
-dontwarn okhttp3.**

# Mantener Gson y modelos relacionados
-keep class com.google.gson.** { *; }
-keep class com.google.gson.annotations.** { *; }

# Preservar modelos de datos JSON
-keep class com.vatodev.practicapro.model.** { *; }

# Mantener clases de Room
-keep class androidx.room.** { *; }
-keep @androidx.room.* class * { *; }

# Mantener bases de datos y DAOs
-keep class * extends androidx.room.RoomDatabase { *; }
-keep @androidx.room.Database class * { *; }
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao class * { *; }

# Mantener Compose
-keep class androidx.compose.** { *; }
-keep class kotlin.** { *; }

# Evitar advertencias de Compose
-dontwarn androidx.compose.**

# Mantener Kotlin Coroutines
-keepclassmembers class kotlinx.coroutines.** { *; }
-dontwarn kotlinx.coroutines.**

# Mantener Material Design 3
-keep class com.google.android.material.** { *; }
-dontwarn com.google.android.material.**

# Mantener todas las constantes de BuildConfig
-keepclassmembers class **.BuildConfig {
    public static final *;
}

-keep class com.vatodev.practicapro.model.** { *; }
-keep class com.vatodev.practicapro.entitys.** { *; }

# Mantener clases de Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Evitar eliminaciones relacionadas con Kotlin
-keepclassmembers class kotlin.** { *; }
-dontwarn kotlin.**

# Mantener clases con reflexiones
-keepattributes Signature, EnclosingMethod, InnerClasses
-keepclasseswithmembers class * {
    public <init>(...);
}
