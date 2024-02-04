package com.example.justeatrecruitmenttest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.justeatrecruitmenttest.ui.theme.JustEatRecruitmentTestTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JustEatRecruitmentTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PostCodeScreen()
                }
            }
        }
    }
}

@Composable
fun PostCodeScreen() {

    @Composable
    fun PostCodeForm(modifier: Modifier, value: String, textChanged: (String) -> Unit) {
        Box(Modifier.fillMaxWidth()) {
            TextField(modifier = modifier.fillMaxWidth(), value = value, onValueChange = textChanged)
        }
    }

    val postCode = remember { mutableStateOf("") }

    Column(Modifier.padding(8.dp)) {
        Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.postcode_label))
        PostCodeForm(Modifier.padding(8.dp), postCode.value) {
            postCode.value = it
        }
        Button(onClick = { /*TODO*/ }, enabled = postCode.value.isNotEmpty()) {
            Text("List Restuarants")
        }
    }
}

@Composable
@Preview
fun PostCodePreview() {
    PostCodeScreen()
}

