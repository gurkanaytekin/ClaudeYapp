package com.gurkan.yapp.ai.network

import okhttp3.ResponseBody
import retrofit2.http.GET
import retrofit2.http.Url

internal interface NetworkApi {
    @GET
    suspend fun get(@Url url: String): ResponseBody
}
