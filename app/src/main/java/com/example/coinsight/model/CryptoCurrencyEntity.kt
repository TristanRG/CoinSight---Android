package com.example.coinsight.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters

@Entity(tableName = "cryptocurrency")
data class CryptoCurrencyEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val symbol: String,
    @TypeConverters(Converters::class)
    val quote: QuoteEntity
)

data class QuoteEntity(
    val usd: UsdEntity
)

data class UsdEntity(
    val price: Double,
    val percentChange1h: Double,
    val percentChange24h: Double,
    val percentChange7d: Double
)
