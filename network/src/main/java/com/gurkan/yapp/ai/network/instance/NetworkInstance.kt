package com.gurkan.yapp.ai.network.instance

import com.gurkan.yapp.ai.network.model.ConfigModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Url
import java.util.concurrent.TimeUnit

/**
 * Singleton network instance for making HTTP requests via Retrofit + OkHttp.
 *
 * Must be initialized once before use, typically in Application.onCreate():
 * ```
 * NetworkInstance.init(
 *     ConfigModel(baseUrl = "https://api.example.com/")
 * )
 * ```
 *
 * Usage:
 * ```
 * val films = NetworkInstance.get("/films")
 * ```
 */
object NetworkInstance {

    @PublishedApi internal var retrofit: Retrofit? = null
    private var rawApi: RawApi? = null

    /**
     * Initialize NetworkInstance with the provided configuration.
     * Must be called before any network requests.
     */
    fun init(config: ConfigModel) {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (config.enableLogging) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }

        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(config.readTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(config.writeTimeoutSeconds, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val requestBuilder = chain.request().newBuilder()
                config.headers.forEach { (key, value) ->
                    requestBuilder.addHeader(key, value)
                }
                chain.proceed(requestBuilder.build())
            }
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(config.baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        rawApi = retrofit!!.create(RawApi::class.java)
    }

    /**
     * Make a GET request to the given path and return the raw response body as String.
     * Runs on IO dispatcher.
     *
     * @param path Relative path, e.g. "/films"
     * @return Response body as String, or null if response was unsuccessful
     * @throws IllegalStateException if NetworkInstance was not initialized via init()
     */
    suspend fun get(path: String): String? {
        checkInitialized()
        return withContext(Dispatchers.IO) {
            val response = rawApi!!.get(path.trimStart('/'))
            if (response.isSuccessful) response.body() else null
        }
    }

    /**
     * Make a GET request and deserialize the response into the specified type.
     *
     * @param path Relative path, e.g. "/films"
     * @return Deserialized response body, or null if unsuccessful
     * @throws IllegalStateException if NetworkInstance was not initialized via init()
     */
    suspend inline fun <reified T> getAs(path: String): T? {
        checkInitialized()
        return withContext(Dispatchers.IO) {
            val service = retrofit!!.create(TypedApi::class.java)
            val response = service.get(path.trimStart('/'))
            if (response.isSuccessful) {
                val body = response.body()
                com.google.gson.Gson().fromJson(
                    com.google.gson.Gson().toJson(body),
                    T::class.java
                )
            } else null
        }
    }

    /**
     * Returns the underlying Retrofit instance for creating custom API services.
     *
     * @throws IllegalStateException if NetworkInstance was not initialized via init()
     */
    fun retrofit(): Retrofit {
        checkInitialized()
        return retrofit!!
    }

    @PublishedApi internal fun checkInitialized() {
        check(retrofit != null) {
            "NetworkInstance is not initialized. Call NetworkInstance.init(ConfigModel) first."
        }
    }
}

private interface RawApi {
    @GET
    suspend fun get(@Url url: String): retrofit2.Response<String>
}

@PublishedApi internal interface TypedApi {
    @GET
    suspend fun get(@Url url: String): retrofit2.Response<Any>
}
