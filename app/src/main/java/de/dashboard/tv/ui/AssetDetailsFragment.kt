package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import de.dashboard.tv.R
import de.dashboard.tv.data.AssetCatalog
import de.dashboard.tv.data.HeatmapRepository
import de.dashboard.tv.data.WatchlistRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AssetDetailsFragment : Fragment() {

    private lateinit var symbolText: TextView
    private lateinit var nameText: TextView
    private lateinit var priceText: TextView
    private lateinit var changeText: TextView
    private lateinit var periodText: TextView
    private lateinit var metaText: TextView
    private lateinit var btnWatchlistToggle: com.google.android.material.button.MaterialButton

    companion object {
        fun newInstance(kind: String, symbol: String): AssetDetailsFragment = AssetDetailsFragment().apply {
            arguments = Bundle().apply {
                putString("kind", kind)
                putString("symbol", symbol)
            }
        }
    }

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_asset_details, container, false)
        symbolText = view.findViewById(R.id.detailsSymbol)
        nameText = view.findViewById(R.id.detailsName)
        priceText = view.findViewById(R.id.detailsPrice)
        changeText = view.findViewById(R.id.detailsChange)
        periodText = view.findViewById(R.id.detailsPeriod)
        metaText = view.findViewById(R.id.detailsMeta)
        btnWatchlistToggle = view.findViewById(R.id.btnWatchlistToggle)

        btnWatchlistToggle.setOnClickListener { toggleWatchlist() }

        load()
        return view
    }

    private fun load() {
        val kind = arguments?.getString("kind") ?: "stocks"
        val symbol = arguments?.getString("symbol") ?: ""
        val displaySymbol = if (symbol.endsWith("-USD")) symbol.removeSuffix("-USD") else symbol
        symbolText.text = displaySymbol
        nameText.text = AssetCatalog.nameOf(symbol)

        periodText.text =
            if (kind == "crypto") "Letzte 24 Stunden" else "Zum vorherigen Handelsschluss"

        scope.launch {
            val list = if (kind == "crypto") HeatmapRepository.getCryptoHeatmap() else HeatmapRepository.getStockHeatmap()
            val item = list.firstOrNull { it.symbol == symbol }
            if (item != null) {
                priceText.text = String.format("%.2f %s", item.price, item.currency)
                changeText.text = (if (item.change24h >= 0) "+%.2f%%" else "%.2f%%").format(item.change24h)
                changeText.setTextColor(
                    if (item.change24h >= 0) itemViewContext().getColor(R.color.positive)
                    else itemViewContext().getColor(R.color.negative)
                )
                metaText.text = "Datenstand: –"
            } else {
                priceText.text = "–"
                changeText.text = "–"
                metaText.text = "Keine Daten"
            }
        }
    }

    private fun itemViewContext() = requireContext()

    private fun toggleWatchlist() {
        val symbols = WatchlistRepository.get(requireContext()).toMutableList()
        val idx = symbols.indexOf(arguments?.getString("symbol") ?: "")
        val symbol = arguments?.getString("symbol") ?: ""
        if (idx >= 0) symbols.removeAt(idx) else symbols.add(symbol)
        WatchlistRepository.save(requireContext(), symbols)
        val inList = symbols.any { it == arguments?.getString("symbol") }
        btnWatchlistToggle.text = if (inList) "Aus Watchlist entfernen" else "Zur Watchlist hinzufügen"
    }
}
