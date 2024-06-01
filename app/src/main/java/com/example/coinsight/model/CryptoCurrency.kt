package com.example.coinsight.model

import com.google.gson.annotations.SerializedName

data class CryptoCurrency(
    val id: Int,
    val name: String,
    val symbol: String,
    @SerializedName("quote")
    val quote: Quote
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is CryptoCurrency) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id
    }
}

data class Quote(
    @SerializedName("USD")
    val usd: Usd
)

data class Usd(
    val price: Double,
    @SerializedName("percent_change_1h")
    val percentChange1h: Double,
    @SerializedName("percent_change_24h")
    val percentChange24h: Double,
    @SerializedName("percent_change_7d")
    val percentChange7d: Double
)
