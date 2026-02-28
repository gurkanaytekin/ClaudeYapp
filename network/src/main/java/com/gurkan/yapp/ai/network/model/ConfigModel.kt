package com.gurkan.yapp.ai.network.model

/**
 * Configuration model for NetworkInstance initialization.
 *
 * Usage:
 * ```
 * NetworkInstance.init(
 *     ConfigModel(
 *         baseUrl = "https://api.example.com/",
 *         connectTimeoutSeconds = 30,
 *         readTimeoutSeconds = 30,
 *         writeTimeoutSeconds = 30,
 *         headers = mapOf("Authorization" to "Bearer token"),
 *         enableLogging = BuildConfig.DEBUG
 *     )
 * )
 * ```
 */
data class ConfigModel(
    val baseUrl: String,
    val connectTimeoutSeconds: Long = 30L,
    val readTimeoutSeconds: Long = 30L,
    val writeTimeoutSeconds: Long = 30L,
    val headers: Map<String, String> = emptyMap(),
    val enableLogging: Boolean = false
)
