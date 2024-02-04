package com.example.justeatrecruitmenttest.data

import com.example.justeatrecruitmenttest.data.dto.PostCodeResponseDTO
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface RestuarantService {
    @GET("/restaurants/bypostcode/{postcode}")
    suspend fun restuarantsByPostcode(@Path("postcode")postcode: String): Response<PostCodeResponseDTO>
}