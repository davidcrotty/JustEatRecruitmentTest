package com.example.justeatrecruitmenttest.domain

import com.example.justeatrecruitmenttest.PostCode

interface RestuarantRepository {

    data class ResturantEntity(val name: String, val rating: Double, val foodTypes: List<FoodType>)

    @JvmInline
    value class FoodType(val type: String)
    suspend fun getRestuarants(postcode: PostCode): Result<List<ResturantEntity>>
}