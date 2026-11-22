package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import de.dashboard.tv.R
import de.dashboard.tv.data.WeatherRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherFragment : Fragment() {

    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        val progress = view.findViewById<ProgressBar>(R.id.progressBar)
        val icon = view.findViewById<TextView>(R.id.iconText)
        val temp = view.findViewById<TextView>(R.id.tempText)
        val condition = view.findViewById<TextView>(R.id.conditionText)
        val updated = view.findViewById<TextView>(R.id.updatedText)

        progress.visibility = View.VISIBLE
        scope.launch {
            val w = WeatherRepository.currentBerlin()
            progress.visibility = View.GONE
            if (w != null) {
                icon.text = w.icon
                temp.text = w.temp
                condition.text = w.condition
                updated.text = "Datenstand: " +
                    SimpleDateFormat("HH:mm", Locale.GERMANY).format(Date())
            } else {
                temp.text = "–"
                condition.text = "Wetter nicht verfügbar"
                updated.text = "Bitte Internetverbindung prüfen"
            }
        }
        return view
    }
}
