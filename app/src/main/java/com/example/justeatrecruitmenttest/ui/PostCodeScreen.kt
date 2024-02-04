package com.example.justeatrecruitmenttest.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.justeatrecruitmenttest.R
import com.example.justeatrecruitmenttest.di.PostCodeModule
import com.example.justeatrecruitmenttest.frameworks.LocationFetcher
import com.example.justeatrecruitmenttest.presentation.PostCode
import com.example.justeatrecruitmenttest.presentation.PostCodeUIState
import com.example.justeatrecruitmenttest.presentation.PostCodeViewModel
import com.example.justeatrecruitmenttest.presentation.ResturantModel

@Composable
fun PostCodeScreen(viewModel: PostCodeViewModel) {

    @Composable
    fun PostCodeForm(modifier: Modifier, state: PostCodeUIState, value: String, postCode: MutableState<String>, textChanged: (String) -> Unit) {
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
            onResult = { locationResultGranted ->
                if (locationResultGranted) {
                    viewModel.requestLocation()
                }
            })

        Column {
            Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.postcode_label))
            TextField(value = value, onValueChange = textChanged)
            when (state) {
                is PostCodeUIState.PostCodeLocateLoading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    TextButton(onClick = { viewModel.requestLocation() }) {
                        Text(text = "auto detect outcode")
                    }
                }
            }
            Button(
                onClick = { viewModel.searchPostCode(PostCode(postCode.value)) },
                enabled = postCode.value.isNotEmpty() && state != PostCodeUIState.PostCodeLocateLoading
            ) {
                Text(stringResource(id = R.string.list_restaurants))
            }
        }

    }

    @Composable
    fun ResturantList(result: List<ResturantModel>) {
        LazyColumn(state = rememberLazyListState()) {
            items(result.size) { index ->
                val item = result[index]
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(item.logoUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = stringResource(R.string.logo_description),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .padding(8.dp)
                        )
                        Column {
                            Text(text = item.name, Modifier.padding(4.dp))
                            Text(text = "Rating: ${item.rating}", Modifier.padding(4.dp))
                            Text(text = "Food types: ${item.typesOfFood}", Modifier.padding(4.dp))
                        }
                    }
                }
            }
        }
    }

    val state by viewModel.uiState.collectAsState()
    val results = remember { mutableStateOf<List<ResturantModel>?>(null) }
    val postCode = remember { mutableStateOf("") }

    Column(Modifier.padding(8.dp)) {
        PostCodeForm(Modifier, state = state, value = postCode.value, postCode = postCode, textChanged = {
            postCode.value = it
        })
        when (state) {
            is PostCodeUIState.Error -> {
                Text(
                    (state as PostCodeUIState.Error).message,
                    style = TextStyle(color = Color.Red),
                    modifier = Modifier.padding(8.dp)
                )
                results.value?.let {
                    ResturantList(it)
                }
            }

            is PostCodeUIState.Loading -> {
                CircularProgressIndicator()
            }

            is PostCodeUIState.Success -> {
                val result = (state as PostCodeUIState.Success)
                results.value = result.resturants
                Text(text = "Open restaurant search results for: ${result.searched.text}", Modifier.padding(4.dp))
                ResturantList(result.resturants)
            }

            is PostCodeUIState.PostCodeLocateSuccess -> {
                postCode.value = (state as PostCodeUIState.PostCodeLocateSuccess).postCode.text
                results.value?.let {
                    ResturantList(it)
                }
            }
            else -> {
                results.value?.let {
                    ResturantList(it)
                }
            }
        }
    }
}

@Composable
@Preview
fun PostCodePreview() {
    PostCodeScreen(PostCodeViewModel(PostCodeModule().repo(), object : LocationFetcher {
        override fun requestLocation(callback: (Result<PostCode>) -> Unit) {
            callback(Result.success(PostCode("BS378UL")))
        }
    }))
}