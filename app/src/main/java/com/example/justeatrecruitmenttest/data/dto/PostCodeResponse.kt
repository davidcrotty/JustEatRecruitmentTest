package com.example.justeatrecruitmenttest.data.dto

import com.google.gson.annotations.SerializedName

class PostCodeResponseDTO {
    @SerializedName("Restaurants")
    var resturants: List<ResturantsDTO>? = null
}

class ResturantsDTO(
    @SerializedName("Name")
    var name: String? = null,
    @SerializedName("RatingStars")
    var ratingStars: Double? = null,
    @SerializedName("Cuisines")
    var cuisines: List<CusinesDTO>,
    @SerializedName("IsOpenNow")
    var isOpenNow: Boolean,
    @SerializedName("LogoUrl")
    var logoUrl: String
)

class CusinesDTO(
    @SerializedName("Name")
    var name: String? = null
)