package com.example.coinsight.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coinsight.R
import com.example.coinsight.adapter.MarketAdapter
import com.example.coinsight.api.ApiUtilities
import com.example.coinsight.databinding.FragmentCryptoListBinding
import com.example.coinsight.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CryptoListFragment : Fragment() {

    private var _binding: FragmentCryptoListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MarketAdapter
    private lateinit var cryptoDatabase: CryptoDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCryptoListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        cryptoDatabase = CryptoDatabase.getDatabase(requireContext())

        binding.currencyRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MarketAdapter(requireContext(), R.id.action_cryptoListFragment_to_detailsFragment)
        binding.currencyRecyclerView.adapter = adapter

        fetchCryptocurrencyData()
    }

    private fun fetchCryptocurrencyData() {
        val api = ApiUtilities.getCoinMarketCapApi()

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = api.getLatestListings().execute()
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val cryptocurrencyResponse = response.body()
                        Log.d("CryptoListFragment", "API Response: $cryptocurrencyResponse")

                        cryptocurrencyResponse?.let {
                            val cryptocurrencies = it.cryptocurrencies
                            Log.d("CryptoListFragment", "Received ${cryptocurrencies.size} cryptocurrencies")
                            if (cryptocurrencies.isNotEmpty()) {
                                adapter.submitList(cryptocurrencies)
                                binding.notFoundTextView.visibility = View.GONE
                                binding.currencyRecyclerView.visibility = View.VISIBLE
                                storeDataLocally(cryptocurrencies)
                            } else {
                                showError("No data received from the API or cryptocurrencies list is empty")
                            }
                        } ?: run {
                            showError("No data received from the API or cryptocurrencies list is null")
                        }
                    } else {
                        showError("API call unsuccessful: ${response.code()} ${response.message()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("CryptoListFragment", "Exception during API call", e)
                withContext(Dispatchers.Main) {
                    showError("Exception during API call: ${e.message}")
                    loadLocalData()
                }
            }
        }
    }

    private fun storeDataLocally(cryptocurrencies: List<CryptoCurrency>) {
        val cryptoEntities = cryptocurrencies.map { crypto ->
            CryptoCurrencyEntity(
                id = crypto.id,
                name = crypto.name,
                symbol = crypto.symbol,
                quote = QuoteEntity(
                    usd = UsdEntity(
                        price = crypto.quote.usd.price,
                        percentChange1h = crypto.quote.usd.percentChange1h,
                        percentChange24h = crypto.quote.usd.percentChange24h,
                        percentChange7d = crypto.quote.usd.percentChange7d
                    )
                )
            )
        }
        lifecycleScope.launch(Dispatchers.IO) {
            cryptoDatabase.cryptoCurrencyDao().insertAll(cryptoEntities)
        }
    }

    private fun loadLocalData() {
        lifecycleScope.launch(Dispatchers.IO) {
            val localData = cryptoDatabase.cryptoCurrencyDao().getAll()
            withContext(Dispatchers.Main) {
                if (localData.isNotEmpty()) {
                    val cryptocurrencies = localData.map { entity ->
                        CryptoCurrency(
                            id = entity.id,
                            name = entity.name,
                            symbol = entity.symbol,
                            quote = Quote(
                                usd = Usd(
                                    price = entity.quote.usd.price,
                                    percentChange1h = entity.quote.usd.percentChange1h,
                                    percentChange24h = entity.quote.usd.percentChange24h,
                                    percentChange7d = entity.quote.usd.percentChange7d
                                )
                            )
                        )
                    }
                    adapter.submitList(cryptocurrencies)
                    binding.notFoundTextView.visibility = View.GONE
                    binding.currencyRecyclerView.visibility = View.VISIBLE
                } else {
                    showError("No local data available")
                }
            }
        }
    }

    private fun showError(message: String) {
        Log.e("CryptoListFragment", message)
        binding.notFoundTextView.visibility = View.VISIBLE
        binding.currencyRecyclerView.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
