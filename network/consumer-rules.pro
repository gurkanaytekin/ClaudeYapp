# Retrofit
-keepattributes Signature
-keep class retrofit2.** { *; }

# OkHttp
-keep class okhttp3.** { *; }

# Gson
-keep class com.google.gson.** { *; }
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}
