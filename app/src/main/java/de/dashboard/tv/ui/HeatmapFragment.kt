package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import de.dashboard.tv.R
import de.dashboard.tv.data.HeatmapRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class HeatmapFragment : Fragment() {

    private lateinit var stockMap: TreemapView
    private lateinit var cryptoMap: TreemapView
    private lateinit var progressBar: ProgressBar
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_heatmap, container, false)

        stockMap = view.findViewById(R.id.stockMap)
        cryptoMap = view.findViewById(R.id.cryptoMap)
        progressBar = view.findViewById(R.id.progressBar)

        load()
        return view
    }

    private fun load() {
        progressBar.visibility = View.VISIBLE

        scope.launch {
            val stocks = HeatmapRepository.getStockHeatmap()
            val crypto = HeatmapRepository.getCryptoHeatmap()

            progressBar.visibility = View.GONE

            stockMap.setData(stocks)
            cryptoMap.setData(crypto)
        }
    }
}
