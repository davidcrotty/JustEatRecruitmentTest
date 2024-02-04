package com.example.justeatrecruitmenttest.domain

import com.example.justeatrecruitmenttest.presentation.PostCode

interface RestuarantRepository {

    data class ResturantEntity(val name: String, val rating: Double, val foodTypes: List<FoodType>, val logoUrl: String)

    @JvmInline
    value class FoodType(val type: String)
    suspend fun getRestuarants(postcode: PostCode): Result<List<ResturantEntity>>
}