package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.data.HeatmapRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HeatmapFragment : Fragment() {

    private lateinit var stockAdapter: HeatmapAdapter
    private lateinit var cryptoAdapter: HeatmapAdapter
    private lateinit var progressBar: ProgressBar
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_heatmap, container, false)

        progressBar = view.findViewById(R.id.progressBar)

        val stockRv = view.findViewById<RecyclerView>(R.id.stockMap)
        stockRv.layoutManager = LinearLayoutManager(requireContext())
        stockAdapter = HeatmapAdapter(emptyList())
        stockRv.adapter = stockAdapter

        val cryptoRv = view.findViewById<RecyclerView>(R.id.cryptoMap)
        cryptoRv.layoutManager = LinearLayoutManager(requireContext())
        cryptoAdapter = HeatmapAdapter(emptyList())
        cryptoRv.adapter = cryptoAdapter

        load()
        return view
    }

    private fun load() {
        progressBar.visibility = View.VISIBLE

        scope.launch {
            val stocks = HeatmapRepository.getStockHeatmap()
            val crypto = HeatmapRepository.getCryptoHeatmap()

            progressBar.visibility = View.GONE

            stockAdapter.submitList(stocks)
            cryptoAdapter.submitList(crypto)
        }
    }
}