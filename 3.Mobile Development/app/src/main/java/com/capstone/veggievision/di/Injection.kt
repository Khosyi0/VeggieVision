package com.capstone.veggievision.di

import com.capstone.veggievision.data.Repository

object Injection {
    fun provideRepository(): Repository {
        return Repository()
    }
}