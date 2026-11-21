package de.dashboard.tv.data

import de.dashboard.tv.models.NewsItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NewsRepository {

    private const val BASE_URL = "https://newsapi.org/v2/"
    private const val API_KEY = "***"

    private val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NewsApiService::class.java)
    }

    suspend fun getTopHeadlines(): List<NewsItem> = withContext(Dispatchers.IO) {
        try {
            val response = api.getTopHeadlines(
                country = "de",
                category = "business",
                apiKey = "***"
            )
            response.articles.map { article ->
                NewsItem(
                    headline = article.title ?: "",
                    source = article.source.name ?: "News",
                    time = article.publishedAt?.take(16)?.replace("T", " ") ?: "",
                    category = "Business"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}