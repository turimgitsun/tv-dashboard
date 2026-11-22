package de.dashboard.tv.ui

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.models.HeatmapItem

class HeatmapAdapter(private var items: List<HeatmapItem>) :
    RecyclerView.Adapter<HeatmapAdapter.TileViewHolder>() {

    fun submitList(newItems: List<HeatmapItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun getItemCount() = items.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TileViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_heatmap_tile, parent, false)
        return TileViewHolder(view)
    }

    override fun onBindViewHolder(holder: TileViewHolder, position: Int) {
        val item = items[position]
        val isCrypto = item.symbol.endsWith("-USD")
        val displaySymbol = if (isCrypto) item.symbol.removeSuffix("-USD") else item.symbol

        holder.bind(item, displaySymbol)
    }

    class TileViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val symbolText: TextView = itemView.findViewById(R.id.tileSymbol)
        private val changeText: TextView = itemView.findViewById(R.id.tileChange)
        private val priceText: TextView = itemView.findViewById(R.id.tilePrice)

        fun bind(item: HeatmapItem, displaySymbol: String) {
            symbolText.text = displaySymbol
            val cur = when (item.currency.uppercase()) {
                "EUR" -> "€"; "USD" -> "$"; "GBP" -> "£"; else -> item.currency
            }
            val priceStr = if (item.price >= 1.0) {
                String.format("%,.2f %s", item.price, cur)
            } else {
                String.format("%.4f %s", item.price, cur)
            }
            priceText.text = priceStr

            val change = kotlin.math.abs(item.change24h)
            val changeStr = "${if (item.change24h >= 0) "▲" else "▼"} %.2f%%".format(change)
            changeText.text = changeStr
            changeText.setTextColor(
                if (item.change24h >= 0) Color.parseColor("#35D07F") else Color.parseColor("#E5484D")
            )

            // Background color based on change
            val base = if (item.change24h >= 0) Color.parseColor("#35D07F") else Color.parseColor("#E5484D")
            itemView.setBackgroundColor(Color.argb(0x22, (base shr 16) and 0xFF, (base shr 8) and 0xFF, base and 0xFF))
        }
    }
}
