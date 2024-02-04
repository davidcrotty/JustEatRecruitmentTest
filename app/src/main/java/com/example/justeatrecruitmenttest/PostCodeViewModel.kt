package com.example.justeatrecruitmenttest

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PostCodeViewModel : ViewModel() {

    private val postCodePattern = """([Gg][Ii][Rr] 0[Aa]{2})|((([A-Za-z][0-9]{1,2})|(([A-Za-z][A-Ha-hJ-Yj-y][0-9]{1,2})|(([A-Za-z][0-9][A-Za-z])|([A-Za-z][A-Ha-hJ-Yj-y][0-9][A-Za-z]?))))\s?[0-9][A-Za-z]{2})"""

    val uiState: StateFlow<PostCodeUIState>
        get() = _uiState

    private val _uiState: MutableStateFlow<PostCodeUIState> = MutableStateFlow(PostCodeUIState.Initial)

    fun searchPostCode(postcode: PostCode) {
        if (postcode.text.matches(Regex(postCodePattern))) {
            // API call
            _uiState.value = PostCodeUIState.Loading
        } else {
            _uiState.value = PostCodeUIState.Error("invalid Postcode")
        }
    }

}

sealed class PostCodeUIState {

    object Initial: PostCodeUIState()
    object Loading: PostCodeUIState()
    class Error(val message: String): PostCodeUIState()

    class Success(val result: ResturantModel): PostCodeUIState()
}

data class ResturantModel(val name: String, val rating: Int, val typesOfFood: String)

@JvmInline
value class PostCode(val text: String)