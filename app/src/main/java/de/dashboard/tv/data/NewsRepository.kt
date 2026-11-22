package de.dashboard.tv.data

import de.dashboard.tv.models.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NewsRepository {

    private const val API_KEY = "***"
    private const val RSS_URL = "https://www.tagesschau.de/xml/rss2/"

    private val newsApi: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    private val rssApi: RssApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.rss2json.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RssApiService::class.java)
    }

    suspend fun getTopHeadlines(): List<NewsItem> = withContext(Dispatchers.IO) {
        // 1) Try NewsAPI
        try {
            val response = newsApi.getTopHeadlines("de", "business", API_KEY)
            if (response.articles.isNotEmpty()) {
                return@withContext response.articles.map { article ->
                    NewsItem(
                        headline = article.title ?: "",
                        source = article.source.name ?: "News",
                        time = article.publishedAt?.take(16)?.replace("T", " ") ?: "",
                        category = "Business"
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // 2) Fallback: RSS via rss2json
        try {
            val response = rssApi.getFeed(RSS_URL)
            return@withContext response.items?.map { item ->
                NewsItem(
                    headline = item.title ?: "",
                    source = item.author?.takeIf { it.isNotBlank() } ?: "tagesschau",
                    time = item.pubDate?.replace(" GMT", "")?.take(16) ?: "",
                    category = item.categories?.firstOrNull() ?: "News"
                )
            } ?: emptyList()
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }
}