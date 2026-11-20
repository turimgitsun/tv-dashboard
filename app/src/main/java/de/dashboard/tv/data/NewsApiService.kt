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