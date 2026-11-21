package de.dashboard.tv.models

data class StockQuote(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: String,
    val changeValue: Double,
    val currency: String
)
