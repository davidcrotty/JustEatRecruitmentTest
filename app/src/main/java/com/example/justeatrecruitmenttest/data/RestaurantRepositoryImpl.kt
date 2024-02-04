package com.example.justeatrecruitmenttest.data

import com.example.justeatrecruitmenttest.domain.RestuarantRepository

class RestaurantRepositoryImpl : RestuarantRepository {
    override fun getRestuarants(): Result<List<RestuarantRepository.ResturantEntity>> {
        return Result.success(
            listOf(
                RestuarantRepository.ResturantEntity(
                    name = "Dominos",
                    rating = 5,
                    foodTypes = listOf(RestuarantRepository.FoodType("pizza"))
                )
            )
        )
    }
}