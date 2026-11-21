package de.dashboard.tv.data

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface CoinGeckoService {
    @GET("coins/markets")
    suspend fun getMarkets(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 20,
        @Query("page") page: Int = 1,
        @Query("sparkline") sparkline: Boolean = false
    ): List<CoinMarketData>
}

data class CoinMarketData(
    val id: String?,
    val symbol: String?,
    val name: String?,
    @SerializedName("current_price") val currentPrice: Double?,
    @SerializedName("market_cap") val marketCap: Double?,
    @SerializedName("price_change_percentage_24h") val priceChangePercentage24h: Double?
)
