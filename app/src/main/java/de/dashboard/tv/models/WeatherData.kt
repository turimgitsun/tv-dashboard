package de.dashboard.tv.models

data class WeatherData(
    val time: String,
    val temp: String,
    val icon: String,
    val condition: String,
    val precipitation: String
)
