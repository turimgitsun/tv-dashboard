package de.dashboard.tv

import android.content.res.ColorStateList
import android.os.Bundle
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import de.dashboard.tv.ui.NewsFragment
import de.dashboard.tv.ui.PortfolioFragment
import de.dashboard.tv.ui.WeatherFragment

class MainActivity : AppCompatActivity() {

    private lateinit var container: FrameLayout
    private lateinit var navPortfolio: MaterialButton
    private lateinit var navWeather: MaterialButton
    private lateinit var navNews: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container = findViewById(R.id.fragmentContainer)
        navPortfolio = findViewById(R.id.navPortfolio)
        navWeather = findViewById(R.id.navWeather)
        navNews = findViewById(R.id.navNews)

        navPortfolio.setOnClickListener { select(navPortfolio, PortfolioFragment()) }
        navWeather.setOnClickListener { select(navWeather, WeatherFragment()) }
        navNews.setOnClickListener { select(navNews, NewsFragment()) }

        if (savedInstanceState == null) select(navPortfolio, PortfolioFragment())
    }

    private fun select(button: MaterialButton, fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()

        val active = button
        listOf(navPortfolio, navWeather, navNews).forEach { b ->
            val selected = b === active
            b.isSelected = selected
            b.backgroundTintList = ColorStateList.valueOf(
                if (selected) getColor(R.color.accent) else getColor(R.color.surface2)
            )
            b.setTextColor(
                if (selected) getColor(android.R.color.black) else getColor(R.color.textPrimary)
            )
        }
    }
}