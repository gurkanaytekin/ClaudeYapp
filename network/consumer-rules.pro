# Consumer ProGuard rules for the :network module.
# Keep Retrofit service interfaces and model classes.
-keep interface com.gurkan.yapp.ai.network.NetworkApi { *; }
-keep class com.gurkan.yapp.ai.network.ConfigModel { *; }
-keep class com.gurkan.yapp.ai.network.NetworkInstance { *; }
