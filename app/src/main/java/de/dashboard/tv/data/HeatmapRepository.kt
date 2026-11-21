package de.dashboard.tv.data

import de.dashboard.tv.models.HeatmapItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object HeatmapRepository {

    private val geckoApi: CoinGeckoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.coingecko.com/api/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(CoinGeckoService::class.java)
    }

    // Approximate market caps (USD) for sizing – only relative order matters
    private data class StockDef(val symbol: String, val name: String, val capUsd: Double)

    private val STOCKS = listOf(
        StockDef("AAPL", "Apple", 3.4e12),
        StockDef("MSFT", "Microsoft", 3.1e12),
        StockDef("NVDA", "NVIDIA", 2.8e12),
        StockDef("GOOGL", "Alphabet", 2.1e12),
        StockDef("AMZN", "Amazon", 2.0e12),
        StockDef("META", "Meta", 1.5e12),
        StockDef("TSLA", "Tesla", 0.9e12),
        StockDef("NFLX", "Netflix", 0.3e12),
        StockDef("SAP.DE", "SAP", 0.28e12),
        StockDef("SIE.DE", "Siemens", 0.16e12),
        StockDef("ALV.DE", "Allianz", 0.12e12),
        StockDef("BMW.DE", "BMW", 0.06e12)
    )

    suspend fun getCryptoHeatmap(): List<HeatmapItem> = withContext(Dispatchers.IO) {
        try {
            geckoApi.getMarkets(perPage = 20).mapNotNull { coin ->
                val cap = coin.marketCap ?: return@mapNotNull null
                val price = coin.currentPrice ?: return@mapNotNull null
                HeatmapItem(
                    symbol = coin.symbol?.uppercase() ?: "",
                    name = coin.name ?: "",
                    price = price,
                    change24h = coin.priceChangePercentage24h ?: 0.0,
                    marketCap = cap,
                    currency = "USD"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getStockHeatmap(): List<HeatmapItem> = coroutineScope {
        try {
            val yahooApi = RetrofitClient.apiService
            val jobs = STOCKS.map { stock ->
                async(Dispatchers.IO) {
                    try {
                        val resp = yahooApi.getStockChart(stock.symbol)
                        val meta = resp.chart.result.firstOrNull()?.meta ?: return@async null
                        val price = meta.regularMarketPrice ?: meta.chartPreviousClose ?: return@async null
                        val prev = meta.chartPreviousClose ?: meta.previousClose ?: price
                        val change = if (prev > 0) (price - prev) / prev * 100.0 else 0.0
                        HeatmapItem(
                            symbol = stock.symbol.removeSuffix(".DE"),
                            name = stock.name,
                            price = price,
                            change24h = change,
                            marketCap = stock.capUsd,
                            currency = meta.currency ?: "USD"
                        )
                    } catch (e: Exception) { null }
                }
            }
            jobs.awaitAll().filterNotNull()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}
