package de.dashboard.tv.models

data class StockQuote(
    val symbol: String,
    val name: String,
    val price: Double,
    val change: String
)

data class WeatherData(
    val time: String,
    val temp: String,
    val icon: String,
    val condition: String,
    val precipitation: String
)

data class NewsItem(
    val headline: String,
    val source: String,
    val time: String,
    val category: String
)