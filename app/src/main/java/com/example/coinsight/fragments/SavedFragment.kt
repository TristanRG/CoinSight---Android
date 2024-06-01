package com.example.coinsight.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.coinsight.R
import com.example.coinsight.adapter.MarketAdapter
import com.example.coinsight.databinding.FragmentSavedBinding
import com.example.coinsight.utils.SavedCryptos

class SavedFragment : Fragment() {

    private var _binding: FragmentSavedBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: MarketAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSavedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.watchlistRecyclerView.layoutManager = LinearLayoutManager(context)
        adapter = MarketAdapter(requireContext(), R.id.action_savedFragment_to_detailsFragment)
        binding.watchlistRecyclerView.adapter = adapter

        loadSavedCryptos()
    }

    private fun loadSavedCryptos() {
        val savedCryptos = SavedCryptos.getAll()

        if (savedCryptos.isEmpty()) {
            binding.emptyTextView.visibility = View.VISIBLE
            binding.watchlistRecyclerView.visibility = View.GONE
        } else {
            binding.emptyTextView.visibility = View.GONE
            binding.watchlistRecyclerView.visibility = View.VISIBLE
            adapter.submitList(savedCryptos)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
