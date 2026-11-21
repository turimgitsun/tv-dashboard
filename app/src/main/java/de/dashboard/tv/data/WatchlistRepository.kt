package de.dashboard.tv.data

import android.content.Context

object WatchlistRepository {
    private const val PREFS = "watchlist"
    private const val KEY = "symbols"

    private val DEFAULT = listOf("AAPL", "MSFT", "SAP.DE", "BTC-USD", "ETH-USD")

    fun get(context: Context): List<String> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        return prefs.getStringSet(KEY, null)?.toList() ?: DEFAULT
    }

    fun save(context: Context, symbols: List<String>) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putStringSet(KEY, symbols.toSet())
            .apply()
    }
}