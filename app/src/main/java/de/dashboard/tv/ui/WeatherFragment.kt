package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.models.WeatherData

class WeatherFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: WeatherAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        adapter = WeatherAdapter(emptyList()) {}
        recyclerView.adapter = adapter

        loadWeather()

        return view
    }

    private fun loadWeather() {
        val sample = listOf(
            WeatherData("19:00", "18°", "☀️", "Sonnig", "0%"),
            WeatherData("20:00", "17°", "☀️", "Sonnig", "0%"),
            WeatherData("21:00", "16°", "🌙", "Klar", "0%"),
            WeatherData("22:00", "15°", "🌙", "Klar", "5%"),
            WeatherData("23:00", "14°", "🌙", "Klar", "5%")
        )
        adapter.submitList(sample)
    }

    class WeatherAdapter(
        private val items: List<WeatherData>,
        private val onClick: (WeatherData) -> Unit
    ) : RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

        private var data = items

        fun submitList(newItems: List<WeatherData>) {
            data = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_weather, parent, false)
            return WeatherViewHolder(view)
        }

        override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount() = data.size

        class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(weather: WeatherData) {
                itemView.findViewById<android.widget.TextView>(R.id.timeText).text = weather.time
                itemView.findViewById<android.widget.TextView>(R.id.tempText).text = weather.temp
                itemView.findViewById<android.widget.TextView>(R.id.iconText).text = weather.icon
                itemView.findViewById<android.widget.TextView>(R.id.conditionText).text = weather.condition
                itemView.findViewById<android.widget.TextView>(R.id.precipText).text = weather.precipitation
            }
        }
    }
}