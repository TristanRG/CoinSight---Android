package com.example.coinsight.api

import com.example.coinsight.model.CryptocurrencyResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinMarketCapApi {

    @GET("v1/cryptocurrency/listings/latest")
    fun getLatestListings(
        @Query("start") start: Int = 1,
        @Query("limit") limit: Int = 30,
        @Query("convert") convert: String = "USD"
    ): Call<CryptocurrencyResponse>
}
