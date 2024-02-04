package com.example.justeatrecruitmenttest.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.justeatrecruitmenttest.PostCodeViewModel
import com.example.justeatrecruitmenttest.data.RestaurantRepositoryImpl
import com.example.justeatrecruitmenttest.data.RestuarantService
import com.example.justeatrecruitmenttest.domain.RestuarantRepository
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create

class PostCodeModule {
    fun repo(): RestuarantRepository = RestaurantRepositoryImpl(restuarantService())

    private fun restuarantService(): RestuarantService {
        return Retrofit.Builder()
            .baseUrl("https://uk.api.just-eat.io")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(RestuarantService::class.java)
    }

    fun viewModelFactory() = Factory(repo())

    class Factory(private val repo: RestuarantRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PostCodeViewModel(repo) as T
        }
    }
}