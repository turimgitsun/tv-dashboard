package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.data.AssetCatalog
import de.dashboard.tv.data.HeatmapRepository
import de.dashboard.tv.data.NewsRepository
import de.dashboard.tv.data.StockRepository
import de.dashboard.tv.data.WatchlistRepository
import de.dashboard.tv.models.StockQuote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class OverviewFragment : Fragment() {

    private lateinit var stockAdapter: HeatmapAdapter
    private lateinit var cryptoAdapter: HeatmapAdapter
    private lateinit var watchAdapter: WatchlistCardAdapter
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_overview, container, false)

        val stocksRv = view.findViewById<RecyclerView>(R.id.overviewStocks)
        stocksRv.layoutManager = LinearLayoutManager(requireContext())
        stockAdapter = HeatmapAdapter(emptyList())
        stocksRv.adapter = stockAdapter

        val cryptoRv = view.findViewById<RecyclerView>(R.id.overviewCrypto)
        cryptoRv.layoutManager = LinearLayoutManager(requireContext())
        cryptoAdapter = HeatmapAdapter(emptyList())
        cryptoRv.adapter = cryptoAdapter

        val watchRv = view.findViewById<RecyclerView>(R.id.overviewWatchlist)
        watchRv.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        watchAdapter = WatchlistCardAdapter(emptyList())
        watchRv.adapter = watchAdapter

        stocksRv.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) { openFullscreen("stocks"); true } else { false }
        }
        cryptoRv.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) { openFullscreen("crypto"); true } else { false }
        }
        watchRv.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == android.view.KeyEvent.KEYCODE_DPAD_CENTER) {
                parentFragmentManager.beginTransaction().replace(R.id.fragmentContainer, PortfolioFragment()).addToBackStack(null).commit()
                true
            } else { false }
        }

        load()
        return view
    }

    private fun load() {
        scope.launch {
            val stocks = async { HeatmapRepository.getStockHeatmap() }
            val crypto = async { HeatmapRepository.getCryptoHeatmap() }
            val watchlist = async { loadWatchlist() }

            stockAdapter.submitList(stocks.await())
            cryptoAdapter.submitList(crypto.await())
            watchAdapter.submitList(watchlist.await())

            runCatching {
                val items = NewsRepository.getTopHeadlines()
                val first = items.firstOrNull()
                if (first != null) {
                    view?.findViewById<TextView>(R.id.overviewNewsHeadline)?.text = first.headline
                    view?.findViewById<TextView>(R.id.overviewNewsMeta)?.text =
                        "${first.source} · ${first.time}"
                }
            }
        }
    }

    private suspend fun loadWatchlist(): List<StockQuote> = coroutineScope {
        val symbols = WatchlistRepository.get(requireContext()).take(4)
        if (symbols.isEmpty()) return@coroutineScope emptyList()
        symbols.map { sym ->
            async(Dispatchers.IO) { StockRepository.getQuote(sym, AssetCatalog.nameOf(sym)) }
        }.awaitAll().filterNotNull()
    }

    class WatchlistCardAdapter(private var items: List<StockQuote>) :
        RecyclerView.Adapter<WatchlistCardAdapter.CardHolder>() {

        fun submitList(newItems: List<StockQuote>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun getItemCount() = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardHolder {
            val v = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_watchlist_card, parent, false)
            return CardHolder(v)
        }

        override fun onBindViewHolder(holder: CardHolder, position: Int) {
            holder.bind(items[position])
        }

        class CardHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val symbol: TextView = itemView.findViewById(R.id.cardSymbol)
            private val name: TextView = itemView.findViewById(R.id.cardName)
            private val price: TextView = itemView.findViewById(R.id.cardPrice)
            private val change: TextView = itemView.findViewById(R.id.cardChange)

            fun bind(q: StockQuote) {
                val sym = if (q.symbol.endsWith("-USD")) q.symbol.removeSuffix("-USD") else q.symbol
                symbol.text = sym
                name.text = q.name
                price.text = String.format("%.2f %s", q.price, q.currency)
                val pct = if (q.changeValue >= 0) "+%.2f%%" else "%.2f%%"
                change.text = String.format(pct, q.changeValue)
                change.setTextColor(
                    if (q.changeValue >= 0) itemView.context.getColor(R.color.positive)
                    else itemView.context.getColor(R.color.negative)
                )
                (itemView as? CardView)?.setCardBackgroundColor(
                    itemView.context.getColor(R.color.surface)
                )
            }
        }
    }

    private fun openFullscreen(kind: String) {
        val next = HeatmapFullscreenFragment.newInstance(kind)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, next)
            .addToBackStack(null)
            .commit()
    }
}
