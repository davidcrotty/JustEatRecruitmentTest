package com.example.justeatrecruitmenttest

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.justeatrecruitmenttest.di.PostCodeModule
import com.example.justeatrecruitmenttest.frameworks.LocationFetcher
import com.example.justeatrecruitmenttest.ui.theme.JustEatRecruitmentTestTheme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.Exception
import java.util.concurrent.Executors

class MainActivity : ComponentActivity(), LocationFetcher {

    private val module = PostCodeModule()
    private val geoCoder by lazy { Geocoder(this) }

    private val postCodeViewModel by lazy {
        ViewModelProvider(
            this,
            module.viewModelFactory(this)
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

    override fun requestLocation(callback: (Result<PostCode>) -> Unit) {
        val client = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 1000)
            .setWaitForAccurateLocation(false)
            .setMinUpdateIntervalMillis(1000)
            .setMaxUpdateDelayMillis(5000)
            .build()

        client.requestLocationUpdates(request, Executors.newSingleThreadExecutor(), object :
            LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                lifecycleScope.launch(Dispatchers.Default) {
                    delay(2000)
                    val lastLocation = locationResult.lastLocation
                    if (lastLocation != null) {
                        val result = geoCoder.getFromLocation(
                            lastLocation.latitude,
                            lastLocation.longitude,
                            1
                        )
                        withContext(Dispatchers.Main) {
                            if (result?.isEmpty() == true) {
                                callback(Result.failure<PostCode>(Exception("Postcode not found")))
                            } else {
                                val firstAddress = result?.first()
                                val postCode = firstAddress?.postalCode
                                if (postCode == null) {
                                    callback(Result.failure<PostCode>(Exception("No address found")))
                                } else {
                                    callback(Result.success(PostCode(postCode)))
                                }
                            }
                        }
                    }
                }
                client.removeLocationUpdates(this)
            }
        })
    }
}

@Composable
fun PostCodeScreen(viewModel: PostCodeViewModel) {

    @Composable
    fun PostCodeForm(modifier: Modifier, value: String, textChanged: (String) -> Unit) {
        TextField(modifier = modifier, value = value, onValueChange = textChanged)
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
                    Text(text = item.name, Modifier.padding(4.dp))
                    Text(text = "Rating: ${item.rating}", Modifier.padding(4.dp))
                    Text(text = "Food types: ${item.typesOfFood}", Modifier.padding(4.dp))
                }
            }
        }
    }

    val state by viewModel.uiState.collectAsState()
    val results = remember { mutableStateOf<List<ResturantModel>?>(null) }
    val postCode = remember { mutableStateOf("") }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { locationResultGranted ->
            if (locationResultGranted) {
                viewModel.requestLocation()
            }
        })

    Column(Modifier.padding(8.dp)) {
        Text(modifier = Modifier.padding(8.dp), text = stringResource(id = R.string.postcode_label))
        Row(verticalAlignment = Alignment.CenterVertically) {
            PostCodeForm(
                Modifier
                    .padding(8.dp)
                    .weight(1f), postCode.value
            ) {
                postCode.value = it
            }
            when (state) {
                is PostCodeUIState.PostCodeLocateLoading -> {
                    CircularProgressIndicator()
                }
                else -> {
                    IconButton(onClick = {
                        launcher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                    }) {
                        Image(
                            painter = painterResource(R.drawable.my_location),
                            contentDescription = "find location"
                        )
                    }
                }
            }
        }
        Button(
            onClick = { viewModel.searchPostCode(PostCode(postCode.value)) },
            enabled = postCode.value.isNotEmpty() && state != PostCodeUIState.PostCodeLocateLoading
        ) {
            Text(stringResource(id = R.string.list_restaurants))
        }

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
                Text(text = "Search results for: ${result.searched.text}", Modifier.padding(4.dp))
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

