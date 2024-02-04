package com.example.justeatrecruitmenttest.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.justeatrecruitmenttest.domain.RestuarantRepository
import com.example.justeatrecruitmenttest.frameworks.LocationFetcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostCodeViewModel(private val repository: RestuarantRepository,
    private val locationFetcher: LocationFetcher) : ViewModel() {

    private val postCodePattern =
        """([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\s?[0-9][A-Za-z]{2})"""

    val uiState: StateFlow<PostCodeUIState>
        get() = _uiState

    private val _uiState: MutableStateFlow<PostCodeUIState> =
        MutableStateFlow(PostCodeUIState.Initial)

    fun searchPostCode(postcode: PostCode) {
        viewModelScope.launch {
            if (postcode.text.matches(Regex(postCodePattern))) {
                // API call
                _uiState.value = PostCodeUIState.Loading
                repository.getRestuarants(postcode).onSuccess { resturants ->
                    val listItems = resturants.map {
                      ResturantModel(
                          name = it.name,
                          rating = it.rating.toString(),
                          typesOfFood = it.foodTypes.map { it.type }.reduce { acc, s -> "$acc, $s" },
                          logoUrl = it.logoUrl
                      )
                    }
                    _uiState.value = PostCodeUIState.Success(listItems, postcode)
                }.onFailure {
                    _uiState.value = PostCodeUIState.Error("Unable to fetch resturants")
                }
            } else {
                _uiState.value = PostCodeUIState.Error("Invalid postcode")
            }
        }
    }

    fun requestLocation() {
        _uiState.value = PostCodeUIState.PostCodeLocateLoading
        locationFetcher.requestLocation {
            if (it.isSuccess) {
                val postCode = PostCode(it.getOrNull()?.text.orEmpty())
                _uiState.value = PostCodeUIState.PostCodeLocateSuccess(postCode = postCode)
                searchPostCode(postCode)
            }
        }
    }

}

sealed class PostCodeUIState {

    object Initial : PostCodeUIState()
    object Loading : PostCodeUIState()
    class Error(val message: String) : PostCodeUIState()

    object PostCodeLocateLoading : PostCodeUIState()
    class PostCodeLocateSuccess(val postCode: PostCode): PostCodeUIState()

    class Success(val resturants: List<ResturantModel>, val searched: PostCode) : PostCodeUIState()
}

data class ResturantModel(val name: String, val rating: String, val typesOfFood: String, val logoUrl: String)

@JvmInline
value class PostCode(val text: String)