package com.capstone.veggievision.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.capstone.veggievision.data.Repository
import com.capstone.veggievision.data.remote.response.ArticlesItem

class HomeViewModel(private val repository: Repository) : ViewModel() {
    fun getArticles(): LiveData<PagingData<ArticlesItem>> {
        return repository.getArticles().cachedIn(viewModelScope)
    }
}