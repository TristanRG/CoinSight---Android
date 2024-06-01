package com.example.coinsight.model

import com.google.gson.annotations.SerializedName

data class CryptocurrencyResponse(
    @SerializedName("data")
    val cryptocurrencies: List<CryptoCurrency>
)
