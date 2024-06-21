package com.capstone.veggievision.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.capstone.veggievision.data.remote.response.ArticlesItem
import com.capstone.veggievision.data.remote.response.ScanResponse
import com.capstone.veggievision.data.remote.retrofit.ApiConfig
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository {
    private val apiService = ApiConfig.getApiService()
    fun getArticles(): LiveData<PagingData<ArticlesItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                NewsPagingSource(apiService)
            }
        ).liveData
    }

    private val _scanResult = MutableLiveData<ScanResponse?>()
    val scanResult: LiveData<ScanResponse?> = _scanResult

    fun uploadImage(image: MultipartBody.Part) {
        val client = ApiConfig.getApiService().uploadImage(image)
        client.enqueue(object : Callback<ScanResponse> {
            override fun onResponse(call: Call<ScanResponse>, response: Response<ScanResponse>) {
                if (response.isSuccessful) {
                    _scanResult.postValue(response.body())
                } else {
                    _scanResult.postValue(null)
                }
            }

            override fun onFailure(call: Call<ScanResponse>, t: Throwable) {
                _scanResult.postValue(null)
            }
        })
    }
}