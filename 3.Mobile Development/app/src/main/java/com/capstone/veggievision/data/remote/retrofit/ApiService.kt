package com.capstone.veggievision.data.remote.retrofit

import com.capstone.veggievision.data.remote.response.NewsResponse
import com.capstone.veggievision.data.remote.response.ScanResponse
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @GET("/articles")
    suspend fun getNews(
        @Query("page") page: Int,
        @Query("pageSize") pageSize: Int
    ): NewsResponse

    @Multipart
    @POST("/scan")
    fun uploadImage(
        @Part image: MultipartBody.Part
    ): Call<ScanResponse>
}