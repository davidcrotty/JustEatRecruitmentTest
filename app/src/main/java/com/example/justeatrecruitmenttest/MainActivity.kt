package com.example.justeatrecruitmenttest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.example.justeatrecruitmenttest.di.PostCodeModule
import com.example.justeatrecruitmenttest.ui.theme.JustEatRecruitmentTestTheme

class MainActivity : ComponentActivity() {

    private val module = PostCodeModule()

    private val postCodeViewModel by lazy {
        ViewModelProvider(
            this,
            module.viewModelFactory()
        ).get(PostCodeViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JustEatRecruitmentTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostCodeScreen(postCodeViewModel)
                }
            }
        }
    }
}

@Composable
fun PostCodeScreen(viewModel: PostCodeViewModel) {

    @Composable
    fun PostCodeForm(modifier: Modifier, value: String, textChanged: (String) -> Unit) {
        Box(Modifier.fillMaxWidth()) {
            TextField(modifier = modifier.fillMaxWidth(), value = value, onValueChange = textChanged)
        }
    }

    val state by viewModel.uiState.collectAsState()

    val postCode = remember { mutableStateOf("") }

    Column(Modifier.padding(8.dp)) {
        Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.postcode_label))
        PostCodeForm(Modifier.padding(8.dp), postCode.value) {
            postCode.value = it
        }
        Button(onClick = { viewModel.searchPostCode(PostCode(postCode.value)) }, enabled = postCode.value.isNotEmpty()) {
            Text(stringResource(id = R.string.list_restaurants))
        }

        when (state) {
            is PostCodeUIState.Error -> {
                Text((state as PostCodeUIState.Error).message, style = TextStyle(color = Color.Red), modifier = Modifier.padding(8.dp))
            }
            is PostCodeUIState.Loading -> {
                CircularProgressIndicator()
            }
            is PostCodeUIState.Success -> {
                val result = (state as PostCodeUIState.Success).result
                LazyColumn(state = rememberLazyListState()) {
                    items(result.size) { index ->
                        val item = result[index]
                        Card(Modifier.fillMaxWidth().padding(8.dp)) {
                            Text(text = item.name, Modifier.padding(4.dp))
                            Text(text = "Rating: ${item.rating}", Modifier.padding(4.dp))
                            Text(text = "Food types: ${item.typesOfFood}", Modifier.padding(4.dp))
                        }
                    }
                }
            }
            else -> {

            }
        }
    }
}

@Composable
@Preview
fun PostCodePreview() {
    PostCodeScreen(PostCodeViewModel(PostCodeModule().repo()))
}

