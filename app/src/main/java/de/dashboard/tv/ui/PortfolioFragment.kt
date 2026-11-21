package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.data.AssetCatalog
import de.dashboard.tv.data.StockRepository
import de.dashboard.tv.data.WatchlistRepository
import de.dashboard.tv.models.StockQuote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PortfolioFragment : Fragment() {

    private lateinit var adapter: AssetAdapter
    private lateinit var progressBar: ProgressBar
    private lateinit var emptyText: TextView
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_portfolio, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        emptyText = view.findViewById(R.id.emptyText)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = AssetAdapter(emptyList())
        rv.adapter = adapter

        view.findViewById<View>(R.id.btnManage).setOnClickListener { showPicker() }

        load()
        return view
    }

    override fun onResume() {
        super.onResume()
        if (::adapter.isInitialized) load()
    }

    private fun load() {
        val symbols = WatchlistRepository.get(requireContext())

        if (symbols.isEmpty()) {
            emptyText.visibility = View.VISIBLE
            adapter.submitList(emptyList())
            return
        }
        emptyText.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        scope.launch {
            val quotes = withContext(Dispatchers.IO) {
                symbols.map { StockRepository.getQuote(it, AssetCatalog.nameOf(it)) }
            }.filterNotNull()

            progressBar.visibility = View.GONE
            adapter.submitList(quotes)
        }
    }

    private fun showPicker() {
        val entries = AssetCatalog.all
        val selectedSymbols = WatchlistRepository.get(requireContext()).toSet()
        val checked = BooleanArray(entries.size) { entries[it].symbol in selectedSymbols }

        val labels = entries.map { "${it.name}  (${it.symbol})" }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Assets auswählen")
            .setMultiChoiceItems(labels, checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton("Speichern") { _, _ ->
                val selected = entries.filterIndexed { index, _ -> checked[index] }.map { it.symbol }
                WatchlistRepository.save(requireContext(), selected)
                load()
            }
            .setNegativeButton("Abbrechen", null)
            .show()
    }

    class AssetAdapter(private var items: List<StockQuote>) :
        RecyclerView.Adapter<AssetAdapter.AssetViewHolder>() {

        fun submitList(newItems: List<StockQuote>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun getItemCount() = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssetViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_asset, parent, false)
            return AssetViewHolder(view)
        }

        override fun onBindViewHolder(holder: AssetViewHolder, position: Int) {
            holder.bind(items[position])
        }

        class AssetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val symbolText: TextView = itemView.findViewById(R.id.symbolText)
            private val nameText: TextView = itemView.findViewById(R.id.nameText)
            private val priceText: TextView = itemView.findViewById(R.id.priceText)
            private val changeText: TextView = itemView.findViewById(R.id.changeText)

            fun bind(q: StockQuote) {
                symbolText.text = if (q.symbol.endsWith("-USD"))
                    q.symbol.removeSuffix("-USD") else q.symbol
                nameText.text = q.name

                val symbol = when (q.currency.uppercase()) {
                    "EUR" -> "€"
                    "USD" -> "$"
                    "GBP" -> "£"
                    else -> q.currency + " "
                }
                priceText.text = String.format("%.2f %s", q.price, symbol)
                changeText.text = q.change
                changeText.setTextColor(
                    if (q.changeValue >= 0) 0xFF00E5A0.toInt() else 0xFFFF5252.toInt()
                )
            }
        }
    }
}