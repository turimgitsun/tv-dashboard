package de.dashboard.tv.data

data class AssetEntry(
    val symbol: String,
    val name: String,
    val isCrypto: Boolean
)

object AssetCatalog {

    val all: List<AssetEntry> = listOf(
        // Aktien (USA)
        AssetEntry("AAPL", "Apple", false),
        AssetEntry("MSFT", "Microsoft", false),
        AssetEntry("GOOGL", "Alphabet", false),
        AssetEntry("AMZN", "Amazon", false),
        AssetEntry("TSLA", "Tesla", false),
        AssetEntry("NVDA", "NVIDIA", false),
        AssetEntry("META", "Meta Platforms", false),
        AssetEntry("NFLX", "Netflix", false),
        // Aktien (Deutschland / Xetra)
        AssetEntry("SAP.DE", "SAP", false),
        AssetEntry("SIE.DE", "Siemens", false),
        AssetEntry("ALV.DE", "Allianz", false),
        AssetEntry("BMW.DE", "BMW", false),
        AssetEntry("VOW3.DE", "Volkswagen", false),
        AssetEntry("MBG.DE", "Mercedes-Benz", false),
        AssetEntry("BAS.DE", "BASF", false),
        // Crypto
        AssetEntry("BTC-USD", "Bitcoin", true),
        AssetEntry("ETH-USD", "Ethereum", true),
        AssetEntry("SOL-USD", "Solana", true),
        AssetEntry("ADA-USD", "Cardano", true),
        AssetEntry("DOGE-USD", "Dogecoin", true),
        AssetEntry("XRP-USD", "Ripple", true),
        AssetEntry("DOT-USD", "Polkadot", true),
        AssetEntry("AVAX-USD", "Avalanche", true)
    )

    fun nameOf(symbol: String): String =
        all.firstOrNull { it.symbol == symbol }?.name ?: symbol

    fun isCrypto(symbol: String): Boolean =
        all.firstOrNull { it.symbol == symbol }?.isCrypto ?: symbol.endsWith("-USD")
}