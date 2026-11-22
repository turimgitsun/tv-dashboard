package de.dashboard.tv.data

import retrofit2.http.GET
import retrofit2.http.Query

interface RssApiService {
    @GET("v1/api.json")
    suspend fun getFeed(@Query("rss_url") rssUrl: String): RssResponse
}

data class RssResponse(
    val status: String?,
    val items: List<RssItem>?
)

data class RssItem(
    val title: String?,
    val pubDate: String?,
    val link: String?,
    val author: String?,
    val categories: List<String>?
)