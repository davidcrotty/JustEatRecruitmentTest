package com.example.justeatrecruitmenttest.domain

interface RestuarantRepository {

    data class ResturantEntity(val name: String, val rating: Int, val foodTypes: List<FoodType>)

    @JvmInline
    value class FoodType(val type: String)
    fun getRestuarants(): Result<List<ResturantEntity>>
}