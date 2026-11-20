# TV Dashboard App – Sony Android TV

Ein Android-TV-App, die dein Dashboard auf dem Sony TV anzeigt:
- **Aktien & ETFs** mit Kursen und Änderungen (Yahoo Finance API)
- **Wetter Berlin** mit Stundenvorhersage (OpenWeatherMap API – optional)
- **News** mit Schlagzeilen, Quelle und Uhrzeit (RSS / NewsAPI – optional)

## Projektstruktur

```
tv-dashboard/
├── app/
│   ├── build.gradle
│   ├── AndroidManifest.xml
│   └── src/main/
│       ├── java/de/dashboard/tv/
│       │   ├── MainActivity.kt
│       │   ├── data/
│       │   │   ├── ApiService.kt
│       │   │   ├── RetrofitClient.kt
│       │   │   └── StockRepository.kt
│       │   ├── models/
│       │   │   ├── StockQuote.kt
│       │   │   ├── WeatherData.kt
│       │   │   └── NewsItem.kt
│       │   └── ui/
│       │       ├── StocksFragment.kt
│       │       ├── WeatherFragment.kt
│       │       └── NewsFragment.kt
│       └── res/
│           ├── layout/
│           │   ├── activity_main.xml
│           │   ├── fragment_stocks.xml
│           │   ├── fragment_weather.xml
│           │   ├── fragment_news.xml
│           │   ├── item_stock.xml
│           │   ├── item_weather.xml
│           │   └── item_news.xml
│           └── values/
│               └── styles.xml
```

## Anforderungen
- Android TV / Google TV (Sony Bravia)
- minSdk 24, targetSdk 33
- Leanback-Kategorie im Manifest für TV-Integration

## Build & Install

### 1. APK bauen (Android Studio)
1. Projekt in Android Studio importieren (`tv-dashboard/`)
2. `Build → Generate Signed Bundle / APK` → APK signieren
3. `debug` APK reicht zum Testen: `./gradlew assembleDebug`

### 2. Auf Sony TV installieren

**Methode A: ADB (Wireless)**
```bash
adb connect <TV-IP>:5555
adb install app/build/outputs/apk/debug/app-debug.apk
```

**Methode B: USB / Dateimanager**
- APK auf USB-Stick kopieren
- Dateimanager-App auf dem TV installieren (z. B. aus dem Play Store)
- APK über den Dateimanager tippen → Installieren

**Methode C: Developer Options**
- `Einstellungen → Über → Build-Nummer` 7× tippen
- `Einstellungen → Apps → Sondererlaubnisse → Install unknown apps` freigeben

## Datenquellen

| Tab | API | Key |
|-----|-----|-----|
| Aktien | Yahoo Finance API (v8/finance/chart) | Kostenlos, kein Key |
| Wetter | OpenWeatherMap API | Kostenlos (Registrierung) |
| News | RSS-Feed / NewsAPI | Optional |

### Yahoo Finance API Beispiel
```
GET https://query1.finance.yahoo.com/v8/finance/chart/AAPL
```
Returns: `chart.result[0].meta.regularMarketPrice`, `chartPreviousClose`

### OpenWeatherMap API Beispiel
```
GET https://api.openweathermap.org/data/2.5/weather?q=Berlin&appid=YOUR_API_KEY
```

### NewsAPI Beispiel
```
GET https://newsapi.org/v2/top-headlines?country=de&category=business
```

## Layout-Anpassung
Die App ist landscape-orientiert und nutzt `ViewBinding` + `FragmentStateAdapter` für das Tab-System (Aktien / Wetter / News).

## Nächste Schritte
- [ ] OpenWeatherMap API integrieren für echte Wetterdaten
- [ ] NewsAPI oder RSS für echte Nachrichten
- [ ] Watchlist-konfigurierbar machen (API-Symbol-Liste)
- [ ] Kursdiagramme (MPAndroidChart) für kleine Kurscharts
- [ ] Auto-Refresh-Intervall (z. B. alle 5 Minuten)