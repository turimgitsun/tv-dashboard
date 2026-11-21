package de.dashboard.tv.data

import de.dashboard.tv.models.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NewsRepository {

    private const val RSS_URL = "https://www.tagesschau.de/xml/rss2/"

    private val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.rss2json.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    suspend fun getTopHeadlines(): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            val response = api.getFeed(RSS_URL)
            response.items?.map { item ->
                NewsItem(
                    headline = item.title ?: "",
                    source = item.author?.takeIf { it.isNotBlank() } ?: "tagesschau",
                    time = item.pubDate?.replace(" GMT", "")?.take(16) ?: "",
                    category = item.categories?.firstOrNull() ?: "News"
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}