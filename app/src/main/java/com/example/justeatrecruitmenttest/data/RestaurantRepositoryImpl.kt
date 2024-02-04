package com.example.justeatrecruitmenttest.data

import com.example.justeatrecruitmenttest.PostCode
import com.example.justeatrecruitmenttest.domain.RestuarantRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

class RestaurantRepositoryImpl(private val resturantService: RestuarantService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO) : RestuarantRepository {
    override suspend fun getRestuarants(postcode: PostCode): Result<List<RestuarantRepository.ResturantEntity>> {
        return withContext(dispatcher) {
            try {
                val response = resturantService.restuarantsByPostcode(postcode.text)
                if (response.isSuccessful) {
                    val resturants = response.body()?.resturants?.mapNotNull {
                        RestuarantRepository.ResturantEntity(
                            name = it.name.orEmpty(),
                            rating = it.ratingStars ?: 0.0,
                            foodTypes = it.cuisines.map { RestuarantRepository.FoodType(it.name.orEmpty()) }
                        )
                    }
                    Result.success(resturants.orEmpty())
                } else {
                    Result.failure(Exception("API response not successful"))
                }
            } catch (e: Exception) {
              Result.failure(e)
            }
        }
    }
}