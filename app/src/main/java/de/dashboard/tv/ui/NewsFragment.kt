package de.dashboard.tv.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.dashboard.tv.R
import de.dashboard.tv.data.NewsRepository
import de.dashboard.tv.models.NewsItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class NewsFragment : Fragment() {

    private lateinit var adapter: NewsAdapter
    private lateinit var progressBar: ProgressBar
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_news, container, false)

        progressBar = view.findViewById(R.id.progressBar)
        val rv = view.findViewById<RecyclerView>(R.id.recyclerView)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = NewsAdapter(emptyList())
        rv.adapter = adapter

        load()
        return view
    }

    private fun load() {
        progressBar.visibility = View.VISIBLE
        scope.launch {
            val items = NewsRepository.getTopHeadlines()
            progressBar.visibility = View.GONE
            adapter.submitList(items)
        }
    }

    class NewsAdapter(private var items: List<NewsItem>) :
        RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

        fun submitList(newItems: List<NewsItem>) {
            items = newItems
            notifyDataSetChanged()
        }

        override fun getItemCount() = items.size

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_news, parent, false)
            return NewsViewHolder(view)
        }

        override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
            holder.bind(items[position])
        }

        class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            fun bind(news: NewsItem) {
                itemView.findViewById<TextView>(R.id.headlineText).text = news.headline
                itemView.findViewById<TextView>(R.id.sourceText).text = news.source
                itemView.findViewById<TextView>(R.id.timeText).text = news.time
                itemView.findViewById<TextView>(R.id.categoryText).text = news.category
            }
        }
    }
}