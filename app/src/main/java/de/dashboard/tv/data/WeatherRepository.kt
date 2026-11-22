package de.dashboard.tv.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

data class WeatherNow(
    val temp: String,
    val condition: String,
    val icon: String
)

interface OpenMeteoService {
    @GET("v1/forecast")
    suspend fun current(
        @Query("latitude") lat: Double,
        @Query("longitude") lon: Double,
        @Query("current") current: String
    ): OpenMeteoResponse
}

data class OpenMeteoResponse(val current: OpenMeteoCurrent?)
data class OpenMeteoCurrent(
    val temperature_2m: Double?,
    val weather_code: Int?
)

object WeatherRepository {
    private const val LAT = 52.52   // Berlin
    private const val LON = 13.41

    private val api: OpenMeteoService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.open-meteo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OpenMeteoService::class.java)
    }

    suspend fun currentBerlin(): WeatherNow? {
        return try {
            val resp = api.current(LAT, LON, "temperature_2m,weather_code")
            val cur = resp.current ?: return null
            val temp = cur.temperature_2m?.let { "${Math.round(it)}°" } ?: "–"
            val code = cur.weather_code ?: -1
            WeatherNow(temp = temp, condition = conditionOf(code), icon = iconOf(code))
        } catch (e: Exception) {
            null
        }
    }

    private fun iconOf(code: Int): String = when (code) {
        0 -> "☀️"
        1, 2 -> "🌤️"
        3 -> "☁️"
        45, 48 -> "🌫️"
        51, 53, 55, 56, 57 -> "🌦️"
        61, 63, 65, 66, 67 -> "🌧️"
        71, 73, 75, 77 -> "❄️"
        80, 81, 82 -> "🌧️"
        85, 86 -> "🌨️"
        95, 96, 99 -> "⛈️"
        else -> "🌡️"
    }

    private fun conditionOf(code: Int): String = when (code) {
        0 -> "Klar"
        1 -> "Überwiegend klar"
        2 -> "Teils bewölkt"
        3 -> "Bedeckt"
        45, 48 -> "Nebel"
        51, 53, 55 -> "Nieselregen"
        56, 57 -> "Gefrierender Niesel"
        61, 63, 65 -> "Regen"
        66, 67 -> "Gefrierender Regen"
        71, 73, 75 -> "Schnee"
        77 -> "Schneegriesel"
        80, 81, 82 -> "Schauer"
        85, 86 -> "Schneeschauer"
        95 -> "Gewitter"
        96, 99 -> "Gewitter mit Hagel"
        else -> "–"
    }
}
