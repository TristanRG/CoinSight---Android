package com.example.coinsight.utils

import com.example.coinsight.model.CryptoCurrency

object SavedCryptos {
    private val savedList = mutableListOf<CryptoCurrency>()

    fun add(crypto: CryptoCurrency) {
        if (!isSaved(crypto)) {
            savedList.add(crypto)
        }
    }

    fun remove(crypto: CryptoCurrency) {
        savedList.remove(crypto)
    }

    fun isSaved(crypto: CryptoCurrency): Boolean {
        return savedList.contains(crypto)
    }

    fun getAll(): List<CryptoCurrency> {
        return savedList.toList()
    }
}
