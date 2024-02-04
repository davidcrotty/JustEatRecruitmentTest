package com.example.justeatrecruitmenttest

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.justeatrecruitmenttest.domain.RestuarantRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PostCodeViewModel(private val repository: RestuarantRepository) : ViewModel() {

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
                repository.getRestuarants().onSuccess { resturants ->
                    val listItems = resturants.map {
                      ResturantModel(
                          name = it.name,
                          rating = it.rating,
                          typesOfFood = it.foodTypes.map { it.type }.reduce { acc, s -> "$acc, $s" }
                      )
                    }
                    _uiState.value = PostCodeUIState.Success(listItems)
                }.onFailure {
                    _uiState.value = PostCodeUIState.Error("Unable to fetch resturants")
                }
            } else {
                _uiState.value = PostCodeUIState.Error("invalid Postcode")
            }
        }
    }

}

sealed class PostCodeUIState {

    object Initial : PostCodeUIState()
    object Loading : PostCodeUIState()
    class Error(val message: String) : PostCodeUIState()

    class Success(val result: List<ResturantModel>) : PostCodeUIState()
}

data class ResturantModel(val name: String, val rating: Int, val typesOfFood: String)

@JvmInline
value class PostCode(val text: String)