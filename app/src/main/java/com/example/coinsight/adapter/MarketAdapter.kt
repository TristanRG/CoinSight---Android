package com.example.coinsight.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.coinsight.R
import com.example.coinsight.databinding.CoinLayoutBinding
import com.example.coinsight.fragments.CryptoListFragmentDirections
import com.example.coinsight.fragments.SavedFragmentDirections
import com.example.coinsight.model.CryptoCurrency
import com.google.gson.Gson

class MarketAdapter(
    private val context: Context,
    private val navigateToDetailsActionId: Int
) : ListAdapter<CryptoCurrency, MarketAdapter.MarketViewHolder>(CryptoDiffCallback()) {

    inner class MarketViewHolder(private val binding: CoinLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    val itemJson = Gson().toJson(item)
                    val action = when (navigateToDetailsActionId) {
                        R.id.action_cryptoListFragment_to_detailsFragment ->
                            CryptoListFragmentDirections.actionCryptoListFragmentToDetailsFragment(itemJson)
                        R.id.action_savedFragment_to_detailsFragment ->
                            SavedFragmentDirections.actionSavedFragmentToDetailsFragment(itemJson)
                        else -> throw IllegalStateException("Unknown action ID")
                    }
                    binding.root.findNavController().navigate(action)
                }
            }
        }

        fun bind(item: CryptoCurrency) {
            Log.d("MarketViewHolder", "Binding item: $item")
            binding.apply {
                currencyNameTextView.text = item.name
                currencySymbolTextView.text = item.symbol

                Glide.with(context)
                    .load("https://s2.coinmarketcap.com/static/img/coins/64x64/${item.id}.png")
                    .placeholder(R.drawable.placeholder_image_foreground)
                    .into(currencyImageView)

                Glide.with(context)
                    .load("https://s3.coinmarketcap.com/generated/sparklines/web/7d/usd/${item.id}.png")
                    .placeholder(R.drawable.placeholder_image_background)
                    .into(currencyChartImageView)

                currencyPriceTextView.text = "$${String.format("%.02f", item.quote.usd.price)}"

                val percentChange24h = item.quote.usd.percentChange24h
                val changeText = if (percentChange24h > 0) {
                    "+${String.format("%.02f", percentChange24h)} %"
                } else {
                    "${String.format("%.02f", percentChange24h)} %"
                }
                currencyChangeTextView.apply {
                    text = changeText
                    setTextColor(context.resources.getColor(if (percentChange24h > 0) R.color.green else R.color.red))
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MarketViewHolder {
        val inflater = LayoutInflater.from(context)
        val binding = CoinLayoutBinding.inflate(inflater, parent, false)
        return MarketViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MarketViewHolder, position: Int) {
        val item = getItem(position)
        item?.let { holder.bind(it) }
    }

    private class CryptoDiffCallback : DiffUtil.ItemCallback<CryptoCurrency>() {
        override fun areItemsTheSame(oldItem: CryptoCurrency, newItem: CryptoCurrency): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CryptoCurrency, newItem: CryptoCurrency): Boolean {
            return oldItem == newItem
        }
    }
}
