package de.dashboard.tv.data

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverter
import retrofit2.converter.scalars.ScalarsConverter

object RetrofitClient {
    private const val BASE_URL = "https://query1.finance.yahoo.com/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}