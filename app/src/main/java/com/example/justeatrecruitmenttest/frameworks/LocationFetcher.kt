package com.example.justeatrecruitmenttest.frameworks

import com.example.justeatrecruitmenttest.presentation.PostCode

interface LocationFetcher {
    fun requestLocation(callback: (Result<PostCode>) -> Unit)
}