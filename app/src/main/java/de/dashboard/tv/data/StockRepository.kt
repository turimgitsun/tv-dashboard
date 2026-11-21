package de.dashboard.tv.data

import de.dashboard.tv.models.StockQuote
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object StockRepository {

    private val api get() = RetrofitClient.apiService

    suspend fun getQuote(symbol: String, name: String): StockQuote? = withContext(Dispatchers.IO) {
        try {
            val response = api.getStockChart(symbol)
            val meta = response.chart.result.firstOrNull()?.meta ?: return@withContext null
            val price = meta.regularMarketPrice ?: meta.chartPreviousClose ?: return@withContext null
            val prevClose = meta.chartPreviousClose ?: meta.previousClose ?: price
            val changeValue = if (prevClose > 0) (price - prevClose) / prevClose * 100.0 else 0.0
            val change = if (changeValue >= 0) "+%.2f%%".format(changeValue) else "%.2f%%".format(changeValue)
            StockQuote(
                symbol = symbol,
                name = name,
                price = price,
                change = change,
                changeValue = changeValue,
                currency = meta.currency ?: "USD"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
