package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.models.NewsItem

class NewsFragment : Fragment() {

    private lateinit var adapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        adapter = NewsAdapter(emptyList()) {}
        recyclerView.adapter = adapter
        loadNews()

        return view
    }

    private fun loadNews() {
        val sample = listOf(
            NewsItem("DAX steigt auf Rekordhöhe", "Reuters", "18:30", "Wirtschaft"),
            NewsItem("Neue KI-Modelle vorgestellt", "TechCrunch", "18:15", "Technologie"),
            NewsItem("EZB hält Leitzinsen stabil", "Bloomberg", "18:00", "Wirtschaft"),
            NewsItem("Tesla plant neue Fabrik", "Handelsblatt", "17:45", "Technologie"),
            NewsItem("Energiepreise fallen weiter", "FAZ", "17:30", "Wirtschaft")
        )
        adapter.submitList(sample)
    }

    class NewsAdapter(
        private val items: List<NewsItem>,
        private val onClick: (NewsItem) -> Unit
    ) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

        private var data = items

        fun submitList(newItems: List<NewsItem>) {
            data = newItems
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
            return NewsViewHolder(view)
        }

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            holder.bind(data[position])
        }

        override fun getItemCount() = data.size

        class NewsViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            fun bind(news: NewsItem) {
                view.findViewById<android.widget.TextView>(R.id.headlineText).text = news.headline
                view.findViewById<android.widget.TextView>(R.id.sourceText).text = news.source
                view.findViewById<android.widget.TextView>(R.id.timeText).text = news.time
                view.findViewById<android.widget.TextView>(R.id.categoryText).text = news.category
            }
        }
    }
}