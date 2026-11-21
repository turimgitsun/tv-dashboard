package de.dashboard.tv.models

data class HeatmapItem(
    val symbol: String,
    val name: String,
    val price: Double,
    val change24h: Double,
    val marketCap: Double,
    val currency: String
)
