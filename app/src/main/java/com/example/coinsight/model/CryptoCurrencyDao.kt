package com.example.coinsight.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CryptoCurrencyDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cryptocurrencies: List<CryptoCurrencyEntity>)

    @Query("SELECT * FROM cryptocurrency")
    suspend fun getAll(): List<CryptoCurrencyEntity>
}
