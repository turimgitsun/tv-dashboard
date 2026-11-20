package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.models.StockQuote
import de.dashboard.tv.data.StockRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class StocksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StockAdapter
    private lateinit var progressBar: ProgressBar

    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_stocks, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = StockAdapter(emptyList()) { stock ->
            // Handle stock click if needed
        }
        recyclerView.adapter = adapter

        loadStocks()

        return view
    }

    private fun loadStocks() {
        progressBar.visibility = View.VISIBLE

        coroutineScope.launch {
            val stocks = listOf(
                Triple("AAPL", "Apple Inc.", "Apple"),
                Triple("MSFT", "Microsoft Corp.", "Microsoft"),
                Triple("GOOGL", "Alphabet Inc.", "Google"),
                Triple("AMZN", "Amazon.com Inc.", "Amazon"),
                Triple("TSLA", "Tesla Inc.", "Tesla")
            )

            val results = stocks.mapNotNull { (symbol, name, displayName) ->
                StockRepository.getStock(symbol, displayName).let { stock ->
                    stock
                }
            }

            progressBar.visibility = View.GONE
            adapter.submitList(results)
        }
    }

    class StockAdapter(
        private var stocks: List<StockQuote>,
        private val onStockClick: (StockQuote) -> Unit
    ) : RecyclerView.Adapter<StockAdapter.StockViewHolder>() {

        fun submitList(newStocks: List<StockQuote>) {
            stocks = newStocks
            notifyDataSetChanged()
        }

        override fun getItemCount() = stocks.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stock, parent, false)
            return StockViewHolder(view)
        }

        override fun onBindViewHolder(holder: StockViewHolder, position: Int) {
            val stock = stocks[position]
            holder.bind(stock)
        }

        class StockViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val symbolText: TextView = itemView.findViewById(R.id.symbolText)
            private val nameText: TextView = itemView.findViewById(R.id.nameText)
            private val priceText: TextView = itemView.findViewById(R.id.priceText)
            private val changeText: TextView = itemView.findViewById(R.id.changeText)

            fun bind(stock: StockQuote) {
                symbolText.text = stock.symbol
                nameText.text = stock.name
                priceText.text = String.format("%.2f €", stock.price)
                changeText.text = stock.change

                val changeValue = stock.change.removePrefix("+").toDoubleOrNull()
                val isPositive = changeValue != null && changeValue > 0
                changeText.setTextColor(
                    if (isPositive) android.graphics.Color.GREEN else android.graphics.Color.RED
                )
            }
        }
    }
}