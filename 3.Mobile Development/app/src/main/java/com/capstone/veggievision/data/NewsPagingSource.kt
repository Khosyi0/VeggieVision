package com.capstone.veggievision.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.capstone.veggievision.BuildConfig
import com.capstone.veggievision.data.remote.response.ArticlesItem
import com.capstone.veggievision.data.remote.retrofit.ApiService

class NewsPagingSource(
    private val apiService: ApiService
) : PagingSource<Int, ArticlesItem>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ArticlesItem> {
        return try {
            val position = params.key ?: 1
            val response = apiService.getNews(position, params.loadSize)
            LoadResult.Page(
                data = response.articles,
                prevKey = if (position == 1) null else position - 1,
                nextKey = if (response.articles.isEmpty()) null else position + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ArticlesItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}