package com.example.coinsight.api

import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiUtilities {

    private const val BASE_URL = "https://pro-api.coinmarketcap.com/"

    private val client = OkHttpClient.Builder().addInterceptor { chain ->
        val original: Request = chain.request()
        val request: Request = original.newBuilder()
            .header("X-CMC_PRO_API_KEY", "secret")
            .header("Accept", "application/json")
            .method(original.method, original.body)
            .build()
        chain.proceed(request)
    }.build()

    fun getCoinMarketCapApi(): CoinMarketCapApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(CoinMarketCapApi::class.java)
    }
}
