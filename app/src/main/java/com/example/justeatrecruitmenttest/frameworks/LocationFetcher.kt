package com.example.justeatrecruitmenttest.frameworks

import com.example.justeatrecruitmenttest.PostCode

interface LocationFetcher {
    fun requestLocation(callback: (Result<PostCode>) -> Unit)
}