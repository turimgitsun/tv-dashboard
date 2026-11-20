package de.dashboard.tv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import de.dashboard.tv.models.StockQuote
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object StockRepository {

    private const val BASE_URL = "https://query1.finance.yahoo.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    suspend fun getStock(symbol: String, name: String): StockQuote? = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getStockChart(symbol)
            val result = response.chart.result.firstOrNull()
            val meta = result?.meta ?: return@withContext null
            val price = meta.regularMarketPrice ?: meta.chartPreviousClose ?: 0.0
            val prevClose = meta.chartPreviousClose ?: meta.previousClose ?: price
            val changePercent = if (prevClose > 0) ((price - prevClose) / prevClose * 100) else 0.0
            val changeStr = if (changePercent >= 0) {
                "+%.2f%%".format(changePercent)
            } else {
                "%.2f%%".format(changePercent)
            }
            StockQuote(
                symbol = symbol,
                name = name,
                price = price,
                change = changeStr
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}