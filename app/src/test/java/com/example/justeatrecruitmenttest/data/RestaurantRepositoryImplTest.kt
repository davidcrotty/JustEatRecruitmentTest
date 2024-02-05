package com.example.justeatrecruitmenttest.data

import com.example.justeatrecruitmenttest.data.dto.CusinesDTO
import com.example.justeatrecruitmenttest.data.dto.PostCodeResponseDTO
import com.example.justeatrecruitmenttest.data.dto.ResturantsDTO
import com.example.justeatrecruitmenttest.domain.RestuarantRepository
import com.example.justeatrecruitmenttest.presentation.PostCode
import io.mockk.coEvery
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.runBlocking
import org.junit.Test
import retrofit2.Response
import java.lang.Exception

class RestaurantRepositoryImplTest {

    @Test
    fun fetchingOpenResturants() {

        val api = mockk<RestuarantService>() {
            coEvery { restuarantsByPostcode("BS378UL") } returns resturantAPIResponse()
        }

        val sut = RestaurantRepositoryImpl(
            api
        )

        val result = runBlocking {
            sut.getRestuarants(PostCode("BS378UL"))
        }

        assertEquals(openResturants(), result)
    }

    @Test
    fun fetchingOpenResturantFails() {
        val api = mockk<RestuarantService>() {
            coEvery { restuarantsByPostcode("BS378UL") } throws Exception()
        }

        val sut = RestaurantRepositoryImpl(
            api
        )

        val result = runBlocking {
            sut.getRestuarants(PostCode("BS378UL"))
        }

        assertEquals(true, result.isFailure)
    }

    private fun openResturants(): Result<List<RestuarantRepository.ResturantEntity>> {
        return Result.success(
            listOf(
                RestuarantRepository.ResturantEntity(
                    "Best Pizza",
                    5.64,
                    listOf(
                        RestuarantRepository.FoodType("Pizza"),
                        RestuarantRepository.FoodType("Alchohol")
                    ),
                    "http://cdn.com/bestpizza.gif"
                )
            )
        )
    }

    private fun resturantAPIResponse(): Response<PostCodeResponseDTO> {
        return Response.success(
            PostCodeResponseDTO(
                listOf(
                    ResturantsDTO(
                        name = "Best Pizza",
                        ratingStars = 5.64,
                        cuisines = listOf(
                            CusinesDTO("Pizza"),
                            CusinesDTO("Alchohol")
                        ),
                        isOpenNow = true,
                        logoUrl = "http://cdn.com/bestpizza.gif"
                    )
                )
            )
        )
    }
}