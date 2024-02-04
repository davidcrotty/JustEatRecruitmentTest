package com.example.justeatrecruitmenttest.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.justeatrecruitmenttest.PostCodeViewModel
import com.example.justeatrecruitmenttest.data.RestaurantRepositoryImpl
import com.example.justeatrecruitmenttest.domain.RestuarantRepository

class PostCodeModule {
    fun repo(): RestuarantRepository = RestaurantRepositoryImpl()

    fun viewModelFactory() = Factory(repo())

    class Factory(private val repo: RestuarantRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PostCodeViewModel(repo) as T
        }
    }
}