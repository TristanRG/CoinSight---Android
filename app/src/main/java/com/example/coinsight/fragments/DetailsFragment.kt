package com.example.coinsight.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.coinsight.databinding.FragmentDetailsBinding
import com.example.coinsight.model.CryptoCurrency
import com.example.coinsight.utils.SavedCryptos
import com.google.gson.Gson
import android.webkit.WebViewClient
import com.example.coinsight.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding!!
    private val TAG = "DetailsFragment"
    private lateinit var cryptoCurrency: CryptoCurrency

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cryptoCurrencyJson = requireArguments().getString("cryptoCurrencyJson") ?: ""
        Log.d(TAG, "Received JSON: $cryptoCurrencyJson")

        if (cryptoCurrencyJson.isNotEmpty()) {
            try {
                cryptoCurrency = Gson().fromJson(cryptoCurrencyJson, CryptoCurrency::class.java)
                Log.d(TAG, "Parsed CryptoCurrency: $cryptoCurrency")

                setUpDetails()
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing CryptoCurrency JSON: ${e.message}")
            }
        } else {
            Log.e(TAG, "Received empty JSON string")
        }

        binding.backStackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setUpDetails() {
        Log.d(TAG, "Setting up details for: $cryptoCurrency")

        binding.detailSymbolTextView.text = cryptoCurrency.symbol

        Glide.with(requireContext())
            .load("https://s2.coinmarketcap.com/static/img/coins/64x64/${cryptoCurrency.id}.png")
            .into(binding.detailImageView)

        binding.detailPriceTextView.text = "$ ${String.format("%.4f", cryptoCurrency.quote.usd.price)}"
        setPriceChange(cryptoCurrency.quote.usd.percentChange24h)

        updateSaveButton()
        loadChart(cryptoCurrency.symbol, "D")
        setupButtonClickListeners()

        binding.addSavelistButton.setOnClickListener {
            toggleSaveStatus()
        }
    }

    private fun setPriceChange(changePercentage: Double) {
        if (changePercentage > 0) {
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.green))
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_up)
            binding.detailChangeTextView.text = "+${String.format("%.02f", changePercentage)} %"
        } else {
            binding.detailChangeTextView.setTextColor(requireContext().resources.getColor(R.color.red))
            binding.detailChangeImageView.setImageResource(R.drawable.ic_caret_down)
            binding.detailChangeTextView.text = "${String.format("%.02f", changePercentage)} %"
        }
    }

    private fun loadChart(symbol: String, interval: String) {
        binding.detaillChartWebView.settings.javaScriptEnabled = true
        binding.detaillChartWebView.webViewClient = WebViewClient()

        val url = "https://s.tradingview.com/widgetembed/?frameElementId=tradingview_76d87&symbol=${symbol}USD&interval=$interval&hidesidetoolbar=1&hidetoptoolbar=1&symboledit=1&saveimage=1&toolbarbg=F1F3F6&studies=[]&hideideas=1&theme=Dark&style=1&timezone=Etc/UTC&studies_overrides={}&overrides={}&enabled_features=[]&disabled_features=[]&locale=en&utmsource=coinmarketcap.com&utmmedium=widget&utm_campaign=chart&utmterm=BTCUSDT"
        Log.d(TAG, "Loading URL: $url")

        binding.detaillChartWebView.loadUrl(url)
    }

    private fun setupButtonClickListeners() {
        binding.button1h.setOnClickListener {
            setPriceChange(cryptoCurrency.quote.usd.percentChange1h)
            updateButtonStyles(binding.button1h)
            loadChart(cryptoCurrency.symbol, "1")
        }
        binding.button24h.setOnClickListener {
            setPriceChange(cryptoCurrency.quote.usd.percentChange24h)
            updateButtonStyles(binding.button24h)
            loadChart(cryptoCurrency.symbol, "D")
        }
        binding.button7d.setOnClickListener {
            setPriceChange(cryptoCurrency.quote.usd.percentChange7d)
            updateButtonStyles(binding.button7d)
            loadChart(cryptoCurrency.symbol, "W")
        }
    }

    private fun updateButtonStyles(activeButton: View) {
        binding.button1h.setBackgroundResource(if (activeButton == binding.button1h) R.drawable.active_button else android.R.color.transparent)
        binding.button24h.setBackgroundResource(if (activeButton == binding.button24h) R.drawable.active_button else android.R.color.transparent)
        binding.button7d.setBackgroundResource(if (activeButton == binding.button7d) R.drawable.active_button else android.R.color.transparent)
    }

    private fun updateSaveButton() {
        if (SavedCryptos.isSaved(cryptoCurrency)) {
            binding.addSavelistButton.setImageResource(R.drawable.star)
        } else {
            binding.addSavelistButton.setImageResource(R.drawable.star_outline)
        }
    }

    private fun toggleSaveStatus() {
        lifecycleScope.launch(Dispatchers.IO) {
            if (SavedCryptos.isSaved(cryptoCurrency)) {
                SavedCryptos.remove(cryptoCurrency)
            } else {
                SavedCryptos.add(cryptoCurrency)
            }
            withContext(Dispatchers.Main) {
                updateSaveButton()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
