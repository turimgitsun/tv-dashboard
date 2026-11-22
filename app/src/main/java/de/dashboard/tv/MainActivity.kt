package de.dashboard.tv

import android.content.res.ColorStateList
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import de.dashboard.tv.data.WeatherRepository
import de.dashboard.tv.ui.HeatmapFragment
import de.dashboard.tv.ui.NewsFragment
import de.dashboard.tv.ui.OverviewFragment
import de.dashboard.tv.ui.PortfolioFragment
import de.dashboard.tv.ui.SettingsFragment
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private lateinit var navOverview: MaterialButton
    private lateinit var navMarkets: MaterialButton
    private lateinit var navWatchlist: MaterialButton
    private lateinit var navNews: MaterialButton
    private lateinit var navSettings: MaterialButton
    private lateinit var headerWeather: TextView
    private lateinit var headerDateTime: TextView
    private lateinit var footerStatus: TextView

    private val navButtons by lazy { listOf(navOverview, navMarkets, navWatchlist, navNews, navSettings) }
    private val clockHandler = Handler(Looper.getMainLooper())
    private val clock = object : Runnable {
        override fun run() {
            val fmt = SimpleDateFormat("EEE dd.MM. · HH:mm", Locale.GERMANY)
            headerDateTime.text = fmt.format(Date())
            clockHandler.postDelayed(this, 30_000)
            footerStatus.text = "Datenstand: " + SimpleDateFormat("HH:mm", Locale.GERMANY).format(Date())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container = findViewById(R.id.fragmentContainer)
        navOverview = findViewById(R.id.navOverview)
        navMarkets = findViewById(R.id.navMarkets)
        navWatchlist = findViewById(R.id.navWatchlist)
        navNews = findViewById(R.id.navNews)
        navSettings = findViewById(R.id.navSettings)
        headerWeather = findViewById(R.id.headerWeather)
        headerDateTime = findViewById(R.id.headerDateTime)
        footerStatus = findViewById(R.id.footerStatus)

        navOverview.setOnClickListener { select(navOverview, OverviewFragment()) }
        navMarkets.setOnClickListener { select(navMarkets, HeatmapFragment()) }
        navWatchlist.setOnClickListener { select(navWatchlist, PortfolioFragment()) }
        navNews.setOnClickListener { select(navNews, NewsFragment()) }
        navSettings.setOnClickListener { select(navSettings, SettingsFragment()) }

        clockHandler.post(clock)
        loadHeaderWeather()

        if (savedInstanceState == null) select(navOverview, OverviewFragment())
    }

    private fun loadHeaderWeather() {
        lifecycleScope.launch {
            val w = WeatherRepository.currentBerlin()
            headerWeather.text = if (w != null) "Berlin  ${w.temp}  ${w.icon}" else "Berlin  –"
        }
    }

    private fun select(button: MaterialButton, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        val active = button
        navButtons.forEach { b ->
            val selected = b === active
            b.isSelected = selected
            b.backgroundTintList = android.content.res.ColorStateList.valueOf(
                if (selected) getColor(R.color.accent) else getColor(R.color.surface2)
            )
            b.setTextColor(
                if (selected) getColor(android.R.color.black) else getColor(R.color.textPrimary)
            )
        }
        footerStatus.text = "Datenstand: " + SimpleDateFormat("HH:mm", Locale.GERMANY).format(Date())
    }

    override fun onDestroy() {
        super.onDestroy()
        clockHandler.removeCallbacks(clock)
    }
}
