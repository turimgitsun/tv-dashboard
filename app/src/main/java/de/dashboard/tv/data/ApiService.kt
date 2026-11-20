package de.dashboard.tv.data

import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("v8/finance/chart/{symbol}")
    suspend fun getStockChart(@Path("symbol") symbol: String): StockChartResponse
}

data class StockChartResponse(
    val chart: ChartData
)

data class ChartData(
    val result: List<Result>
)

data class Result(
    val meta: Meta
)

data class Meta(
    val regularMarketPrice: Double? = null,
    val chartPreviousClose: Double? = null,
    val previousClose: Double? = null,
    val regularMarketChangePercent: Double? = null
)