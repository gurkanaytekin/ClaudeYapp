package com.gurkan.yapp.ai.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Singleton that manages a single Retrofit instance for the application.
 *
 * Usage:
 * ```
 * // Initialise once (e.g. in Application.onCreate)
 * NetworkInstance.init(
 *     ConfigModel(
 *         baseUrl        = "https://api.example.com/",
 *         connectTimeout = 30,
 *         readTimeout    = 30,
 *         writeTimeout   = 30
 *     )
 * )
 *
 * // Make a GET request from a coroutine
 * val films = NetworkInstance.get("/films")
 * ```
 */
object NetworkInstance {

    private var api: NetworkApi? = null

    /**
     * Initialises the network layer with the supplied [config].
     * Must be called before any [get] calls.
     *
     * @throws IllegalArgumentException if [ConfigModel.baseUrl] is blank.
     */
    fun init(config: ConfigModel) {
        require(config.baseUrl.isNotBlank()) { "baseUrl must not be blank." }
        val client = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeout, TimeUnit.SECONDS)
            .readTimeout(config.readTimeout, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeout, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(NetworkApi::class.java)
    }

    /**
     * Performs an HTTP GET request to [path] (relative to the configured base URL)
     * and returns the raw response body as a [String].
     *
     * Must be called from a coroutine.
     *
     * @throws IllegalStateException if [init] has not been called yet.
     */
    suspend fun get(path: String): String {
        val networkApi = api
            ?: throw IllegalStateException(
                "NetworkInstance is not initialised. Call NetworkInstance.init(ConfigModel) first."
            )
        return networkApi.get(path).string()
    }
}
