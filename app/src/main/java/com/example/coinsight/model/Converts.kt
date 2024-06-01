package com.example.coinsight.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object Converters {

    @TypeConverter
    fun fromUsdEntity(usdEntity: UsdEntity): String {
        return Gson().toJson(usdEntity)
    }

    @TypeConverter
    fun toUsdEntity(usdString: String): UsdEntity {
        val type = object : TypeToken<UsdEntity>() {}.type
        return Gson().fromJson(usdString, type)
    }

    @TypeConverter
    fun fromQuoteEntity(quoteEntity: QuoteEntity): String {
        return Gson().toJson(quoteEntity)
    }

    @TypeConverter
    fun toQuoteEntity(quoteString: String): QuoteEntity {
        val type = object : TypeToken<QuoteEntity>() {}.type
        return Gson().fromJson(quoteString, type)
    }
}
