package com.gurkan.yapp.ai.network

/**
 * Configuration model for initializing [NetworkInstance].
 *
 * @param baseUrl        Base URL for all network requests (must not be blank; Retrofit requires it
 *                       to end with "/").
 * @param connectTimeout Connection timeout in seconds. Default: 30.
 * @param readTimeout    Read timeout in seconds. Default: 30.
 * @param writeTimeout   Write timeout in seconds. Default: 30.
 */
data class ConfigModel(
    val baseUrl: String,
    val connectTimeout: Long = 30,
    val readTimeout: Long = 30,
    val writeTimeout: Long = 30
)
