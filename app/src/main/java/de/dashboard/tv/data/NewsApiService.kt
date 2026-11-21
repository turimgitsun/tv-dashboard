package de.dashboard.tv.data

import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("top-headlines")
    suspend fun getTopHeadlines(
        @Query("country") country: String = "de",
        @Query("category") category: String = "business",
        @Query("apiKey") apiKey: String
    ): NewsApiResponse
}

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