package de.dashboard.tv.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import de.dashboard.tv.models.NewsItem
import retrofit2.HttpException

object NewsRepository {

    private const val BASE_URL = "https://newsapi.org/v2/"
    private const val API_KEY = "YOUR_NEWSAPI_KEY_HERE"

    private val apiService: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    suspend fun getTopHeadlines(category: String = "business"): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getTopHeadlines(
                country = "de",
                category = category,
                apiKey = API_KEY
            )
            response.articles.map { article ->
                NewsItem(
                    headline = article.title ?: "",
                    source = article.source.name ?: "Unbekannt",
                    time = article.publishedAt?.take(16)?.replace("T", " ") ?: "",
                    category = category
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}

// --- API Models ---

data class NewsApiResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<NewsArticle>
)

data class NewsArticle(
    val title: String?,
    val url: String?,
    val urlToImage: String?,
    val source: NewsSource,
    val publishedAt: String?
)

data class NewsSource(
    val id: String?,
    val name: String?
)